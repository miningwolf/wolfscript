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

import java.io.File;
import io.wolfscript.engine.WSEngineNodyn;

import java.io.IOException;

import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import java.util.ArrayList;

/* 
 * Lifecycle manager for a WolfScript plugin, host agnostic
 *
 * @author miningwolf
 */
public class WolfScriptPluginManager {
	
	protected  WSEngineNodyn wsEngine;
	protected Logger logger;
	protected ArrayList<WSPlugin> plugins = new ArrayList<WSPlugin>();
	
 	public void onLoad(Logger defaultLogger) throws Exception {
 	 
 	 logger = defaultLogger;
	    
	 	if (wsEngine == null) {
	 	    
            getLogger().info("Loading WolfScript Language Engine");
            wsEngine = new WSEngineNodyn(getLogger());
            wsEngine.loadRuntime();
            
           File dependencyDirectory = new File("node_modules/");
            File[] files = dependencyDirectory.listFiles();
            for (int i = 0; i < files.length; i++) {
            	if (files[i].isDirectory()) {
            	 
            	    WSPlugin wsPlugin = new WSPlugin(wsEngine, files[i].getName(), logger);
            	    plugins.add(wsPlugin);
                 getLogger().info("WolfScript Package Loaded: "+files[i].getName());
            	}
            }
	 	}
	}
   
    public void onEnable() throws Exception {
        getLogger().info("Enabling WolfScript Language Plugins");
        for (WSPlugin wsPlugin : plugins) {
           wsPlugin.onEnable();
        }
    }
    
    public void onDisable() throws Exception {
         getLogger().info("WolfScript Language Engine Stopped");
         for (WSPlugin wsPlugin : plugins) {
           wsPlugin.onDisable();
        }
    }
		
	public final Logger getLogger() {
        return logger;
    }
}