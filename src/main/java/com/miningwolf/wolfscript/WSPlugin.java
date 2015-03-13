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

import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginDescriptor;
import net.canarymod.exceptions.PluginLoadFailedException;
import io.nodyn.runtime.Program;

import org.dynjs.runtime.JSObject;
import io.nodyn.Nodyn;
import org.dynjs.runtime.*;
import org.dynjs.runtime.builtins.DynJSBuiltin;

/**
 * A WolfScript plugin (java side, common code for all WolfScript plugins)
 *
 * @author miningwolf
 */
public class WSPlugin extends Plugin {
	private JSObject jsplugin;
	private Nodyn nodyn;
	private DynJS runtime;
	private JSObject globalObject;

	public WSPlugin(Nodyn nodyn, JSObject jsplugin) {
		super();

		this.jsplugin = jsplugin;
		this.nodyn = nodyn;
		this.globalObject = (JSObject) nodyn.getGlobalContext();

		DynJSBuiltin dynjsBuiltin = (DynJSBuiltin) globalObject.get(null, "dynjs");
		this.runtime = dynjsBuiltin.getRuntime();
	}

	@Override
	public void disable() {
		try {
			runtime.getDefaultExecutionContext().call((JSFunction) jsplugin.get(null, "disable"), this);
		} catch (Exception e) {
			this.getLogman().error(e.getMessage());
		}
	}

	@Override
	public boolean enable() {
		try {
			runtime.getDefaultExecutionContext().call((JSFunction) jsplugin.get(null, "enable"), this);
			return true;
		} catch (Exception e) {
			this.getLogman().error(e.getMessage());
			return false;
		}
	}

}