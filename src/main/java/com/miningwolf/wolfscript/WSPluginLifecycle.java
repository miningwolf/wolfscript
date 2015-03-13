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

package com.miningwolf.wolfscript;

import net.canarymod.CanaryClassLoader;
import net.canarymod.exceptions.PluginLoadFailedException;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginDescriptor;
import net.canarymod.plugin.lifecycle.PluginLifecycleBase;
import net.canarymod.Canary;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.Invocable;
import javax.script.ScriptException;

import io.nodyn.Callback;
import io.nodyn.CallbackResult;
import io.nodyn.ExitHandler;
import io.nodyn.Nodyn;
import io.nodyn.runtime.NodynConfig;
import io.nodyn.runtime.RuntimeFactory;
import io.nodyn.NoOpExitHandler;
import org.dynjs.runtime.*;
import org.dynjs.runtime.builtins.DynJSBuiltin;

import io.nodyn.runtime.Program;

import java.io.File;


/* 
 * Lifecycle manager for a WolfScript plugin
 *
 * @author miningwolf
 */
public final class WSPluginLifecycle extends PluginLifecycleBase {
	private CanaryClassLoader loader;
	private static Nodyn nodyn;
	private static JSFunction bootPlugin;
	private static JSObject globalObject;

	public WSPluginLifecycle(PluginDescriptor desc) throws Exception {
		super(desc);

		if (nodyn == null) {
			RuntimeFactory factory = RuntimeFactory.init(
			WolfScriptPluginLifecycle.class.getClassLoader(),
			RuntimeFactory.RuntimeType.DYNJS);

			String SCRIPT = "__native_require('bootstrap.js'); ";

			NodynConfig config = new NodynConfig(new String[] {
				"-e", SCRIPT
			});

			nodyn = factory.newRuntime(config);
			nodyn.setExitHandler(new NoOpExitHandler());

			globalObject = (JSObject) nodyn.getGlobalContext();
			globalObject.defineOwnProperty(null, "__log", PropertyDescriptor.newDataPropertyDescriptor(Canary.log, true, true, false), false);

			try {
				int exitCode = nodyn.run();
				if (exitCode != 0) {
					throw new PluginLoadFailedException("Failed to load language plugin");
				}

				bootPlugin = (JSFunction) globalObject.get(null, "__boot_plugin");

			} catch (Throwable t) {
				t.printStackTrace();
				Canary.log.error(t.getMessage());
				throw new PluginLoadFailedException("Failed to load language plugin", t);
			}
		}
	}

	@Override
	protected void _load() throws PluginLoadFailedException {
		JSObject jsplugin = null;
		String mainFile = desc.getPath() + "/" + desc.getCanaryInf().getString("main-class");
		try {
			DynJSBuiltin dynjsBuiltin = (DynJSBuiltin) globalObject.get(null, "dynjs");
			DynJS runtime = dynjsBuiltin.getRuntime();
			Object obj = runtime.getDefaultExecutionContext().call(bootPlugin, globalObject, mainFile);
			if (obj instanceof JSObject) {
				jsplugin = (JSObject) obj;
			} else {
				Canary.log.error("Wolfscript plugin does not seem to be an node module with exports.enable function() ");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			Canary.log.error(t.getMessage());
			throw new PluginLoadFailedException("Failed to load plugin", t);
		}

		//loader = new CanaryClassLoader(new File(desc.getPath()).toURI().toURL(), getClass().getClassLoader());
		Plugin.threadLocalName.set(desc.getName());
		Plugin p = new WSPlugin(nodyn, jsplugin);
		p.setName(desc.getName());
		p.setPriority(desc.getPriority());
		desc.setPlugin(p);
		p.getLogman().info("WolfScript plugin " + mainFile + " loaded.");
	}

	@Override
	protected void _unload() {
		if (loader != null) {
			loader.close();
		}
	}
}