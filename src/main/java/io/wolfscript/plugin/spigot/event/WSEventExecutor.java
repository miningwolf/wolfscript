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

import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.event.Listener;

import io.wolfscript.plugin.WSPluginCore;
import io.wolfscript.plugin.spigot.event.WSEvents;

@SuppressWarnings("unchecked")
public class WSEventExecutor implements EventExecutor {
    private JSFunction executeMethod;
    private ExecutionContext executionContext;
    private String name;
    private Class<? extends Event> type;
    private EventPriority priority;
    private WSPluginCore core;
 
    public WSEventExecutor(WSPluginCore core, String name, String priority, JSFunction executeMethod, ExecutionContext executionContext) throws Exception {
        this.name = name;
        this.executeMethod = executeMethod;
        this.executionContext = executionContext;
        this.core = core;
        this.type = getClass(name);
        this.priority = EventPriority.valueOf(priority);
    }

     private Class<? extends Event> getClass(String eventName) throws Exception {
           String[] elements = { "",
           "org.bukkit.event."      };

        for (String s: elements) {           
            System.out.println(s); 
            try {
                Class t =  Class.forName(s + eventName);
                return t;
            } catch (Exception ex) { 
                
            };
            try {
                Class t =  Class.forName(s + eventName + "Event");
                return t;
            } catch (Exception ex) { 
                
            };


            
        }
        throw new Exception("Event not found " + eventName); 
    }

   /**
     * Returns the name of this event
     *
     * @return Name of this event
     */
    public String getName() {
        return name;
    }


   /**
     * Returns the name of this event
     *
     * @return Type of this event
     */
    public Class<? extends Event> getType() {
        return type;
    }

       /**
     * Returns the priorty of this event
     *
     * @return Type of this event
     */
    public EventPriority getPriority() {
        return priority;
    }

     /**
     * Executes the given command, returning its success
     *
     * @param Listener Listener
     * @param Event Event
      */
    @Override
    public void execute(Listener listener, Event event) throws EventException
    {

        if(!listener.getClass().equals(WSEvents.class)) {
            throw new IllegalArgumentException("Event handler called with something other than a WolfScript WSEvents class");
        }

        try {
            executionContext.call((JSFunction) executeMethod, core, event);
        } catch (Exception ex) {
                throw new EventException(ex);
        }
    }
}