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

package io.wolfscript.plugin.spigot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.lang.RuntimeException;
import java.lang.IllegalArgumentException;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.command.PluginCommand;

import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;

import io.wolfscript.plugin.spigot.WSPlugin;
import io.wolfscript.plugin.spigot.command.WSCommandTabExecutor;
import io.wolfscript.plugin.ReflectionHelper;

public class WSCommands {

    /**
     * Once enabled, no more commands can be added
     */
    private boolean isEnabled = false;

    /**
     * List of handlers to register
     */
    ArrayList<WSCommandTabExecutor> commandTabExecutors = new ArrayList<WSCommandTabExecutor>();
    
    /**
     * Plugin owner 
     */
    WSPlugin plugin;

    public WSCommands(WSPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Add tabExecutors to new Spigot Command on plugin enable
     */
    public void onEnable() {
        isEnabled = true;

        PluginManager pm = plugin.getServer().getPluginManager();
        for (int i=0; i<commandTabExecutors.size(); i++) {
            WSCommandTabExecutor commandTabExecutor = commandTabExecutors.get(i);
            String name = commandTabExecutor.getName();
            PluginCommand command = getCommand(name);
            if (command == null)
                throw new IllegalArgumentException("Command " + name + " not found in plugin " + plugin.getDescription().getName());
            command.setExecutor(commandTabExecutor);
            command.setTabCompleter(commandTabExecutor);
       }
    }

     /**
     * Gets the command with the given name, specific to the plugin
     *
     * @param name Name or alias of the command
     * @return PluginCommand if found, otherwise null
     */
     private PluginCommand getCommand(String name) {
        String alias = name.toLowerCase();
        PluginCommand command = plugin.getServer().getPluginCommand(alias);

        if ((command != null) && (command.getPlugin() != plugin)) {
            command = plugin.getServer().getPluginCommand(plugin.getDescription().getName().toLowerCase() + ":" + alias);
        }

        if ((command != null) && (command.getPlugin() == plugin)) {
            return command;
        } else {
            return null;
        }
    }

    /**
    * Register a command 
    * @param name name to use
    * @param usage metadata
    * @param desc metadata
    * @param aliases metadata
    * @param JSFunction executeMethod to set as handler
    * @param JSFunction tabCompleteMethod
    * @param ExecutionContext executionContext
    */ 
    public void registerCommand(String name, String usage, String desc,  List<?> aliases, JSFunction executeMethod, JSFunction tabComplete, ExecutionContext executionContext) {
       
        if (isEnabled)
            throw new RuntimeException("Cannot register handlers after plugin is enabled; add in consructor or on load");
 
        Map<String, Object> commandmap = new HashMap<String, Object>();
        if (desc != null)
            commandmap.put("description", desc);
        if (usage != null)
            commandmap.put("usage", usage);
        if (aliases != null)
            commandmap.put("aliases", aliases);

        addToPluginDescription(name, commandmap);
    
        WSCommandTabExecutor commandTabExecutor = new WSCommandTabExecutor(name, executeMethod, tabComplete, executionContext);
        commandTabExecutors.add(commandTabExecutor);
    }

    @SuppressWarnings("unchecked")
    private void addToPluginDescription(String name, Map<String, Object> commandmap)
    {
        PluginDescriptionFile plugindesc = plugin.getDescription();
        Object object = plugindesc.getCommands();
        
        if (object == null) {
            object = new HashMap<String, HashMap<String, Object>>();
            try {
                ReflectionHelper.setPrivateValue(plugindesc, "commands", object);
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RuntimeException("Plugin command list does not exist");
            }
        }

        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;

        if (map.containsKey(name)) {
                throw new RuntimeException("Plugin already has a command called " + name);
               }

        map.put(name, commandmap);   
    }

}