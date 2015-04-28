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

package io.wolfscript.plugin;

import java.lang.reflect.Field;

/**
 * @author MiningWolf
 *
 */
public class ReflectionHelper {

    /**
     * Set a private value. 
     * @param obj object to set the field in
     * @param fieldName name of field to set
     * @param newValue new value to set the field to
     * @throws IllegalArgumentException can be thrown when setting the field
     * @throws IllegalAccessException can be thrown when setting the field
     * @throws SecurityException can be thrown when retrieving the field object
     * @throws NoSuchFieldException can be thrown when retrieving the field object
     */
    public static void setPrivateValue(Object obj, String fieldName, Object newValue) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
        setPrivateValue(obj.getClass(), obj, fieldName, newValue);
    }

    /**
     * Set a private value.
     * @param containingClass class containing the field
     * @param obj object to set the field in
     * @param fieldName name of field to set
     * @param newValue new value to set the field to
     * @throws IllegalArgumentException can be thrown when setting the field
     * @throws IllegalAccessException can be thrown when setting the field
     * @throws SecurityException can be thrown when retrieving the field object
     * @throws NoSuchFieldException can be thrown when retrieving the field object
     */
    public static void setPrivateValue(Class<?> containingClass, Object obj, String fieldName, Object newValue) throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
        Field field = containingClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, newValue);
    }
}