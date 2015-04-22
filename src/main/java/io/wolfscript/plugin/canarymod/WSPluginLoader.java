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

import net.canarymod.CanaryClassLoader;
import net.canarymod.exceptions.PluginLoadFailedException;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginDescriptor;
import net.canarymod.plugin.lifecycle.PluginLifecycleBase;
import net.canarymod.Canary;

import io.nodyn.Nodyn;
import io.nodyn.runtime.NodynConfig;
import io.nodyn.runtime.RuntimeFactory;
import io.nodyn.NoOpExitHandler;
import org.dynjs.runtime.*;
import org.dynjs.runtime.builtins.DynJSBuiltin;
import org.dynjs.runtime.java.JavaPackage;

import java.io.File;
import java.lang.Thread;
import java.lang.ClassLoader;

/* 
 * Lifecycle manager for a WolfScript plugin that runs under CanaryMod
 *
 * @author miningwolf
 */
public final class WSPluginLoader extends PluginLifecycleBase {
	private CanaryClassLoader loader;
	private static Nodyn nodyn;
	private static JSFunction bootPlugin;
	private static JSObject globalObject;

	public WSPluginLoader(PluginDescriptor desc) throws Exception {
		super(desc);
		
    	if (nodyn == null) {
			RuntimeFactory factory = RuntimeFactory.init(
			  WSPluginLoader.class.getClassLoader() 
			, RuntimeFactory.RuntimeType.DYNJS);

			String SCRIPT = "__native_require('bootstrap.js'); ";

			NodynConfig config = new NodynConfig(new String[] {
				"-e", SCRIPT
			});

			nodyn = factory.newRuntime(config);
			nodyn.setExitHandler(new NoOpExitHandler());

			globalObject = (JSObject) nodyn.getGlobalContext();
            DynJSBuiltin dynjsBuiltin = (DynJSBuiltin) globalObject.get(null, "dynjs");
		    GlobalContext globalContext = dynjsBuiltin.getRuntime().getGlobalContext();

			globalObject.defineOwnProperty(null, "__log", PropertyDescriptor.newDataPropertyDescriptor(Canary.log, true, true, false), false);
			globalObject.defineOwnProperty(null, "net", PropertyDescriptor.newDataPropertyDescriptor(new JavaPackage(globalContext, "net"), true, true, true), false);
		
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

		Plugin.threadLocalName.set(desc.getName());
		try {
				Plugin p = new WSPlugin(nodyn, desc);
				p.setName(desc.getName());
				p.setPriority(desc.getPriority());
				desc.setPlugin(p);
				p.getLogman().info("WolfScript plugin " + p.getName() + " loaded.");

			} catch (Exception t ){
					t.printStackTrace();
					Canary.log.error(t.getMessage());
					throw new PluginLoadFailedException("Failed to load plugin", t);
			}
		}

	@Override
	protected void _unload() {
		if (loader != null) {
			loader.close();
		}
	}
}