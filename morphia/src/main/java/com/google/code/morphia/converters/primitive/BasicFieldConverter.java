/*
 *         Copyright 2013 Allanbank Consulting, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.morphia.converters.primitive;

import com.allanbank.mongodb.bson.Element;

/**
 * FieldConverter provides a common interface for all converters that do not
 * need any contextual information to function. This is mainly primitive and
 * objects easily converted to and from primitives.
 * 
 * @param <T>
 *            The type mapped by this converter.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public interface BasicFieldConverter<T> {

    /**
     * Returns true if this converter can be used to convert an object of the
     * {@code mappingType};
     * 
     * @param mappingType
     *            The class of object being converted.
     * @return True if this converter
     */
    public boolean canConvert(Class<?> mappingType);

    /**
     * Converts the object to an element of the specified type.
     * 
     * @param mappingType
     *            The class of object being converted.
     * @param name
     *            The name of the object to create into an element.
     * @param object
     *            The object to convert.
     * @return The element wrapping the name and value.
     */
    public Element toElement(Class<?> mappingType, String name, T object);

    /**
     * Converts an element into the specified object type.
     * 
     * @param mappingType
     *            The class of object being created.
     * @param element
     *            The element containing the mapped type.
     * @return The converted object.
     */
    public T fromElement(Class<?> mappingType, Element element);
}
