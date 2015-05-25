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
import java.util.logging.Logger;

import io.wolfscript.plugin.ILogger;
import io.wolfscript.plugin.spigot.WSLoggerExtender;

public class WSLogger implements ILogger {
    private java.util.logging.Logger l;
    
    public WSLogger(String name, java.util.logging.Logger logger) { this.l = new WSLoggerExtender(name, logger); }
 
    public String getName() { return l.getName(); }
    public void debug(Object o) { if (l.isLoggable(Level.FINE)) l.log(Level.FINE, o.toString()); }
    public void debug(Object o, Throwable t) { if (l.isLoggable(Level.FINE)) l.log(Level.FINE, o.toString(), t); }
    public void error(Object o) { if (l.isLoggable(Level.SEVERE)) l.log(Level.SEVERE, o.toString()); }
    public void error(Object o, Throwable t) { if (l.isLoggable(Level.SEVERE)) l.log(Level.SEVERE, o.toString(), t); }
    public void fatal(Object o) { if (l.isLoggable(Level.SEVERE)) l.log(Level.SEVERE, o.toString()); }
    public void fatal(Object o, Throwable t) { if (l.isLoggable(Level.SEVERE)) l.log(Level.SEVERE, o.toString(), t); }
    public void severe(Object o) { if (l.isLoggable(Level.SEVERE)) l.log(Level.SEVERE, o.toString()); }
    public void severe(Object o, Throwable t) { if (l.isLoggable(Level.SEVERE)) l.log(Level.SEVERE, o.toString(), t); }
    public void info(Object o) { if (l.isLoggable(Level.INFO)) l.log(Level.INFO, o.toString()); }
    public void info(Object o, Throwable t) { if (l.isLoggable(Level.INFO)) l.log(Level.INFO, o.toString(), t); }
    public boolean isDebugEnabled() { return l.isLoggable(Level.FINE); }
    public boolean isErrorEnabled() { return l.isLoggable(Level.SEVERE); }
    public boolean isFatalEnabled() { return l.isLoggable(Level.SEVERE); }
    public boolean isInfoEnabled() { return l.isLoggable(Level.INFO); }
    public boolean isTraceEnabled() { return l.isLoggable(Level.FINEST); }
    public boolean isWarnEnabled() { return l.isLoggable(Level.WARNING); }
    public void trace(Object o) { if (l.isLoggable(Level.FINEST)) l.log(Level.FINEST, o.toString()); }
    public void trace(Object o, Throwable t) { if (l.isLoggable(Level.FINEST)) l.log(Level.FINEST, o.toString(), t); }
    public void warn(Object o) { if (l.isLoggable(Level.WARNING)) l.log(Level.WARNING, o.toString()); }
    public void warn(Object o, Throwable t) { if (l.isLoggable(Level.WARNING)) l.log(Level.WARNING, o.toString(), t); }
   
    public java.util.logging.Logger javaLogger() { return l;}
}