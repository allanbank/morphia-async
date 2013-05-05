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
import com.allanbank.mongodb.bson.DocumentReference;
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
    public Converter(final MappedClassCache cache) {
        this.cache = cache;
        this.fieldConverter = new CachingFieldConverter(this);
    }

    /**
     * Returns the {@code id} value wrapped as a {@link DocumentReference} based
     * on the mapping for the id field of the {@code clazz}.
     * 
     * @param clazz
     *            The class containing the id value.
     * @param id
     *            The id value to map.
     * @return The {@link DocumentReference} for an object with the specified
     *         id.
     */
    public <T, V> DocumentReference toDocumentReference(Class<T> clazz, V id) {
        // TODO - Implement.
        return null;
    }

    /**
     * Returns a DocumentReference for the entity.
     * 
     * @param entity
     *            The entity to create a document reference for.
     * @return The {@link DocumentReference} for an object with the specified
     *         id.
     */
    public <T> DocumentReference toDocumentReference(T entity) {
        // TODO - Implement.
        return null;
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
    @SuppressWarnings("unchecked")
    public <T> T fromDocument(final Class<T> type, final Document document) {
        MappedClass mapping = cache.getMappingFor(type);

        Object object = null;

        final Class<?> clazz = mapping.determineClassFor(document);
        mapping = cache.getMappingFor(clazz);
        try {
            // TODO - Support constructor args.
            final Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            object = constructor.newInstance();

            final MappedField id = mapping.getIdField();
            final MappedField version = mapping.getVersionField();

            for (final Element element : document) {
                final String name = element.getName();
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
                    final MappedField field = mapping.findField(name);
                    if ((field != null)
                            && (field.getStrategy() != Strategy.NONE)) {
                        field.set(object, fieldConverter.fromElement(mapping,
                                field, element));
                    }
                }
            }
        }
        catch (final InstantiationException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }
        catch (final IllegalAccessException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }
        catch (final SecurityException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }
        catch (final NoSuchMethodException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }
        catch (final IllegalArgumentException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }
        catch (final InvocationTargetException e) {
            throw new MappingException("Could not create an instance of the '"
                    + clazz.getCanonicalName() + "' class.", e);
        }

        return (T) object;
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
     * Maps the document to an object.
     * 
     * @param object
     *            The object to create an object from.
     * @return The object converted to a document.
     */
    public Document toDocument(final Object object) {
        final MappedClass mapping = cache.getMappingFor(object.getClass());

        final DocumentBuilder builder = BuilderFactory.start();
        final MappedField id = mapping.getIdField();
        if (id != null) {
            if (id.isWritten()) {
                builder.add(fieldConverter.toElement(mapping, id, "_id",
                        id.get(object)));
            }
        }

        final MappedField version = mapping.getVersionField();
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

        for (final MappedField field : mapping.getFields()) {
            if (field.isWritten() && (field != id) && (field != version)) {
                builder.add(fieldConverter.toElement(mapping, field,
                        field.getMappedFieldName(), field.get(object)));
            }
        }

        return builder.build();
    }
}
