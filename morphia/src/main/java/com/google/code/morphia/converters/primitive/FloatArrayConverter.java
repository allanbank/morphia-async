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
import com.allanbank.mongodb.bson.element.DoubleElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * FieldConverter to and from float[] values.
 * 
 * @copyright 2010-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class FloatArrayConverter extends AbstractBasicFieldConverter<float[]> {

    /**
     * Creates a new FloatConverter.
     */
    public FloatArrayConverter() {
        super(float[].class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link ArrayElement}.
     * </p>
     */
    @Override
    public Element toElement(Class<?> mappingType, String name, float[] object) {
        if (object == null) {
            return new NullElement(name);
        }

        DoubleElement[] elements = new DoubleElement[object.length];
        for (int i = 0; i < object.length; ++i) {
            elements[i] = new DoubleElement(String.valueOf(i), object[i]);
        }
        return new ArrayElement(name, elements);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code ellement} into a {@link Float} value.
     * </p>
     */
    @Override
    public float[] fromElement(Class<?> mappingType, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.ARRAY) {
            FloatConverter converter = new FloatConverter();

            ArrayElement array = (ArrayElement) element;
            List<Element> entries = array.getEntries();

            float[] result = new float[entries.size()];
            for (int i = 0; i < result.length; ++i) {
                Float value = converter
                        .fromElement(mappingType, entries.get(i));
                if (value != null) {
                    result[i] = value.floatValue();
                }
                else {
                    throw new MappingException(
                            "Cannot have a null value in a float[].");
                }
            }
            return result;
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a float[].");
    }
}
