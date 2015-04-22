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

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.InvalidPluginException;

import io.wolfscript.engine.WSClassPathFixer;
import io.wolfscript.plugin.WSPluginManager;

/* 
 * Lifecycle manager for a WolfScript plugin that runs under Bukkit / CraftBukkit / Spigot
 *
 * @author miningwolf
 */
public class WolfScript extends JavaPlugin {
    
    private WSPluginManager wsPluginManager;
   
    public WolfScript() {
         super();
         
         wsPluginManager = new WSPluginManager();
         try {
             WSClassPathFixer.fix();
             wsPluginManager.onLoad(getLogger());
         } catch (Exception t ){
					t.printStackTrace();
					getLogger().severe(t.getMessage());
		}
    }
	
	@Override
    public void onEnable() {
        try {
             wsPluginManager.onEnable();
        } catch (Exception t ){
					t.printStackTrace();
					getLogger().severe(t.getMessage());
		}
   }
    
	@Override
    public void onDisable()  {
        try {
             wsPluginManager.onDisable();
        } catch (Exception t ){
					t.printStackTrace();
					getLogger().severe(t.getMessage());
			}
    }
}