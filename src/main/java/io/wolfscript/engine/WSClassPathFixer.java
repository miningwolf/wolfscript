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
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;

/* 
 * Lifecycle manager for a WolfScript plugin, host agnostic
 *
 * @author miningwolf
 */
public class WSClassPathFixer {
	
	protected static boolean fixed = false;
	
 	public static void fix() throws Exception {
 	 
 	 	if (fixed == false) {
	 	    
	 	    // Add Current JAR to System Class Loader so that Nodyn Vertx ServiceLoader works
            URL url = WSClassPathFixer.class.getProtectionDomain().getCodeSource().getLocation();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class}); 
            method.setAccessible(true); 
            method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{url}); 
            
            fixed = true;
 	 	}
            
	}
} 