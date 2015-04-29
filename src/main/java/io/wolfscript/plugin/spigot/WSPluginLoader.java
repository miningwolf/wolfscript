/*
 * WolfScript
 * Copyright (c) 2015 Mining Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package io.wolfscript.plugin.spigot;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import org.bukkit.plugin.PluginLoader;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import org.bukkit.Server;
import org.bukkit.event.EventException;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.TimedRegisteredListener;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPluginLoader;

import io.wolfscript.plugin.spigot.event.WSEvents;
import io.wolfscript.plugin.spigot.event.WSEventExecutor;

/**
 * Spigot / CraftBukkit / Bukkit Plugin Loader for WolfScript
 */
public class WSPluginLoader implements PluginLoader {
    
    final Server server;
    public static final Pattern[] fileFilters = new Pattern[] {
           Pattern.compile("^([^\\\\]*)(?:\\\\)*package.json$")
        };
    
    private HashSet<String> loadedplugins = new HashSet<String>();
      
    /**
     * This class is not meant to be constructed explicitly
     */
    public WSPluginLoader(Server instance) {
        server = instance;
    }
        
     /**
     * Loads the plugin contained in the specified file
     *
     * @param file File to attempt to load
     * @return Plugin that was contained in the specified file, or null if
     *     unsuccessful
     * @throws InvalidPluginException Thrown when the specified file is not a
     *     plugin
     * @throws UnknownDependencyException If a required dependency could not
     *     be found
     */
   public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException
    {
     //   File file = packagefile.getParentFile();
        
        if (!file.exists()) {
            throw new InvalidPluginException(new FileNotFoundException(String.format("%s does not exist",
                    file.getPath())));
        }
        
        WSPlugin result = null;
     
       final PluginDescriptionFile description;
        try {
            description = getPluginDescription(file);
        } catch (InvalidDescriptionException ex) {
            throw new InvalidPluginException(ex);
        }

         File dataFolder = new File(file.getParentFile().getParentFile(), description.getName());

         if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidPluginException(String.format(
                "Projected datafolder: `%s' for %s (%s) exists and is not a directory",
                dataFolder,
                description.getFullName(),
                file
            ));
        }

        result = new WSPlugin(server, this, description, dataFolder, file);
        result.onLoad();
        
        if (!loadedplugins.contains(description.getName()))
            loadedplugins.add(description.getName());
            
        return result;
    }

    /**
     * Loads a PluginDescriptionFile from the specified WolfPack
     *
     * @param file File to attempt to load from
     * @return A new PluginDescriptionFile loaded from the package.json in the
     *     specified WolfPack
     * @throws InvalidDescriptionException If the plugin description file
     *     could not be created
     */
    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException
    {
        try {
            FileReader reader = new FileReader(file);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            String name =(String)jsonObject.get("name");
            String version =(String)jsonObject.get("version");
            String main =(String)jsonObject.get("main");
            String language =(String)jsonObject.get("language");
            if (!language.trim().toLowerCase().equals("wolfscript"))
              	throw new InvalidDescriptionException("package.json does not contain language: wolfscript ");
          
            PluginDescriptionFile description = new PluginDescriptionFile(name, version, main);
            return description;
        } catch (Throwable t) {
			t.printStackTrace();
			server.getLogger().severe(t.getMessage());
			throw new InvalidDescriptionException("Failed to load package.json ");
		}
    }
    

    /**
     * Returns a list of all filename filters expected by this PluginLoader
     *
     * @return The filters
     */
    public Pattern[] getPluginFileFilters(){
         return fileFilters.clone();
    }

      /**
     * Creates and returns registered listeners for the event classes used in
     * this listener
     *
     * @param listener The object that will handle the eventual call back
     * @param plugin The plugin to use when creating registered listeners
     * @return The registered listeners.
     */
    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(
            Listener listener, Plugin plugin) {
    
        if(!listener.getClass().equals(WSEvents.class)) {
            throw new IllegalArgumentException("Listener to register is not WolfScript WSEvents class");
        }

        WSEvents wsEvents = (WSEvents)listener;

        if(!wsEvents.getPlugin().equals(plugin)) {
            throw new IllegalArgumentException("WSEvents listener is not associated with the given plugin");
        }

         return wsEvents.createRegisteredListeners();
    }

    /**
     * Enables the specified plugin
     * <p>
     * Attempting to enable a plugin that is already enabled will have no
     * effect
     *
     * @param plugin Plugin to enable
     */
    public void enablePlugin(Plugin plugin)
    {
        if (!plugin.isEnabled()) {
            plugin.getLogger().info("Enabling " + plugin.getDescription().getFullName());
            String pluginName = plugin.getDescription().getName();

            WSPlugin wsplugin = (WSPlugin) plugin;
          
            if (!loadedplugins.contains(pluginName))
                loadedplugins.add(pluginName);

            try {
                wsplugin.setEnabled(true);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }
             server.getPluginManager().callEvent(new PluginEnableEvent(plugin));
        }
    }

    /**
     * Disables the specified plugin
     * <p>
     * Attempting to disable a plugin that is not enabled will have no effect
     *
     * @param plugin Plugin to disable
     */
    public void disablePlugin(Plugin plugin)
    {
         if (plugin.isEnabled()) {
            String message = String.format("Disabling %s", plugin.getDescription().getFullName());
            plugin.getLogger().info(message);

            server.getPluginManager().callEvent(new PluginDisableEvent(plugin));

             WSPlugin wsplugin = (WSPlugin) plugin;
      
            try {
                wsplugin.setEnabled(false);
            } catch (Throwable ex) {
                server.getLogger().log(Level.SEVERE, "Error occurred while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
            }

            String pluginName = wsplugin.getDescription().getName();
            if (loadedplugins.contains(pluginName))
                loadedplugins.remove(pluginName);
    
         }
    }
}