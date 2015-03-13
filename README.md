# WolfScript by Mining Wolf

##### A Node.js javascript language Plugin for CanaryMod with Nodyn and DynJS on Java

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/miningwolf/wolfscript?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)


### About

This repository contains a language plugin for writing Minecraft plugins.  It allows you to write server plugins/mods in JavasScript and makes available the full Node.js API and a game API that is equivalent to the full CanaryMod API.

* Anything you can do in Java you can do in WolfScript, with few exceptions.

* Anything you can do in Node.js (or io.js) you can do in WolfScript, with few exceptions.

The runtime produced includes a highly performant, open-source javascript engine (redistribution of DynJS) without dependency on the built-in Nashorn or Rhino.

By combining Node and CanaryMod, you can write plugins for MineCraft in a simple way, and harness the full ecosystem of more than 130,000 packages.

This framework has been tested with Minecraft 1.8 version clients.  It aims to be compatible with the latest stable version of CanaryMod and Nodyn.

### Getting started

Place `minecraft.jar` in the `/pluginlangs` directory of the CanaryMod server. 

Edit `server.cfg` to include `plugin-dev-mode=true` (to allow loading of plugins from directories, unless you want to zip or jar your WolfScript plugins).

Create a new subdirectory `wolftest` of `/plugins`

In /plugins/wolftest, place two files

#### Canary.inf

    main-class = main.js
    isLibrary = false
    name = wolftest
    author = Mining Wolf
    language=wolfscript
    version = 1.0

#### main.js

``` js
exports.enable = function (){ 
    this.getLogman().info("Hello World");
 };
exports.disable = function (){};
 ```
 
 
Run the server from the root of the canarymod server directory (contains canarymod.jar, the plugins, and pluginlangs folders)
 
     java -cp "canarymod.jar":"pluginlangs/wolfscript.jar" net.canarymod.Main


Tip:  use npm to package and distribute your wolf packs.  For now, you just need a Canary.inf in the home directory of each wolf pack.

### Getting Started


Build with 

    ant

Run with

    cd ../minecraft  #or wherever your canarymod directory is#
    java -cp "canarymod.jar":"pluginlangs/wolfscript.jar" net.canarymod.Main

Please note:  it is important not to run CanaryMod with the usual -jar command, but instead using the classpath (cp) switch indicated.

### License

Open-sourced under Apache 2.0 license.


### Disclaimers and Relationship to Other Work

This is an early development creation intended for private alpha testing.  

Any source code from the Minecraft Server or the CanaryMod Server is not owned by Mining Wolf or its contributors and is not covered by above license.

Usage of source code from the Minecraft Server is subject to the Minecraft End User License Agreement as set forth by Mojang AB.

"Minecraft" is a trademark of Notch Development AB
"CanaryMod" name is used under license from FallenMoonNetwork.

This site is a highly modified fork of ScriptCraft by Walter Higgins and the ideas contained here are highly inspired by that excellent work.  We have re-written and re-factored the code base to 

* Leverage Node.js runtime to ensure that programming skills learned with WolfScript are directly applicable to writing other production applications
* Runs as a language addition under CanaryMod instead of as a user plugin.  This allows the end user to write plugins in javascript and load on the server in the same way they might java, scala, lua or clojure plugins
* Unbundles all the sample and utility code from the language plugin, and relies on NPM to manage package dependencies

Nodyn is a leading-edge Node.js compatible framework, running on the JVM powered by the DynJS Javascript runtime.  It is open-source from the Project Odd team at RedHat.   At time of writing it is the only actively supported Node.js framework for the JVM (similar project avatar.js by Oracle was announced as no longer in active development)