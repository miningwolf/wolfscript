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
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import io.wolfscript.plugin.WSPluginLogger;
import io.wolfscript.plugin.WSPluginCore;

import com.avaje.ebean.EbeanServer;

import io.wolfscript.plugin.spigot.WSPluginLoader;

/**
 * Represents the Java container of a WolfScript plugin for Spigot/CraftBukkit/Bukkit
 * <p>
 * Loaded by new WSPluginLoader, not by original JavaPluginLoader
 * @author MiningWolf
 *
 */
public class WSPlugin extends PluginBase {
    private boolean isEnabled = false;
    private PluginLoader loader = null;
    private Server server = null;
    private File file = null;
    private PluginDescriptionFile description = null;
    private File dataFolder = null;
    private boolean naggable = true;
    private WSPluginLogger logger = null;
    
    private WSPluginCore core = null;
    
    public WSPlugin() {
    }

    public WSPlugin(final Server server, final WSPluginLoader loader, final PluginDescriptionFile description, final File dataFolder, final File file) {
        this.server = server;
        this.loader = loader;
        this.file = file;
        this.description = description;
        this.dataFolder = dataFolder;
        this.logger = new WSPluginLogger(description.getName(), server.getLogger());
        File mainFile = new File(file.getParentFile(), description.getMain());
        this.logger.info("Plugin for WolfScript loading: " + mainFile.getAbsolutePath());
     
        try {
            this.core = new WSPluginCore(mainFile.getAbsolutePath(), logger);
        } catch (Throwable t) {
			t.printStackTrace();
			logger.severe(t.getMessage());
		}
    }
   
   /**
     * Returns the folder that the plugin data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder
     */
    @Override
    public File getDataFolder()
    {
        return dataFolder;
    }

    /**
     * Returns the plugin.yaml file containing the details for this plugin
     *
     * @return Contents of the plugin.yaml file
     */
   @Override
   public PluginDescriptionFile getDescription()
    {
        return description;
    }

    /**
     * Gets the associated PluginLoader responsible for this plugin
     *
     * @return PluginLoader that controls this plugin
     */
     @Override
    public PluginLoader getPluginLoader()
    {
        return loader;
    }

    /**
     * Returns the Server instance currently running this plugin
     *
     * @return Server running this plugin
     */
     @Override
    public Server getServer()
    {
        return server;
    }
    
    /**
     * Sets the enabled state of this plugin
     *
     * @param enabled true if enabled, otherwise false
     */
    public final void setEnabled(final boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;

            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    /**
     * Returns a value indicating whether or not this plugin is currently
     * enabled
     *
     * @return true if this plugin is enabled, otherwise false
     */
     @Override
    public boolean isEnabled()
    {
        return isEnabled;
    }


    /**
     * Called after a plugin is loaded but before it has been enabled.
     * <p>
     * When mulitple plugins are loaded, the onLoad() for all plugins is
     * called before any onEnable() is called.
     */
     @Override
    public void onLoad()
    {
    }

    /**
     * Called when this plugin is enabled
     */
     @Override
    public void onEnable()
    {
        core.onEnable();
    }
    
    /**
     * Called when this plugin is disabled
     */
     @Override
    public void onDisable()
    {
        core.onDisable();
    }


    /**
     * Simple boolean if we can still nag to the logs about things
     *
     * @return boolean whether we can nag
     */
     @Override
    public boolean isNaggable()
    {
        return naggable;
    }

    /**
     * Set naggable state
     *
     * @param canNag is this plugin still naggable?
     */
     @Override
    public void setNaggable(boolean canNag)
    {
        naggable = canNag;
    }

    /**
     * Gets a {@link ChunkGenerator} for use in a default world, as specified
     * in the server configuration
     *
     * @param worldName Name of the world that this will be applied to
     * @param id Unique ID, if any, that was specified to indicate which
     *     generator was requested
     * @return ChunkGenerator for use in the default world generation
     */
     @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
    {
        getServer().getLogger().severe("Plugin " + getDescription().getFullName() + " does not contain any generators that may be used in the default world");
        return null;
    }

    /**
     * Returns the plugin logger associated with this server's logger. The
     * returned logger automatically tags all log messages with the plugin's
     * name.
     *
     * @return Logger associated with this plugin
     */
     @Override
    public Logger getLogger()
    {
         return logger;
    }
    
     @Override
    public String toString() {
        return description.getFullName();
    }
    
    // **************** EMPTY METHODS ****************
    
      /**
     * Gets a {@link FileConfiguration} for this plugin, read through
     * "config.yml"
     * <p>
     * If there is a default config.yml embedded in this plugin, it will be
     * provided as a default for this Configuration.
     *
     * @return Plugin configuration
     */
     @Override
    public FileConfiguration getConfig()
    {
        return null;
    }

    /**
     * Gets an embedded resource in this plugin
     *
     * @param filename Filename of the resource
     * @return File if found, otherwise null
     */
     @Override
    public InputStream getResource(String filename)
    {
        return null;
    }

    /**
     * Saves the {@link FileConfiguration} retrievable by {@link #getConfig()}.
     */
     @Override
    public void saveConfig()
    {
        
    }

    /**
     * Saves the raw contents of the default config.yml file to the location
     * retrievable by {@link #getConfig()}. If there is no default config.yml
     * embedded in the plugin, an empty config.yml file is saved. This should
     * fail silently if the config.yml already exists.
     */
     @Override
    public void saveDefaultConfig()
    {
        
    }

    /**
     * Saves the raw contents of any resource embedded with a plugin's .jar
     * file assuming it can be found using {@link #getResource(String)}.
     * <p>
     * The resource is saved into the plugin's data folder using the same
     * hierarchy as the .jar file (subdirectories are preserved).
     *
     * @param resourcePath the embedded resource path to look for within the
     *     plugin's .jar file. (No preceding slash).
     * @param replace if true, the embedded resource will overwrite the
     *     contents of an existing file.
     * @throws IllegalArgumentException if the resource path is null, empty,
     *     or points to a nonexistent resource.
     */
     @Override
    public void saveResource(String resourcePath, boolean replace)
    {
        
    }

    /**
     * Discards any data in {@link #getConfig()} and reloads from disk.
     */
     @Override
    public void reloadConfig()
    {
        
    }

  /**
     * Gets the {@link EbeanServer} tied to this plugin. This will only be
     * available if enabled in the {@link
     * PluginDescriptionFile#isDatabaseEnabled()}
     * <p>
     * <i>For more information on the use of <a href="http://www.avaje.org/">
     * Avaje Ebeans ORM</a>, see <a
     * href="http://www.avaje.org/ebean/documentation.html">Avaje Ebeans
     * Documentation</a></i>
     * <p>
     * <i>For an example using Ebeans ORM, see <a
     * href="https://github.com/Bukkit/HomeBukkit">Bukkit's Homebukkit Plugin
     * </a></i>
     *
     * @return ebean server instance or null if not enabled
     */
      @Override
   public EbeanServer getDatabase()
    {
        return null;
    }
 
       /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param alias The alias used
     * @param args The arguments passed to the command, including final
     *     partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     *     to default to the command executor
     */
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        return null;
    }
    
      /**
     * Executes the given command, returning its success
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        return false;
    }
}