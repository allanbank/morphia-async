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
 * FieldConverter to and from double[] values.
 * 
 * @copyright 2010-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class DoubleArrayConverter extends AbstractBasicFieldConverter<double[]> {

    /**
     * Creates a new DoubleConverter.
     */
    public DoubleArrayConverter() {
        super(double[].class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code ellement} into a {@link Double} value.
     * </p>
     */
    @Override
    public double[] fromElement(final Class<?> mappingType,
            final Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.ARRAY) {
            final DoubleConverter converter = new DoubleConverter();

            final ArrayElement array = (ArrayElement) element;
            final List<Element> entries = array.getEntries();

            final double[] result = new double[entries.size()];
            for (int i = 0; i < result.length; ++i) {
                final Double value = converter.fromElement(mappingType,
                        entries.get(i));
                if (value != null) {
                    result[i] = value.doubleValue();
                }
                else {
                    throw new MappingException(
                            "Cannot have a null value in a double[].");
                }
            }
            return result;
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a double[].");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link ArrayElement}.
     * </p>
     */
    @Override
    public Element toElement(final Class<?> mappingType, final String name,
            final double[] object) {
        if (object == null) {
            return new NullElement(name);
        }

        final DoubleElement[] elements = new DoubleElement[object.length];
        for (int i = 0; i < object.length; ++i) {
            elements[i] = new DoubleElement(String.valueOf(i), object[i]);
        }
        return new ArrayElement(name, elements);
    }
}
