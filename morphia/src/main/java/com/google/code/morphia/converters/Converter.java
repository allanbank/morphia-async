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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedClassCache;
import com.google.code.morphia.state.MappedField;
import com.google.code.morphia.state.MappedField.Strategy;

/**
 * Converter provides the ability to convert {@link Object}s to and from MongoDB
 * {@link Document}s.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class Converter {

    /** The cache of mapped classes. */
    private final MappedClassCache cache;

    /** The converter for the fields in the object. */
    private final FieldConverter<Object> fieldConverter;

    /**
     * Creates a new Converter.
     * 
     * @param cache
     *            The cache of class mapping information.
     */
    public Converter(MappedClassCache cache) {
        this.cache = cache;
        this.fieldConverter = new CachingFieldConverter(this);
    }

    /**
     * Maps the document to an object.
     * 
     * @param object
     *            The object to create an object from.
     * @return The object converted to a document.
     */
    public Document toDocument(Object object) {
        MappedClass mapping = cache.getMappingFor(object.getClass());

        DocumentBuilder builder = BuilderFactory.start();
        MappedField id = mapping.getIdField();
        if (id != null) {
            if (id.isWritten()) {
                fieldConverter.toElement(mapping, id, "_id", id.get(object));
            }
        }

        MappedField version = mapping.getVersionField();
        if (version != null) {
            if (version.isWritten()) {
                builder.add(fieldConverter.toElement(mapping, version,
                        version.getMappedFieldName(), version.get(object)));
            }
        }

        if (mapping.isClassnameStored()) {
            builder.add(MappedClass.CLASS_NAME_FIELD, object.getClass()
                    .getCanonicalName());
        }

        for (MappedField field : mapping.getFields()) {
            if (field.isWritten()) {
                builder.add(fieldConverter.toElement(mapping, field,
                        field.getMappedFieldName(), field.get(object)));
            }
        }

        return builder.build();
    }

    /**
     * Returns the {@link MappedClassCache}.
     * 
     * @return The {@link MappedClassCache}.
     */
    public MappedClassCache getCache() {
        return cache;
    }

    /**
     * Creates an object of the specified type from the document provided.
     * 
     * @param type
     *            The type of object contained in the document.
     * @param document
     *            The document to deserialize into the object.
     * @return The object filled from the document.
     */
    public Object fromDocument(Class<?> type, Document document) {
        MappedClass mapping = cache.getMappingFor(type);

        Object object = null;

        Class<?> clazz = mapping.determineClassFor(document);
        mapping = cache.getMappingFor(clazz);
        try {
            // TODO - Support constructor args.
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            object = constructor.newInstance();

            MappedField id = mapping.getIdField();
            MappedField version = mapping.getVersionField();

            for (Element element : document) {
                String name = element.getName();
                if ((id != null) && "_id".equals(element.getName())) {
                    if (id.getStrategy() != Strategy.NONE) {
                        id.set(object, fieldConverter.fromElement(mapping, id,
                                element));
                    }
                }
                else if ((version != null)
                        && version.getMappedFieldName().equals(name)) {
                    if (version.getStrategy() != Strategy.NONE) {
                        version.set(object, fieldConverter.fromElement(mapping,
                                version, element));
                    }
                }
                else if (!MappedClass.CLASS_NAME_FIELD.equals(name)) {
                    // Search for the field by name.
                    MappedField field = mapping.findField(name);
                    if ((field != null)
                            && (field.getStrategy() != Strategy.NONE)) {
                        field.set(object, fieldConverter.fromElement(mapping,
                                field, element));
                    }
                }
            }
        }
        catch (InstantiationException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }
        catch (IllegalAccessException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }
        catch (SecurityException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }
        catch (NoSuchMethodException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }
        catch (IllegalArgumentException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }
        catch (InvocationTargetException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }

        return object;
    }
}
