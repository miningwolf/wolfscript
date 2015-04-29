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

package io.wolfscript.plugin.spigot.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.TimedRegisteredListener;
import org.bukkit.event.Listener;

import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;

import io.wolfscript.plugin.spigot.WSPlugin;
import io.wolfscript.plugin.spigot.command.WSCommandTabExecutor;
import io.wolfscript.plugin.ReflectionHelper;
import io.wolfscript.plugin.WSPluginCore;

public class WSEvents implements Listener {

    /**
     * Once enabled, no more events can be registered
     */
    private boolean isEnabled = false;

    /**
     * Hashmap of handlers to register
     */
    HashMap<Class<? extends Event>, Set<WSEventExecutor>> handlers = new HashMap<Class<? extends Event>, Set<WSEventExecutor>>();
  
    /**
     * Plugin owner 
     */
    private WSPlugin plugin;
    private WSPluginCore core;
    

    public WSEvents(WSPlugin plugin, WSPluginCore core) {
        this.plugin = plugin;
        this.core = core;
    }

    /**
     * Freeze from futher event registrations 
     */
    public void onEnable() {
        isEnabled = true;
    }

    public WSPlugin getPlugin() {
        return plugin;
    }

    /**
    * Register an event handler 
    * @param eventName name to use (e.g., "player.PlayerJoin")
    * @param JSFunction executeMethod to set as handler
    * @param String priority of event
    * @param ExecutionContext executionContext
    */ 
    public void registerEvent(String eventName, JSFunction executeMethod, String priority, ExecutionContext executionContext) {
        
        if (isEnabled)
            throw new RuntimeException("Cannot register handlers after plugin is enabled; add in consructor or on load");

        try
        {
            WSEventExecutor eventHandler = new WSEventExecutor(core, eventName, priority, executeMethod, executionContext);
            Class<? extends Event> type = eventHandler.getType();

             Set<WSEventExecutor> set = this.handlers.get(type);

            if(set == null) {
                set = new HashSet<WSEventExecutor>();
                handlers.put(type, set);
            }

            set.add(eventHandler);
        }
        catch (Throwable t) {
            t.printStackTrace();
            plugin.getLogger().severe(t.getMessage());
        }
    }

    /**
    * Utility method called by WSPluginLoader to seal and return all registered handlers
    * @return Map of Events and Registered Listeners
    */ 
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners() {
        
        boolean useTimings = this.plugin.getServer().getPluginManager().useTimings();
        Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<Class<? extends Event>, Set<RegisteredListener>>();

       for(Map.Entry<Class<? extends Event>, Set<WSEventExecutor>> entry : handlers.entrySet()) {
            Set<RegisteredListener> eventSet = new HashSet<RegisteredListener>();

            for(final WSEventExecutor handler : entry.getValue()) {
             
                if(useTimings) {
                    eventSet.add(new TimedRegisteredListener(this, handler, handler.getPriority(), plugin, false));
                }
                else {
                    eventSet.add(new RegisteredListener(this, handler, handler.getPriority(), plugin, false));
                }
            }
            plugin.getLogger().info("Event " + entry.getKey().getName() + " monitored");
            ret.put(entry.getKey(), eventSet);
        }
        return ret;
    }

}