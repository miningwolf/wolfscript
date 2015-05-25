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

package io.wolfscript.plugin;

import java.util.List;
import java.lang.Class;
import io.wolfscript.plugin.ILogger;
import java.io.IOException;
import java.util.Arrays;

import io.wolfscript.engine.WSEngineNodyn;
import io.wolfscript.plugin.IRegisterHandler;

import org.dynjs.runtime.JSFunction;
import org.dynjs.runtime.JSObject;
import org.dynjs.runtime.ExecutionContext;


/**
 * A WolfScript plugin (java side, common code for all WolfScript plugins)
 *
 * @author miningwolf
 */
public class WSPluginCore {
	private JSObject jsplugin;
	private static WSEngineNodyn engine = null;
	private ILogger logger;
	private IRegisterHandler registerHandler;
	private ExecutionContext executionContext;
	
	public WSPluginCore(String mainFile, ILogger logger, IRegisterHandler registerHandler) throws Exception {
		super();
	
		if  (engine == null) {
			engine = new WSEngineNodyn(logger);
            engine.loadRuntime();
		}
 
		this.engine = engine;
		this.executionContext = engine.getDefaultExecutionContext();
		this.logger = logger;
		this.registerHandler = registerHandler;

		this.jsplugin = null;
	
		try {

			JSFunction bootPlugin = (JSFunction) engine.getGlobal( "__boot_plugin");
			Object obj = engine.call(bootPlugin, this, mainFile);
			
			if (obj instanceof JSObject) {
				this.jsplugin = (JSObject) obj;
			} else {
				this.getLogger().severe("WolfScript plugin does not seem to be a node module with exports.enable function() ");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			logger.severe(t.getMessage());
			throw new IOException("Failed to load plugin", t);
		}
	}

	public void onLoad() {
		try {
			engine.call((JSFunction) jsplugin.get(null, "onload"), this);
		} catch (Exception e) {
			this.getLogger().severe(e.getMessage());
		}
	}

	public void onDisable() {
		try {
			engine.call((JSFunction) jsplugin.get(null, "disable"), this);
		} catch (Exception e) {
			this.getLogger().severe(e.getMessage());
		}
	}

	public void onEnable() {
		try {
			engine.call((JSFunction) jsplugin.get(null, "enable"), this);
		} catch (Exception e) {
				this.getLogger().severe(e.getMessage());
		}
	}

    // API Helpers to Call IRegisterHandler

    public void registerWSCommand(String name, String usage, String desc, String[] aliases, JSFunction executeMethod, JSFunction tabComplete) {
           registerHandler.registerCommand(name, usage, desc, Arrays.asList(aliases), executeMethod, tabComplete, engine.getDefaultExecutionContext());
    }

    public void registerWSEvent(String eventName, JSFunction executeMethod, String priority) {
           registerHandler.registerEvent(eventName, executeMethod, priority, engine.getDefaultExecutionContext());
    }    
    
   	public final ILogger getLogger() {
        return logger;
    }
}