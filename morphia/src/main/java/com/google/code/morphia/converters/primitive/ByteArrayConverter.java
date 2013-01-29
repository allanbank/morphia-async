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

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.element.BinaryElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * Converter to and from byte[] values.
 * 
 * @copyright 2010-2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class ByteArrayConverter extends AbstractConverter<byte[]> {

    /**
     * Creates a new IntegerConverter.
     */
    public ByteArrayConverter() {
        super(byte[].class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link BinaryElement}.
     * </p>
     */
    @Override
    public Element toElement(Class<?> mappingType, String name, byte[] object) {
        if (object == null) {
            return new NullElement(name);
        }

        return new BinaryElement(name, object);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code element} into a byte[] value.
     * </p>
     */
    @Override
    public byte[] fromElement(Class<?> mappingType, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.BINARY) {
            return ((BinaryElement) element).getValue();
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " byteo a byte[].");
    }
}
