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
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.StringElement;
import com.allanbank.mongodb.bson.element.SymbolElement;
import com.google.code.morphia.mapping.MappedField;
import com.google.code.morphia.mapping.MappingException;

/**
 * Converter for and from {@link Class} values.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 * @copyright 2013, Uwe Schaefer, scotthernandez and Allanbank Consulting, Inc.,
 *            All Rights Reserved
 */
@SuppressWarnings({ "rawtypes" })
public class ClassConverter extends TypeConverter<Class<?>> implements
        SimpleValueConverter {

    /**
     * Creates a new ClassConverter.
     */
    public ClassConverter() {
        super(Class.class);
    }

    @Override
    public Class<?> decode(Class targetClass, Element val,
            MappedField optionalExtraInfo) throws MappingException {
        if ((val == null) || (val.getType() == ElementType.NULL)) {
            return null;
        }

        String name = null;
        if (val.getType() == ElementType.STRING) {
            name = ((StringElement) val).getValue();
        }
        else if (val.getType() == ElementType.SYMBOL) {
            name = ((SymbolElement) val).getSymbol();
        }

        if (name != null) {
            try {
                return Class.forName(name);
            }
            catch (ClassNotFoundException e) {
                throw new MappingException("Cannot create class from name '"
                        + name + "'", e);
            }
        }

        throw new MappingException("Could not figure out how to map a "
                + val.getClass().getSimpleName() + " into a Class.");
    }

    @Override
    public void encode(DocumentBuilder builder, String name, Class<?> value,
            MappedField optionalExtraInfo) {
        if (value == null) {
            builder.addNull(optionalExtraInfo.getNameToStore());
        }
        else {
            builder.add(name, value.getName());
        }
    }
}
