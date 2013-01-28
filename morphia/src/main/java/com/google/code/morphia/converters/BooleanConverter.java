/*
 *         Copyright 2013 Uwe Schaefer, Scott Hernandez 
 *               and Allanbank Consulting, Inc.
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
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.NumericElement;
import com.allanbank.mongodb.bson.element.BooleanElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * Converter to and from Boolean values.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author Scott Hernandez
 * @copyright 2013, Uwe Schaefer, scotthernandez and Allanbank Consulting, Inc.,
 *            All Rights Reserved
 */
public class BooleanConverter extends AbstractConverter<Boolean> {

    /**
     * Creates a new BooleanConverter.
     */
    public BooleanConverter() {
        super(boolean.class, Boolean.class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link BooleanElement}.
     * </p>
     */
    @Override
    public Element toElement(Class<?> mappingType, String name, Boolean object) {
        if (object == null) {
            return new NullElement(name);
        }
        return new BooleanElement(name, object.booleanValue());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code ellement} into a {@link Boolean} value.
     * </p>
     */
    @Override
    public Boolean fromElement(Class<?> mappingType, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.BOOLEAN) {
            return Boolean.valueOf(((BooleanElement) element).getValue());
        }
        else if (element instanceof NumericElement) {
            // handle the case for things like the ok field
            return Boolean
                    .valueOf(((NumericElement) element).getIntValue() != 0);
        }
        // Handle strings for cases when the boolean is the key
        else if ((element.getType() == ElementType.STRING)
                || (element.getType() == ElementType.SYMBOL)) {
            String sVal = element.getValueAsString();
            return Boolean.valueOf(Boolean.parseBoolean(sVal));
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a boolean.");
    }
}
