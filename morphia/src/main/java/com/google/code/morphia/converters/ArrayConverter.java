/*
 *         Copyright 2010-2013 Allanbank Consulting, Inc.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package com.google.code.morphia.converters;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import com.allanbank.mongodb.bson.Element;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * Converts arrays of objects. Note that this class does not need to handle
 * arrays of primatives as they are handled by the type specific primative
 * converters.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
@SuppressWarnings({ "unchecked" })
public class ArrayConverter extends IterableConverter {

    /**
     * Creates a new ArrayConverter.
     * 
     * @param fieldConverter
     *            The converters for sub-fields.
     */
    public ArrayConverter(final CachingFieldConverter fieldConverter) {
        super(fieldConverter);
    }

    /**
     * 
     * {@inheritDoc}
     * <p>
     * Overridden to test if the field is an array.
     * </p>
     */
    @Override
    public boolean canConvert(final MappedClass clazz, final MappedField field) {
        final Class<?> type = field.getResolvedClass();

        return type.isArray();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the list from the base class into an array.
     * </p>
     */
    @Override
    public Object fromElement(final MappedClass clazz, final MappedField field,
            final Element element) {

        final Class<?> type = field.getResolvedClass().getComponentType();

        // Base class does the heavy lifting.
        final List<Object> values = (List<Object>) super.fromElement(clazz,
                field, element);
        final Object exampleArray = Array.newInstance(type, values.size());
        try {
            return values.toArray((Object[]) exampleArray);
        }
        catch (final ClassCastException e) {
            // Use a vanilla array.
            return values.toArray();
        }
    }

    /**
     * Converts the value into an iterable.
     * 
     * @param value
     *            The value to convert.
     * @return The value as an {@link Iterable}.
     */
    @Override
    protected Iterable<?> toIterable(final Object value) {
        return Arrays.asList((Object[]) value);
    }
}
