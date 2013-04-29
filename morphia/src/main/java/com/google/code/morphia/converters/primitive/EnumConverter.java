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
import com.allanbank.mongodb.bson.element.NullElement;
import com.allanbank.mongodb.bson.element.StringElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * Converts to an from Enum values.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author Scott Hernandez
 * @copyright 2010-2013, Uwe Schaefer, Scott Hernandez and Allanbank Consulting,
 *            Inc., All Rights Reserved
 */
public class EnumConverter implements BasicFieldConverter<Enum<?>> {

    /**
     * Creates a new EnumConverter.
     */
    public EnumConverter() {
        super();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks the input type against the supported type list.
     * </p>
     */
    @Override
    public boolean canConvert(final Class<?> mappingType) {
        return mappingType.isEnum();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the element's value as a {@link Enum}.
     * </p>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Enum<?> fromElement(final Class mappingType, final Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if ((element.getType() == ElementType.STRING)
                || (element.getType() == ElementType.SYMBOL)) {
            try {
                return Enum.valueOf(mappingType, element.getValueAsString());
            }
            catch (final IllegalArgumentException e) {
                throw new MappingException("Invalid double string name '"
                        + element.getValueAsString() + "'.", e);
            }
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a Enum.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link StringElement}.
     * </p>
     */
    @Override
    public Element toElement(final Class<?> mappingType, final String name,
            final Enum<?> object) {
        if (object == null) {
            return new NullElement(name);
        }
        return new StringElement(name, object.name());
    }
}
