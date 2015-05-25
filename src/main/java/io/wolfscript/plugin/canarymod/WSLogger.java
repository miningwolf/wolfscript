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

package io.wolfscript.plugin.canarymod;

import org.apache.logging.log4j.Logger;
import io.wolfscript.plugin.ILogger;

public class WSLogger implements ILogger {
    private org.apache.logging.log4j.Logger l;
    public WSLogger(String name, org.apache.logging.log4j.Logger logger) { this.l = logger; }
  
    public String getName() { return l.getName(); }
    public void debug(Object o) { l.debug(o); }
    public void debug(Object o, Throwable t) { l.debug(o, t); }
    public void error(Object o) { l.error(o); }
    public void error(Object o, Throwable t) { l.error(o, t); }
    public void fatal(Object o) { l.fatal(o); }
    public void fatal(Object o, Throwable t) { l.fatal(o, t); }
    public void severe(Object o) { l.fatal(o); }
    public void severe(Object o, Throwable t) { l.fatal(o, t); }
    public void info(Object o) { l.info(o); }
    public void info(Object o, Throwable t) { l.info(o, t); }
    public boolean isDebugEnabled() { return l.isDebugEnabled(); }
    public boolean isErrorEnabled() { return l.isErrorEnabled(); }
    public boolean isFatalEnabled() { return l.isFatalEnabled(); }
    public boolean isInfoEnabled() { return l.isInfoEnabled(); }
    public boolean isTraceEnabled() { return l.isTraceEnabled(); }
    public boolean isWarnEnabled() { return l.isWarnEnabled(); }
    public void trace(Object o) { trace(o); }
    public void trace(Object o, Throwable t) { trace(o, t); }
    public void warn(Object o) { warn(o); }
    public void warn(Object o, Throwable t) { warn(o, t); }
    
    public org.apache.logging.log4j.Logger log4jLogger() { return l;}
}