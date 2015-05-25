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

package io.wolfscript.plugin.canarymod.commandsys;

import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.Translator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;
import org.dynjs.runtime.DynArray;
import java.util.Collections;
import io.wolfscript.plugin.WSPluginCore;

@SuppressWarnings("unchecked")
public class DynamicCommand extends CanaryCommand {
    private JSFunction executeMethod;
    private JSFunction tabCompleteMethod;
    private ExecutionContext executionContext;
    private WSPluginCore core;
 
    public DynamicCommand(WSPluginCore core, JSFunction executeMethod, Command meta, CommandOwner owner, JSFunction tabCompleteMethod, ExecutionContext executionContext) {
        super(meta, owner, Translator.getInstance());
        this.executeMethod = executeMethod;
        this.tabCompleteMethod = tabCompleteMethod;
        this.executionContext = executionContext;
        this.core = core;
    }

    @Override
    protected void execute(MessageReceiver caller, String[] parameters) {
       try {
         executionContext.call((JSFunction) executeMethod, core, caller, parameters);
        } catch (Throwable t) {
                t.printStackTrace();
       }
    }

    @Override
    protected List<String> tabComplete(MessageReceiver caller, String[] parameters) {
          try {
             return toJavaList((DynArray)executionContext.call((JSFunction) tabCompleteMethod, core, caller, parameters));
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
}