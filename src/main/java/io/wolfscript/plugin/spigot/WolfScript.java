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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.regex.Matcher;


import java.util.regex.Pattern;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.PluginLoader;

import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import io.wolfscript.engine.WSClassPathFixer;

/**
 * Spigot / CraftBukkit / Bukkit Java plugin 
 * to add a separate WolfScript plugin loader
 * <p>
 * Loaded by original JavaPluginLoader, not WSPluginLoader
 * @author MiningWolf
 *
 */
public class WolfScript extends JavaPlugin {
    
    private static Map<Pattern, PluginLoader> fileAssociations = null;
    
    private Pattern[] filters;
    private PluginManager pm;
  
   	@Override
   	public void onDisable() {}
   	
    @Override
    public void onEnable() {}

    /**
     * Called when WolfScript is loaded
     * <p>
     * Checks to see if WolfScript Plugin Loader is registered and if not,
     * registers it and then iterates through disk to load any WolfScript
     * plugins (using the same host plugin manager used by Java plugins)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onLoad() {
        boolean needsload = true;
        
        pm = Bukkit.getServer().getPluginManager();
       
        if (fileAssociations == null)
        {         
            Class<?> pmclass = null;
            Field fieldFileAssociations = null;
            
            try {
                pmclass = Class.forName("org.bukkit.plugin.SimplePluginManager");
                fieldFileAssociations = pmclass.getDeclaredField("fileAssociations");
                fieldFileAssociations.setAccessible(true);
                fileAssociations = (Map<Pattern, PluginLoader>) fieldFileAssociations.get(pm);
            } catch (Throwable t) {
                getLogger().severe("Error while checking for SimplePluginManager");
                t.printStackTrace();
            }
        }
        
       filters = WSPluginLoader.fileFilters;
          
        if (fileAssociations != null) {
            PluginLoader loader = fileAssociations.get(filters[0]);
            if (loader != null) // already loaded
                needsload = false;
        }

        if (needsload) {
            
            getLogger().info("Initializing WolfScript by MiningWolf");
            pm.registerInterface(WSPluginLoader.class);
  
            try {
                 WSClassPathFixer.fix();
             } catch (Exception t ){
    					t.printStackTrace();
    					getLogger().severe(t.getMessage());
    		}
              
           findLoadPlugins( new File(this.getFile().getParentFile(), "node_modules"));
           findLoadPlugins( new File(this.getFile().getParentFile().getParentFile(), "node_modules"));
        } 
    } 
    
    private void findLoadPlugins(File jsdir) {
        
          this.getLogger().info(jsdir.getAbsolutePath());
            
            if (jsdir.exists())
            {
                for (File pdir : jsdir.listFiles()) {
                  if (pdir.isDirectory()){
                     for (File file : pdir.listFiles()) {
                        for (Pattern filter : this.filters) {
                            Matcher match = filter.matcher(file.getName());
                            if (match.find()) {
                                try {
                                    this.pm.loadPlugin(file);
                                }catch (Throwable t) {
                                     t.printStackTrace();
                                }
                            }  //match
                        } //filters
                     } //files
                    } // if
                } //pdir
            }  
    }
 
}