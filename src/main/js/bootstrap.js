/*
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

/*
 This file is executed directly from com.miningwolf.wolfscript.WSPluginLifecycle.java
 */
 
var util=require('util');

console.log = function log() {
    // Handle formatting and circular objects like in the original
    var msg = util.format.apply(this, arguments);
    __log.info(msg);
  };

console.error = function log_error() {
    // Handle formatting and circular objects like in the original
    var msg = util.format.apply(this, arguments);
    __log.error(msg);
  };
  
global.__boot_plugin = function(mainFile){
    var plugin = require(mainFile);
    if (plugin && plugin.enable)
       return plugin
    else
       return false;
};