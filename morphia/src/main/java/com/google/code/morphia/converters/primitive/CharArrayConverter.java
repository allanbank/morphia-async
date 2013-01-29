/*
 *         Copyright 2010-2013 Uwe Schaefer, Scott Hernandez 
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
package com.google.code.morphia.converters.primitive;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.element.NullElement;
import com.allanbank.mongodb.bson.element.StringElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * Converter for and from {@code char[]} values.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author Scott Hernandez
 * @copyright 2010-2013, Uwe Schaefer, Scott Hernandez and Allanbank Consulting,
 *            Inc., All Rights Reserved
 */
public class CharArrayConverter extends AbstractConverter<char[]> {

    /**
     * Creates a new CharArrayConverter.
     */
    public CharArrayConverter() {
        super(char[].class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link StringElement}.
     * </p>
     */
    @Override
    public Element toElement(Class<?> mappingType, String name, char[] object) {
        if (object == null) {
            return new NullElement(name);
        }
        return new StringElement(name, String.valueOf(object));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the element's value as a {@code char[]}.
     * </p>
     */
    @Override
    public char[] fromElement(Class<?> mappingType, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if ((element.getType() == ElementType.STRING)
                || (element.getType() == ElementType.SYMBOL)) {
            String sVal = element.getValueAsString();

            return sVal.toCharArray();
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a char[].");
    }
}
