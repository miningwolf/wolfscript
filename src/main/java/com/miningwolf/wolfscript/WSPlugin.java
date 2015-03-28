/*
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

package com.miningwolf.wolfscript;

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
import io.nodyn.runtime.Program;
import io.nodyn.Callback;
import io.nodyn.CallbackResult;

import org.dynjs.runtime.JSObject;
import io.nodyn.Nodyn;
import org.dynjs.runtime.*;
import org.dynjs.runtime.builtins.DynJSBuiltin;

import java.util.List;

import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.TabCompleteDispatch;
import net.canarymod.commandsys.DynamicCommandAnnotation;
import com.miningwolf.wolfscript.commandsys.CommandListenerCallback;
import com.miningwolf.wolfscript.commandsys.DynamicCanaryCommand;
import net.canarymod.commandsys.CommandOwner;
import net.canarymod.commandsys.CommandManager;
import net.canarymod.commandsys.CommandDependencyException;
import org.dynjs.runtime.JSFunction;
import java.lang.Class;

/**
 * A WolfScript plugin (java side, common code for all WolfScript plugins)
 *
 * @author miningwolf
 */
public class WSPlugin extends Plugin {
	private JSObject jsplugin;
	private Nodyn nodyn;
	private DynJS runtime;
	private JSObject globalObject;
    private HookExecutor hookExecutor;
 
	public WSPlugin(Nodyn nodyn, PluginDescriptor desc) throws Exception {
		super();

		this.nodyn = nodyn;
		this.globalObject = (JSObject) nodyn.getGlobalContext();
		this.hookExecutor = Canary.hooks();

		this.jsplugin = null;
		String mainFile = desc.getPath() + "/" + desc.getCanaryInf().getString("main-class");
		try {
			DynJSBuiltin dynjsBuiltin = (DynJSBuiltin) globalObject.get(null, "dynjs");
			this.runtime = dynjsBuiltin.getRuntime();
			JSFunction bootPlugin = (JSFunction) globalObject.get(null, "__boot_plugin");

			Object obj = this.runtime.getDefaultExecutionContext().call(bootPlugin, this, mainFile);
			if (obj instanceof JSObject) {
				this.jsplugin = (JSObject) obj;
			} else {
				Canary.log.error("Wolfscript plugin does not seem to be an node module with exports.enable function() ");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			Canary.log.error(t.getMessage());
			throw new PluginLoadFailedException("Failed to load plugin", t);
		}
	}

	@Override
	public void disable() {
		try {
			runtime.getDefaultExecutionContext().call((JSFunction) jsplugin.get(null, "disable"), this);
		} catch (Exception e) {
			this.getLogman().error(e.getMessage());
		}
	}

	@Override
	public boolean enable() {
		try {
			ClassLoader cl = WSPlugin.class.getClassLoader();
			runtime.getDefaultExecutionContext().call((JSFunction) jsplugin.get(null, "enable"), this);
			return true;
		} catch (Exception e) {
			throw e;
		//	this.getLogman().error(e.getMessage());
		//	return false;
		}
	}


    // API Helpers
	public void DynamicCommand(String[] aliases, String[] permissions, String description, String toolTip, String parent, String helpLookup, String[] searchTerms, int min, int max, String tabCompleteMethod, int version, JSFunction execute, JSFunction tabComplete) {
		DynamicCommandAnnotation meta = new DynamicCommandAnnotation(aliases, permissions, description, toolTip, parent, helpLookup, searchTerms, min, max, tabCompleteMethod, version);
		DynamicCanaryCommand cc = new com.miningwolf.wolfscript.commandsys.DynamicCanaryCommand(execute, meta, this, tabComplete, runtime.getDefaultExecutionContext());
	
		try {
			Canary.commands().registerCommand(cc, this, false);
		} catch (CommandDependencyException e) {
			this.getLogman().error(e.getMessage());
		};
    }

    public void DynamicEvent(String eventName, JSFunction execute, String priority) {
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
						runtime.getDefaultExecutionContext().call(execute, listener, hook);
					} catch (Exception ex) {
						throw new HookExecutionException(ex.getMessage(), ex);
					}
				}
			},
			Priority.valueOf(priority)
			);
    }

    private Class getClass(String eventName) throws Exception {
	       String[] elements = { "",
	       "net.canarymod.hook.",
	      "net.canarymod.hook.command.",
	      "net.canarymod.hook.entity.",
	      "net.canarymod.hook.player.",
	      "net.canarymod.hook.system.",
	      "net.canarymod.hook.world." 
	    };

	    for (String s: elements) {           
		    System.out.println(s); 
		    try {
		    	Class t =  Class.forName(s + eventName);
		    	return t;
	   		} catch (Exception ex) { 
	   		 	
	   		};
	   		try {
		    	Class t =  Class.forName(s + eventName + "Hook");
		    	return t;
	   		} catch (Exception ex) { 
	   		 	
	   		};


	 		
		}
		throw new Exception("Hook not found " + eventName); 
	}

}