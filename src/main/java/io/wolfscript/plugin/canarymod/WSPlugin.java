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

package io.wolfscript.plugin.canarymod;

import net.canarymod.Canary;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginDescriptor;
import net.canarymod.plugin.PluginListener;
import net.canarymod.exceptions.PluginLoadFailedException;
import net.canarymod.hook.HookExecutor;
import net.canarymod.hook.Hook;
import net.canarymod.hook.HookExecutionException;
import net.canarymod.hook.Dispatcher;
import net.canarymod.plugin.Priority;

import io.wolfscript.plugin.canarymod.WSLogger;
import io.wolfscript.plugin.WSPluginCore;
import io.wolfscript.plugin.canarymod.commandsys.DynamicCommand;
import io.wolfscript.plugin.IRegisterHandler;

import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.TabCompleteDispatch;
import net.canarymod.commandsys.DynamicCommandAnnotation;
import net.canarymod.commandsys.CommandOwner;
import net.canarymod.commandsys.CommandManager;
import net.canarymod.commandsys.CommandDependencyException;

import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;

import java.util.List;

/**
 * Represents the Java container of a WolfScript plugin for CanaryMod
 * <p>
 * Loaded by new WolfScript plugin laoder
 * @author MiningWolf
 *
 */
public class WSPlugin extends Plugin implements IRegisterHandler {
	private WSLogger logger = null;
    private WSPluginCore core = null;
   private HookExecutor hookExecutor;
 
	public WSPlugin(PluginDescriptor desc) throws Exception {
		super();
		
	   this.hookExecutor = Canary.hooks();

		String mainFile = desc.getPath() + "/" + desc.getCanaryInf().getString("main-class");
		this.logger = new WSLogger(desc.getName(), (org.apache.logging.log4j.Logger)this.getLogman());
     
	  try {
           this.core = new WSPluginCore(mainFile, logger, this);
        } catch (Throwable t) {
			t.printStackTrace();
			this.logger.severe(t.getMessage());
		}
	}

	@Override
	public void disable() {
		 core.onDisable();
	}

	@Override
	public boolean enable() {
		try {
			   core.onEnable();
        		return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	  // IRegisterHandler (for API Helpers)
	  
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
    public void registerCommand(String name, String usage, String desc, List<?> aliases, JSFunction executeMethod, JSFunction tabComplete, ExecutionContext executionContext) {
     
	 DynamicCommandAnnotation meta = new DynamicCommandAnnotation(
		aliases.toArray(new String[0]),
		new String[0] /* permissionsArray */,
		desc /* description */,
		usage /* tooltip */,
		"" /* parent */,
		usage /* helpLookup */,
		new String[0] /* searchTerms */, 
		1 /* min */,
		-1 /* max */, 
		"" /* tabCompleteMethod */, 
		1 /* version */
		);
	
	   DynamicCommand cc = new DynamicCommand(this.core, executeMethod, meta, this, tabComplete, executionContext);
	
		try {
			Canary.commands().registerCommand(cc, this, false);
		} catch (CommandDependencyException e) {
			this.logger.error(e.getMessage());
		};
  
    }

    public void registerEvent(String eventName, JSFunction executeMethod, String priority, ExecutionContext executionContext) {
 		Class t;

		try
		{
    		t = this.getClass(eventName);
   		} catch (Exception ex) { 
   		 	throw new HookExecutionException(ex.getMessage(), ex); 
   		};

		hookExecutor.registerHook(
   			new PluginListener() {},
   			this,
   			t,
			new Dispatcher() {
				@Override
				public void execute(PluginListener listener, Hook hook) {
					try {
						executionContext.call(executeMethod, core, hook);
					} catch (Exception ex) {
						throw new HookExecutionException(ex.getMessage(), ex);
					}
				}
			},
			Priority.valueOf(priority)
			);
    }
	
	 private Class<? extends Hook> getClass(String eventName) throws Exception {
           String[] elements = { "",
           "net.canarymod.hook."      };

        for (String s: elements) {           
            System.out.println(s); 
            try {
                Class t =  Class.forName(s + eventName);
                return t;
            } catch (Exception ex) { 
                
            };
			if (eventName.endsWith("Event")) {
			
	            try {
			        Class t =  Class.forName(s + eventName.substring(0, eventName.length() - 5) + "Hook");
	                return t;
	            } catch (Exception ex) { 
	                
	            };
			}

        }
        throw new Exception("Event/Hook not found " + eventName); 
    }
}