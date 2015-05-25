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

import net.canarymod.exceptions.PluginLoadFailedException;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginDescriptor;
import net.canarymod.plugin.lifecycle.PluginLifecycleBase;
import net.canarymod.Canary;

import io.wolfscript.engine.WSClassPathFixer;
import io.wolfscript.plugin.canarymod.WSPlugin;

/* 
 * Lifecycle manager for a WolfScript plugin that runs under CanaryMod
 *
 * @author miningwolf
 */
public final class WolfScript extends PluginLifecycleBase {
		
	public WolfScript(PluginDescriptor desc) throws Exception {
		super(desc);
		
		  try {
                 WSClassPathFixer.fix();
             } catch (Exception t ){
    					t.printStackTrace();
    					Canary.log.error(t.getMessage());
    		}
	}

	@Override
	protected void _load() throws PluginLoadFailedException {

		Plugin.threadLocalName.set(desc.getName());
		try {
				Plugin p = new WSPlugin(desc);
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
	}
}