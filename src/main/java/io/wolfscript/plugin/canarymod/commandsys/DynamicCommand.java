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
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;

@SuppressWarnings("unchecked")
public class DynamicCommand extends CanaryCommand {
    private JSFunction executeMethod;
    private JSFunction tabCompleteMethod;
    private ExecutionContext executionContext;
 
    public DynamicCommand(JSFunction executeMethod, Command meta, CommandOwner owner, JSFunction tabCompleteMethod, ExecutionContext executionContext) {
        super(meta, owner, Translator.getInstance());
        this.executeMethod = executeMethod;
        this.tabCompleteMethod = tabCompleteMethod;
        this.executionContext = executionContext;
    }

    @Override
    protected void execute(MessageReceiver caller, String[] parameters) {
        executionContext.call((JSFunction) executeMethod, owner, caller, parameters);
    }

    @Override
    protected List<String> tabComplete(MessageReceiver caller, String[] parameters) {
         return  (List<String>)executionContext.call((JSFunction) tabCompleteMethod, owner, caller, parameters);
    }
}