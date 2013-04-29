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
package com.google.code.morphia.converters;

import com.allanbank.mongodb.bson.Element;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * FieldConverter provides an interface for the converters of fields to
 * implement.
 * 
 * @param <T>
 *            The type mapped by this converter.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public interface FieldConverter<T> {

    /**
     * Returns true if this converter can be used to convert an object of the
     * {@code mappingType};
     * 
     * @param clazz
     *            The information on the outer class that is being converted.
     * @param field
     *            Details on the field that is being converted.
     * @return True if this converter
     */
    public boolean canConvert(MappedClass clazz, MappedField field);

    /**
     * Converts an element into the specified object type.
     * 
     * @param clazz
     *            The information on the outer class that is being converted.
     * @param field
     *            Details on the field that is being converted.
     * @param element
     *            The element containing the mapped type.
     * @return The converted object.
     */
    public T fromElement(MappedClass clazz, MappedField field, Element element);

    /**
     * Converts the object to an element of the specified type.
     * 
     * @param clazz
     *            The information on the outer class that is being converted.
     * @param field
     *            Details on the field that is being converted.
     * @param name
     *            The name of the object to create into an element.
     * @param value
     *            The object to convert.
     * @return The element wrapping the name and value.
     */
    public Element toElement(MappedClass clazz, MappedField field, String name,
            T value);
}
