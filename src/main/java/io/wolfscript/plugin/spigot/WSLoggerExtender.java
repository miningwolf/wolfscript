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

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The WSLoggerExtender class is a modified {@link Logger} that prepends all
 * logging calls with the name of the plugin doing the logging. The API for
 * WSPluginLogger is exactly the same as {@link Logger}.
 *
 * @see Logger
 */
public class WSLoggerExtender extends Logger {
    
    private String pluginName;
    
    /**
     * Creates a new WSPluginLogger that prefixes the name of a plugin.
     *
     * @param context A reference to the plugin
     */
    public WSLoggerExtender(String name, Logger parent) {
        super(name, parent.getResourceBundleName());
        pluginName = "[" + name + "] ";
        setParent(parent);
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(pluginName + logRecord.getMessage());
        super.log(logRecord);
    }

}