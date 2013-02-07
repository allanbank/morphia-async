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

import java.util.HashMap;
import java.util.Map;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.DocumentElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.allanbank.mongodb.bson.element.StringElement;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * Converts a {@link Map} into a {@link DocumentElement}.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @copyright 2010-2013, Uwe Schaefer, Scott Hernandez and Allanbank Consulting,
 *            Inc., All Rights Reserved
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapConverter implements FieldConverter<Map<?, ?>> {

    /**
     * The class for the {@link HashMap} which is used as the default
     * {@link Map} implementation.
     */
    public static final Class<HashMap> HASH_MAP_CLASS = HashMap.class;

    /** The {@link Map} interface. */
    public static final Class<Map> MAP_CLASS = Map.class;

    /** The converter for sub fields. */
    private final CachingFieldConverter converter;

    /**
     * Creates a new MapConverter.
     * 
     * @param converter
     *            The converter for sub fields.
     */
    public MapConverter(CachingFieldConverter converter) {
        this.converter = converter;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return true if the field is of type {@link Map}.
     * </p>
     */
    @Override
    public boolean canConvert(MappedClass clazz, MappedField field) {
        return MAP_CLASS.isAssignableFrom(field.getResolvedClass());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@link Map} field into a
     * {@link DocumentElement}.
     * </p>
     */
    @Override
    public Element toElement(MappedClass clazz, MappedField field, String name,
            Map<?, ?> value) {
        if (value == null) {
            return new NullElement(name);
        }

        MappedField keyField = new MappedField();
        MappedField valueField = new MappedField();

        keyField.mapFor("key", field.getTypeArgumentClass(0));
        valueField.mapFor("value", field.getTypeArgumentClass(1));

        DocumentBuilder subDoc = BuilderFactory.start();
        for (Map.Entry entry : value.entrySet()) {
            // Resolve the key string to use.
            Element key = converter.toElement(clazz, keyField, "key",
                    entry.getKey());

            String subName = key.getValueAsString();
            subDoc.add(converter.toElement(clazz, valueField, subName,
                    entry.getValue()));
        }

        return new DocumentElement(name, subDoc.build());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@link DocumentElement} back into a map.
     * </p>
     */
    @Override
    public Map<?, ?> fromElement(MappedClass clazz,
            com.google.code.morphia.state.MappedField field, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.DOCUMENT) {
            MappedField keyField = new MappedField();
            MappedField valueField = new MappedField();

            keyField.mapFor("key", field.getTypeArgumentClass(0));
            valueField.mapFor("value", field.getTypeArgumentClass(1));

            final Map<Object, Object> values = newMap(field.getResolvedClass(),
                    HASH_MAP_CLASS);

            for (Element e : ((DocumentElement) element)) {

                Object key = converter.fromElement(clazz, keyField,
                        new StringElement("key", e.getName()));
                Object value = converter.fromElement(clazz, valueField, e);

                values.put(key, value);
            }
            return values;
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a Map.");
    }

    /**
     * Creates an object of the specified type or if that fails the fallback
     * type.
     * 
     * @param type
     *            The type to create a new instance of.
     * @param fallbackType
     *            The fallback type to create.
     * @return The object created.
     */
    private Map<Object, Object> newMap(Class<?> type,
            final Class<? extends Map> fallbackType) {
        try {
            return (Map<Object, Object>) type.newInstance();
        }
        catch (Exception error) {
            try {
                return fallbackType.newInstance();
            }
            catch (InstantiationException e) {
                throw new MappingException(
                        "Could not create a object of type '"
                                + fallbackType.getSimpleName() + "'.", e);
            }
            catch (IllegalAccessException e) {
                throw new MappingException(
                        "Could not create a object of type '"
                                + fallbackType.getSimpleName() + "'.", e);
            }
        }
    }

}