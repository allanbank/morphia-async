/*
 *         Copyright 2010-2013 Allanbank Consulting, Inc.
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
package com.google.code.morphia.converters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.allanbank.mongodb.bson.Element;
import com.google.code.morphia.converters.primitive.BooleanArrayConverter;
import com.google.code.morphia.converters.primitive.BooleanConverter;
import com.google.code.morphia.converters.primitive.ByteArrayConverter;
import com.google.code.morphia.converters.primitive.CharArrayConverter;
import com.google.code.morphia.converters.primitive.CharacterConverter;
import com.google.code.morphia.converters.primitive.ClassConverter;
import com.google.code.morphia.converters.primitive.BasicFieldConverter;
import com.google.code.morphia.converters.primitive.DateConverter;
import com.google.code.morphia.converters.primitive.DoubleArrayConverter;
import com.google.code.morphia.converters.primitive.DoubleConverter;
import com.google.code.morphia.converters.primitive.EnumConverter;
import com.google.code.morphia.converters.primitive.FloatArrayConverter;
import com.google.code.morphia.converters.primitive.FloatConverter;
import com.google.code.morphia.converters.primitive.IntArrayConverter;
import com.google.code.morphia.converters.primitive.IntegerConverter;
import com.google.code.morphia.converters.primitive.LocaleConverter;
import com.google.code.morphia.converters.primitive.LongArrayConverter;
import com.google.code.morphia.converters.primitive.LongConverter;
import com.google.code.morphia.converters.primitive.ObjectIdConverter;
import com.google.code.morphia.converters.primitive.ShortArrayConverter;
import com.google.code.morphia.converters.primitive.ShortConverter;
import com.google.code.morphia.converters.primitive.StringConverter;
import com.google.code.morphia.converters.primitive.TimestampConverter;
import com.google.code.morphia.converters.primitive.URIConverter;
import com.google.code.morphia.converters.primitive.UUIDConverter;
import com.google.code.morphia.state.MappedField;
import com.google.code.morphia.state.MappedClass;

/**
 * PrimitiveFieldConverter provides a standard converter for the primitive
 * types.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class PrimitiveFieldConverter implements FieldConverter<Object> {

    /** The list of primitive converters to delegate to. */
    private static final List<BasicFieldConverter<?>> ourConverters;

    /** Cache of the type names that are not convertable by this class. */
    private static final Set<String> ourNonPrimitiveTypes;

    /** The cache byt type name of the converter to use. */
    private static final ConcurrentMap<String, BasicFieldConverter<Object>> ourConverterByType;

    static {
        List<BasicFieldConverter<?>> converters = new ArrayList<BasicFieldConverter<?>>();

        converters.add(new BooleanArrayConverter());
        converters.add(new BooleanConverter());
        converters.add(new ByteArrayConverter());
        converters.add(new CharacterConverter());
        converters.add(new CharArrayConverter());
        converters.add(new ClassConverter());
        converters.add(new DateConverter());
        converters.add(new DoubleArrayConverter());
        converters.add(new DoubleConverter());
        converters.add(new EnumConverter());
        converters.add(new FloatArrayConverter());
        converters.add(new FloatConverter());
        converters.add(new IntArrayConverter());
        converters.add(new IntegerConverter());
        converters.add(new LocaleConverter());
        converters.add(new LongArrayConverter());
        converters.add(new LongConverter());
        converters.add(new ObjectIdConverter());
        converters.add(new ShortArrayConverter());
        converters.add(new ShortConverter());
        converters.add(new StringConverter());
        converters.add(new TimestampConverter());
        converters.add(new URIConverter());
        converters.add(new UUIDConverter());

        ourConverters = Collections.unmodifiableList(converters);
        ourNonPrimitiveTypes = Collections
                .newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        ourConverterByType = new ConcurrentHashMap<String, BasicFieldConverter<Object>>();
    }

    /**
     * Creates a new PrimitiveFieldConverter.
     */
    public PrimitiveFieldConverter() {
        super();
    }

    /**
     * Locates the appropriate converter based on the specified type.
     * 
     * @param type
     *            The type to find a converter for.
     * @return The converter for the type if found, or <code>null</code> if not
     *         found.
     */
    @SuppressWarnings("unchecked")
    private BasicFieldConverter<Object> findConverter(Class<?> type) {
        String typeName = type.getCanonicalName();
        if (ourNonPrimitiveTypes.contains(typeName)) {
            return null;
        }

        BasicFieldConverter<Object> converter = ourConverterByType
                .get(typeName);
        if (converter == null) {
            // Have to search.
            for (BasicFieldConverter<?> c : ourConverters) {
                if (c.canConvert(type)) {
                    converter = (BasicFieldConverter<Object>) c;
                    ourConverterByType.put(typeName, converter);
                    break;
                }
            }

            if (converter == null) {
                // Exhausted the list. Mark non-primitive type.
                ourNonPrimitiveTypes.add(typeName);
                return null;
            }
        }
        return converter;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return true if the {@link MappedField#getDeclaredClass()}
     * is convertable by a primative converter, false otherwise.
     * </p>
     */
    @Override
    public boolean canConvert(MappedClass clazz, MappedField field) {
        Class<?> mappedType = field.getResolvedClass();

        return (findConverter(mappedType) != null);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the field based on the appropriate primative
     * converter.
     * </p>
     */
    @Override
    public Element toElement(MappedClass clazz, MappedField field, String name,
            Object object) {
        Class<?> mappedType = field.getResolvedClass();
        BasicFieldConverter<Object> converter = findConverter(mappedType);
        if (converter != null) {
            return converter.toElement(mappedType, name, object);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the field based on the appropriate primative
     * converter.
     * </p>
     */
    @Override
    public Object fromElement(MappedClass clazz, MappedField field,
            Element element) {
        Class<?> mappedType = field.getResolvedClass();

        BasicFieldConverter<Object> converter = findConverter(mappedType);
        if (converter != null) {
            return converter.fromElement(mappedType, element);
        }

        return null;
    }
}
