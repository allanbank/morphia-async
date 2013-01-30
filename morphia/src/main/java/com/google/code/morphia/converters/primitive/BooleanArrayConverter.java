/*
 *         Copyright 2010-2013 Allanbank Consulting, Inc.
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

import java.util.List;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.element.ArrayElement;
import com.allanbank.mongodb.bson.element.BooleanElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * FieldConverter to and from boolean[] values.
 * 
 * @copyright 2010-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class BooleanArrayConverter extends AbstractBasicFieldConverter<boolean[]> {

    /**
     * Creates a new BooleanConverter.
     */
    public BooleanArrayConverter() {
        super(boolean[].class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link ArrayElement}.
     * </p>
     */
    @Override
    public Element toElement(Class<?> mappingType, String name, boolean[] object) {
        if (object == null) {
            return new NullElement(name);
        }

        BooleanElement[] elements = new BooleanElement[object.length];
        for (int i = 0; i < object.length; ++i) {
            elements[i] = new BooleanElement(String.valueOf(i), object[i]);
        }
        return new ArrayElement(name, elements);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code ellement} into a {@link Boolean} value.
     * </p>
     */
    @Override
    public boolean[] fromElement(Class<?> mappingType, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.ARRAY) {
            BooleanConverter converter = new BooleanConverter();

            ArrayElement array = (ArrayElement) element;
            List<Element> entries = array.getEntries();

            boolean[] result = new boolean[entries.size()];
            for (int i = 0; i < result.length; ++i) {
                Boolean value = converter.fromElement(mappingType,
                        entries.get(i));
                if (value != null) {
                    result[i] = value.booleanValue();
                }
                else {
                    throw new MappingException(
                            "Cannot have a null value in a boolean[].");
                }
            }
            return result;
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a boolean[].");
    }
}
