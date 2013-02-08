/*
 *         Copyright 2013 Allanbank Consulting, Inc.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.allanbank.mongodb.bson.Element;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * CachingFieldConverter provides a one stop converter for fields.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CachingFieldConverter implements FieldConverter<Object> {

    /** The list of known converters. */
    private final List<FieldConverter> converters;

    /** The map of classes to the converter for the type. */
    private final ConcurrentHashMap<Class<?>, FieldConverter> resolved;

    /** The serialization converter. */
    private final SerializedObjectConverter serializeConverter;

    /**
     * Creates a new CachingFieldConverter.
     * 
     * @param converter
     *            The converter for nexted documents/object.
     */
    public CachingFieldConverter(Converter converter) {
        converters = new ArrayList<FieldConverter>();
        resolved = new ConcurrentHashMap<Class<?>, FieldConverter>();
        serializeConverter = new SerializedObjectConverter();

        converters.add(new PrimitiveFieldConverter());
        converters.add(new SerializedObjectConverter());
        converters.add(new KeyConverter(converter.getCache()));
        converters.add(new EnumSetConverter());

        // Possibly recursive.
        converters.add(new MapConverter(this));
        converters.add(new ArrayConverter(this));
        converters.add(new IterableConverter(this));
        converters.add(new ObjectConverter(converter));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return true. If we can't nobody can.
     * </p>
     */
    @Override
    public boolean canConvert(MappedClass clazz, MappedField field) {
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the value using the most suitable converter.
     * </p>
     */
    @Override
    public Element toElement(MappedClass clazz, MappedField field, String name,
            Object value) {
        if (serializeConverter.canConvert(clazz, field)) {
            return serializeConverter.toElement(clazz, field, name,
                    (Serializable) value);
        }

        Class<?> type = field.getResolvedClass();
        FieldConverter converter = resolved.get(type);
        if ((converter != null) && converter.canConvert(clazz, field)) {
            return converter.toElement(clazz, field, name, value);
        }

        for (FieldConverter c : converters) {
            if (c.canConvert(clazz, field)) {
                resolved.put(type, c);
                return c.toElement(clazz, field, name, value);
            }
        }

        throw new MappingException("Don't know how to convert the field '"
                + field.getField().getName() + "' in the type '"
                + clazz.getMappedClass().getCanonicalName() + "' of type '"
                + type.getSimpleName() + "'.");
    }

    @Override
    public Object fromElement(MappedClass clazz, MappedField field,
            Element element) {
        if (serializeConverter.canConvert(clazz, field)) {
            return serializeConverter.fromElement(clazz, field, element);
        }

        Class<?> type = field.getResolvedClass();
        FieldConverter converter = resolved.get(type);
        if ((converter != null) && converter.canConvert(clazz, field)) {
            return converter.fromElement(clazz, field, element);
        }

        for (FieldConverter c : converters) {
            if (c.canConvert(clazz, field)) {
                resolved.put(type, c);
                return c.fromElement(clazz, field, element);
            }
        }

        throw new MappingException("Don't know how to convert the field '"
                + field.getField().getName() + "' in the type '"
                + clazz.getMappedClass().getCanonicalName() + "' of type '"
                + type.getSimpleName() + "'.");
    }

}
