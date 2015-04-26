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

package io.wolfscript.engine;

import io.nodyn.Nodyn;
import io.nodyn.runtime.NodynConfig;
import io.nodyn.runtime.RuntimeFactory;
import io.nodyn.NoOpExitHandler;
import org.dynjs.runtime.*;
import org.dynjs.runtime.builtins.DynJSBuiltin;
import org.dynjs.runtime.java.JavaPackage;

import java.io.File;
import java.lang.Thread;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.io.IOException;
import java.util.logging.Logger;

/* 
 * Nodyn Loader for WolfScript plugins
 *
 * @author miningwolf
 */
public class WSEngineNodyn  {
	
	protected Nodyn nodyn;
	protected JSObject globalObject;
	protected Logger logger;
	protected DynJS runtime;

	public WSEngineNodyn(Logger defaultLogger)
	{
		logger = defaultLogger;
	}

   	public void loadRuntime() throws IOException {
    	if (nodyn == null) {
    		RuntimeFactory factory = RuntimeFactory.init(
			 this.getClass().getClassLoader()
			, RuntimeFactory.RuntimeType.DYNJS);

			String SCRIPT = "__native_require('bootstrap.js'); ";

			NodynConfig config = new NodynConfig(new String[] {
				"-e", SCRIPT
			});
	
			nodyn = factory.newRuntime(config);
			nodyn.setExitHandler(new NoOpExitHandler());
			
			globalObject = (JSObject) nodyn.getGlobalContext();
            DynJSBuiltin dynjsBuiltin = (DynJSBuiltin) globalObject.get(null, "dynjs");
            runtime = dynjsBuiltin.getRuntime();
	
		    GlobalContext globalContext = dynjsBuiltin.getRuntime().getGlobalContext();

    		globalObject.defineOwnProperty(null, "__log", PropertyDescriptor.newDataPropertyDescriptor(getLogger(), true, true, false), false);
			globalObject.defineOwnProperty(null, "net", PropertyDescriptor.newDataPropertyDescriptor(new JavaPackage(globalContext, "net"), true, true, true), false);
		
			try {
				int exitCode = nodyn.run();
				if (exitCode != 0) {
					throw new IOException("Failed to load wolfscript language engine");
				}

	
			} catch (Throwable t) {
				t.printStackTrace();
				System.out.println(t.getMessage());
				throw new IOException("Failed to load wolfscript language engine", t);
			}
		}

	}
	
	public void setGlobal(String name, Object value, boolean writable, boolean configurable, boolean enumerable)
	{
	
	}
	
   public Object getGlobal(String name)
    {
    	return 	globalObject.get(null, name);
    }
    
    public Object call(JSFunction jsfunction, Object self, Object... args)
    {
    	return this.runtime.getDefaultExecutionContext().call(jsfunction, self, args);
    }
    
    public final Logger getLogger() {
        return logger;
    }
}