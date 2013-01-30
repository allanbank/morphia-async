/*
 *         Copyright 2010-2013 Uwe Schaefer, Scott Hernandez
 *                   and Allanbank Consulting, Inc.
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
package com.google.code.morphia.converters.primitive;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.NumericElement;
import com.allanbank.mongodb.bson.element.DoubleElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * FieldConverter to and from double values.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author Scott Hernandez
 * @copyright 2010-2013, Uwe Schaefer, Scott Hernandez and Allanbank Consulting,
 *            Inc., All Rights Reserved
 */
public class DoubleConverter extends AbstractBasicFieldConverter<Double> {

    /**
     * Creates a new DoubleConverter.
     */
    public DoubleConverter() {
        super(double.class, Double.class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link DoubleElement}.
     * </p>
     */
    @Override
    public Element toElement(Class<?> mappingType, String name, Double object) {
        if (object == null) {
            return new NullElement(name);
        }
        return new DoubleElement(name, object.doubleValue());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the element's value as a {@link Double}.
     * </p>
     */
    @Override
    public Double fromElement(Class<?> mappingType, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.DOUBLE) {
            double value = ((DoubleElement) element).getDoubleValue();
            return Double.valueOf(value);
        }
        else if (element instanceof NumericElement) {
            double value = ((NumericElement) element).getDoubleValue();
            return Double.valueOf(value);
        }
        else if ((element.getType() == ElementType.STRING)
                || (element.getType() == ElementType.SYMBOL)) {
            try {
                return Double.valueOf(element.getValueAsString());
            }
            catch (NumberFormatException e) {
                throw new MappingException("Invalid double string name '"
                        + element.getValueAsString() + "'.", e);
            }
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a Double.");
    }
}
