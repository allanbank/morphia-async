/*
 *         Copyright 2010-2013 stummb, Scott Hernandez
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

import java.util.UUID;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.element.NullElement;
import com.allanbank.mongodb.bson.element.UuidElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * Converts values to and from UUIDs.
 * <p>
 * Provided by http://code.google.com/p/morphia/issues/detail?id=320
 * </p>
 * 
 * @author stummb
 * @author Scott Hernandez
 * @copyright 2010-2013, stummb, Scott Hernandez and Allanbank Consulting, Inc.,
 *            All Rights Reserved
 */
public class UUIDConverter extends AbstractBasicFieldConverter<UUID> {

    /**
     * Creates a new UUIDConverter.
     */
    public UUIDConverter() {
        super(UUID.class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the element's value as a {@link Short}.
     * </p>
     */
    @Override
    public UUID fromElement(final Class<?> mappingType, final Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element instanceof UuidElement) {
            return ((UuidElement) element).getUuid();
        }
        else if ((element.getType() == ElementType.STRING)
                || (element.getType() == ElementType.SYMBOL)) {
            String sVal = element.getValueAsString();

            if (sVal.startsWith("UUID(")) {
                sVal = sVal.substring("UUID(".length());
            }
            if (sVal.endsWith(")")) {
                sVal = sVal.substring(0, sVal.length() - 1);
            }

            return UUID.fromString(sVal);
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a UUID.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link UuidElement}.
     * </p>
     */
    @Override
    public Element toElement(final Class<?> mappingType, final String name,
            final UUID object) {
        if (object == null) {
            return new NullElement(name);
        }
        return new UuidElement(name, object);
    }

}