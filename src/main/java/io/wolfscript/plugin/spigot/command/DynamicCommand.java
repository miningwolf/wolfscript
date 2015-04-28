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

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import io.wolfscript.plugin.ReflectionHelper;

import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;

/**
 * Python decorators and handler registration
 * @author lahwran
 */
public class DynamicCommand {

    /**
     * Once set, no more commands can be added
     */
    private boolean frozen = false;

    /**
     * List of handlers to register
     */
    ArrayList<WSCommandHandler> commandhandlers = new ArrayList<WSCommandHandler>();
    
    /**
     * Plugin description to modify when registering commands
     */
    PluginDescriptionFile plugindesc;

    /**
     * @param plugindesc Plugin description to modify when registering commands
     */
    DynamicCommand(PluginDescriptionFile plugindesc) {
        this.plugindesc = plugindesc;
    }

    @SuppressWarnings("unchecked")
    private void addCommandInfo(String name, String usage, String desc, List aliases) {
        Object object = plugindesc.getCommands();
        if (object == null) {
            object = new HashMap<String, HashMap<String, Object>>();
            try {
                ReflectionHelper.setPrivateValue(plugindesc, "commands", object);
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RunTimeException("Plugin commands list does not exist");
            }
        }

        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;

        if (map.containsKey(name)) {
            if (desc != null || usage != null || aliases != null)
                throw new RunTimeException("Plugin already has a command called '"+name+"'");
            else
                return;
        }

        Map<String, Object> commandmap = new HashMap<String, Object>();
        if (desc != null)
            commandmap.put("description", desc);
        if (usage != null)
            commandmap.put("usage", usage);
        if (aliases != null)
            commandmap.put("aliases", aliases);
        map.put(name, commandmap);
    }

    /**
     * Register everything with spigot
     * @param plugin plugin to do registration as
     */
    void doRegistrations(WSPlugin plugin) {
        frozen = true;
        PluginManager pm = plugin.getServer().getPluginManager();
        for (int i=0; i<commandhandlers.size(); i++) {
            commandhandlers.get(i).register(plugin);
        }
    }

    /**
     * Check if we're allowed to register stuff, and if not, throw an exception
     */
    private void checkFrozen() {
        if (frozen)
            throw new RunTimeException("Cannot register handlers after initiation is complete");
    }

    /**
     * Register a command 
     * @param func function to set as handler
     * @param name name to use
     * @param usage metadata
     * @param desc metadata
     * @param aliases metadata
     */
    public void registerCommand(JSFunction func, String name, String usage, String desc, List<?> aliases, JSFunction tabComplete, ExecutionContext executionContext) {
        checkFrozen();
        String finalname = name;
        if (finalname == null)
            finalname = ((JSFunction)func).__name__;
        addCommandInfo(finalname, usage, desc, aliases);
        WSCommandHandler handler = new WSCommandHandler(func, finalname, tabComplete, executionContext);
        commandhandlers.add(handler);
    }

}