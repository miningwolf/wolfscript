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
import java.util.logging.Logger;
import java.io.IOException;

import io.wolfscript.engine.WSEngineNodyn;

import org.dynjs.runtime.JSFunction;
import org.dynjs.runtime.JSObject;

/**
 * A WolfScript plugin (java side, common code for all WolfScript plugins)
 *
 * @author miningwolf
 */
public class WSPlugin {
	private JSObject jsplugin;
	private WSEngineNodyn engine;
	private Logger logger;

	public WSPlugin(WSEngineNodyn engine, String mainFile, Logger logger) throws Exception {
		super();

		this.engine = engine;
		this.logger = logger;
	//	this.hookExecutor = Canary.hooks();

		this.jsplugin = null;
	//	String mainFile = desc.getPath() + "/" + desc.getCanaryInf().getString("main-class");
	
		try {
			JSFunction bootPlugin = (JSFunction) engine.getGlobal( "__boot_plugin");
			Object obj = engine.call(bootPlugin, this, mainFile);
			
			if (obj instanceof JSObject) {
				this.jsplugin = (JSObject) obj;
			} else {
				this.getLogger().severe("Wolfscript plugin does not seem to be an node module with exports.enable function() ");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			logger.severe(t.getMessage());
			throw new IOException("Failed to load plugin", t);
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

    // API Helpers
	public void DynamicCommand(String[] aliases, String[] permissions, String description, String toolTip, String parent, String helpLookup, String[] searchTerms, int min, int max, String tabCompleteMethod, int version, JSFunction execute, JSFunction tabComplete) {
    }

    public void DynamicEvent(String eventName, JSFunction execute, String priority) {
    }
    
   	public final Logger getLogger() {
        return logger;
    }

}