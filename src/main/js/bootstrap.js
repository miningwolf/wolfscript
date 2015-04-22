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

/*
 This file is executed directly from io.wolfscript.plugin.[host].WSPluginLoader.java
 */
 
var util=require('util');
var events = require("events");

console.log = function log() {
    var msg = util.format.apply(this, arguments);
    __log.info(msg);
  };

console.error = function log_error() {
    var msg = util.format.apply(this, arguments);
    __log.error(msg);
  };
  
global.__boot_plugin = function(mainFile){
    var jsplugin = require(mainFile);

    jsplugin.wolfscript = this;
    jsplugin._API = new API(this, jsplugin);
  
     if (jsplugin && jsplugin.enable)
       return jsplugin
    else
       return false;
};

// global.wolfserver = global.Packages.io.wolfscript.java1;
// global.io.wolfscript = global.Packages.net.canarymod;

var API = function(javaplugin, jsplugin) {
  if (!(this instanceof API)) return new API(javaplugin, jsplugin);
 
   this._javaplugin = javaplugin;
   this._jsplugin = jsplugin;
  
   javaplugin.registerCommand = this.registerCommand.bind(javaplugin);
   javaplugin.onEvent = this.onEvent.bind(javaplugin);
   javaplugin.events = new events.EventEmitter();
   javaplugin.events.on("newListener", function(event, handler) {javaplugin.onEvent(event, handler, "NORMAL")});

 };

/**
 * Register a new command dynamically on the minecraft server
 *
 * @method registerCommand
 * @param String[] aliases    The command names
 * @param String[] permissions  A list of permissions to use this command.
 *                              If you specify more than one, only one of them is needed to execute the command
 * @param String description  What does this command do?
 *                            This will be displayed in a help context.
 *                            Note: This string will be pushed through the translator
 *                            If it finds a respective translation, it will output that instead
 * @param String toolTip    The tip to display when command parsing failed.
 *                            This may also be displayed when help for this command
 *                            was specifically requested
 * @param String parent   The parent command, for creating sub-command structures
 * @param String helpLookup   Explicitly define a name with which the command will be registered
 *                            at the help system. If this is empty (default), all aliases will be registered.
 *                            Otherwise only this name will be registered. <br>
 *                            Use it for registering sub-command helps to avoid name conflicts
 * @param String[] searchTerms   Specifies specific terms for looking up this command in help search
 * @param int min   Min amount of parameters   default 0
 * @param int max   The max amounts of parameters. -1 for infinite amount  default -1
 * @param String tabCompleteMethod
 * @param int version   The version of the command system to use.
 *                      Version 1 passes the command name with the arguments,
 *                      where as Version 2 adjusts the arguments to remove command name.
 *                      NOTE: Available versions are '1' and '2'.
 *                      Default 1
 */

API.prototype.registerCommand = function(metaContext) {
  var aliases = (metaContext.aliases === undefined) ? [ "js" , "jsp" ] : metaContext.aliases;
  var permissions = (metaContext.permissions === undefined) ? [ "" ] : metaContext.permissions;
  var description = (metaContext.description === undefined) ? "" : metaContext.description;
  var toolTip = (metaContext.toolTip === undefined) ? "/js command" : metaContext.toolTip;
  var parent = (metaContext.parent === undefined) ? "" : metaContext.parent;
  var helpLookup = (metaContext.helpLookup === undefined) ? "" : metaContext.helpLookup;
  var searchTerms = (metaContext.searchTerms === undefined) ? [ "" ] : metaContext.searchTerms;
  var min = (metaContext.min === undefined) ? 1 : metaContext.min;
  var max = (metaContext.max === undefined) ? -1 : metaContext.max;
  var tabComplete = (metaContext.tabComplete === undefined) ? function(sender, args){ return ["js"]; } : metaContext.tabComplete;
  var version = (metaContext.version === undefined) ? 1 : metaContext.version;
  var execute = (metaContext.execute === undefined) ? function(sender, args) {} : metaContext.execute;
  
  this.DynamicCommand(
    aliases,
    permissions,  
    description, 
    toolTip, 
    parent, 
    helpLookup, 
    searchTerms, 
    min,
    max, 
    "", 
    version, 
    execute,
    function(owner, sender, args){tabComplete.call(owner,sender,args);}
    );
}

API.prototype.onEvent = function(name, handler, priority) {
  this.DynamicEvent(
    name, 
    handler, 
    priority);
}

console.log("WolfScript successfully bootstrapped");
console.log(process.cwd());