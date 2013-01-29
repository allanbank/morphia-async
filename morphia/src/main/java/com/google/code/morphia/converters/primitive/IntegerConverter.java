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
import com.allanbank.mongodb.bson.element.IntegerElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * Converter to and from integer values.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author Scott Hernandez
 * @copyright 2010-2013, Uwe Schaefer, Scott Hernandez and Allanbank Consulting,
 *            Inc., All Rights Reserved
 */
public class IntegerConverter extends AbstractConverter<Integer> {
    /**
     * Creates a new IntegerConverter.
     */
    public IntegerConverter() {
        super(int.class, Integer.class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link IntegerElement}.
     * </p>
     */
    @Override
    public Element toElement(Class<?> mappingType, String name, Integer object) {
        if (object == null) {
            return new NullElement(name);
        }
        return new IntegerElement(name, object.intValue());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the element's value as a {@link Integer}.
     * </p>
     */
    @Override
    public Integer fromElement(Class<?> mappingType, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.INTEGER) {
            int value = ((IntegerElement) element).getIntValue();
            return Integer.valueOf(value);
        }
        else if (element instanceof NumericElement) {
            int value = ((NumericElement) element).getIntValue();
            return Integer.valueOf(value);
        }
        else if ((element.getType() == ElementType.STRING)
                || (element.getType() == ElementType.SYMBOL)) {
            try {
                return Integer.valueOf(element.getValueAsString());
            }
            catch (NumberFormatException e) {
                throw new MappingException("Invalid integer string name '"
                        + element.getValueAsString() + "'.", e);
            }
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a Integer.");
    }
}
