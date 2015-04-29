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

package io.wolfscript.plugin.spigot.command;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;
import org.dynjs.runtime.DynArray;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import io.wolfscript.plugin.WSPluginCore;

@SuppressWarnings("unchecked")
public class WSCommandTabExecutor implements CommandExecutor, TabCompleter {
    private JSFunction executeMethod;
    private JSFunction tabCompleteMethod;
    private ExecutionContext executionContext;
    private String name;
    private WSPluginCore core;
 
     public WSCommandTabExecutor(WSPluginCore core, String name, JSFunction executeMethod, JSFunction tabCompleteMethod, ExecutionContext executionContext) {
        this.name = name;
        this.executeMethod = executeMethod;
        this.tabCompleteMethod = tabCompleteMethod;
        this.executionContext = executionContext;
        this.core = core;
    }

   /**
     * Returns the name of this command
     *
     * @return Name of this command
     */
    public String getName() {
        return name;
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
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        try {
         return (boolean)executionContext.call((JSFunction) executeMethod, core, sender, command, label, args);
        } catch (Throwable t) {
                t.printStackTrace();
                return false;
             }
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender Source of the command
     * @param command Command to execute
     * @param alias The alias used
     * @param args The arguments passed to the command, including final
     *     partial argument to be completed and command label
     * @return A List of possible completions for the final argument
     */
     @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
           try {
             return toJavaList((DynArray)executionContext.call((JSFunction) tabCompleteMethod, core, sender, command, alias, args));
             } catch (Throwable t) {
            t.printStackTrace();
              return Collections.<String>emptyList();
             }
    }

    private List<String> toJavaList(DynArray dynArray)
    {
         int length = (int) dynArray.length();
        List<String> converted = new ArrayList<String>();
        for (int i = 0; i < length; i++) {
            converted.add((String)dynArray.get(i));
        }
        return converted;
    }

    private String[] toJavaArray(DynArray dynArray)
    {
       int length = (int) dynArray.length();
       String[] converted = new String[length];
        for (int i = 0; i < length; i++) {
            converted[i] = (String)dynArray.get(i);
        }
        return converted;
    }
}