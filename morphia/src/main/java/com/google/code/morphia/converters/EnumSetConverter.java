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
package com.google.code.morphia.converters;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.ArrayBuilder;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.element.ArrayElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.state.MappedClass;

/**
 * Converter for the {@link EnumSet} fields.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author Scott Hernandez
 * @copyright 2010-2013, Uwe Schaefer, Scott Hernandez and Allanbank Consulting,
 *            Inc., All Rights Reserved
 */
public class EnumSetConverter implements
        FieldConverter<EnumSet<? extends Enum<?>>> {

    /** The {@link EnumSet} class. */
    @SuppressWarnings("rawtypes")
    public static final Class<EnumSet> ENUM_SET_CLASS = EnumSet.class;

    /**
     * Creates a new EnumSetConverter.
     */
    public EnumSetConverter() {
        super();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to verify that the field's type is an {@link EnumSet} and the
     * type parameter {@link Enum} is known.
     * </p>
     */
    @Override
    public boolean canConvert(MappedClass clazz,
            com.google.code.morphia.state.MappedField field) {
        Class<?> type = field.getResolvedClass();
        Class<?> typeParam = field.getTypeArgumentClass(0);

        return ENUM_SET_CLASS.isAssignableFrom(type) && (typeParam != null)
                && typeParam.isEnum();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert an {@link ArrayElement} back to an {@link EnumSet}.
     * </p>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public EnumSet<? extends Enum<?>> fromElement(MappedClass clazz,
            com.google.code.morphia.state.MappedField field, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }

        if (canConvert(clazz, field)) {

            Class enumType = field.getTypeArgumentClass(0);
            if (element.getType() == ElementType.ARRAY) {
                ArrayElement ae = (ArrayElement) element;
                List<Enum> values = new ArrayList<Enum>();
                for (Element e : ae.getEntries()) {
                    values.add(Enum.valueOf(enumType, e.getValueAsString()));
                }

                if (values.isEmpty()) {
                    return EnumSet.noneOf(enumType);
                }
                return EnumSet.copyOf(values);
            }
        }
        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into an EnumSet for "
                + field.getResolvedClass().getSimpleName());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert {@link EnumSet} into an {@link ArrayElement} of
     * {@link Enum#name() names}.
     * </p>
     */
    @Override
    public Element toElement(MappedClass clazz,
            com.google.code.morphia.state.MappedField field, String name,
            EnumSet<? extends Enum<?>> value) {
        if (value == null) {
            return new NullElement(name);
        }

        ArrayBuilder ab = BuilderFactory.startArray();
        for (Enum<?> e : value) {
            ab.add(e.name());
        }
        return new ArrayElement(name, ab.build());
    }
}
