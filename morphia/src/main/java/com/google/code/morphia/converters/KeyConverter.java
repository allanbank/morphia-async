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

import com.allanbank.mongodb.bson.DocumentReference;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.element.DocumentElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.Key;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedClassCache;

/**
 * Converter for the {@link Key} fields.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author Scott Hernandez
 * @copyright 2010-2013, Uwe Schaefer, Scott Hernandez and Allanbank Consulting,
 *            Inc., All Rights Reserved
 */
@SuppressWarnings({ "rawtypes" })
public class KeyConverter implements FieldConverter<Key> {

    /** The {@link Key} class. */
    public static final Class<Key> KEY_CLASS = Key.class;

    /** The cache of mapped classes. Used to resolve the collections names. */
    private final MappedClassCache mappedClassCache;

    /**
     * Creates a new KeyConverter.
     * 
     * @param mappedClassCache
     *            The cache of mapped classes.
     */
    public KeyConverter(MappedClassCache mappedClassCache) {
        this.mappedClassCache = mappedClassCache;
    }

    /**
     * Converts a {@link DocumentReference} into a {@link Key}.
     * 
     * @param ref
     *            The reference to convert.
     * @return The converted reference.
     */
    protected <T> Key<T> refToKey(DocumentReference ref) {
        if (ref == null)
            return null;
        Key<T> key = new Key<T>(ref.getCollectionName(), ref.getId());
        return key;
    }

    /**
     * Converts a {@link Key} into a {@link DocumentReference}.
     * 
     * @param key
     *            The {@link Key} to convert.
     * @return The converted key.
     */
    protected DocumentReference keyToRef(Key key) {
        if (key == null)
            return null;
        if (key.getKindClass() == null && key.getKind() == null)
            throw new IllegalStateException(
                    "The key must contain either a class of a collection name.");
        if (key.getKind() == null) {
            key.setKind(mappedClassCache.getMappingFor(key.getKindClass())
                    .getCollectionName());
        }

        Element idElement = BuilderFactory.start().add("_id", key.getId())
                .build().get("_id");
        return new DocumentReference(key.getKind(), idElement);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return true if the field is a {@link Key} type.
     * </p>
     */
    @Override
    public boolean canConvert(MappedClass clazz,
            com.google.code.morphia.state.MappedField field) {
        return KEY_CLASS.isAssignableFrom(field.getResolvedClass());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return a {@link DocumentElement} containing the key as a
     * {@link DocumentReference} or DBRef.
     * </p>
     */
    @Override
    public Element toElement(MappedClass clazz,
            com.google.code.morphia.state.MappedField field, String name,
            Key value) {
        if (value == null) {
            return new NullElement(name);
        }

        return new DocumentElement(name, keyToRef(value).asDocument());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert a {@link DocumentElement} containing a
     * {@link DocumentReference} into a {@link Key}.
     * </p>
     */
    @Override
    public Key fromElement(MappedClass clazz,
            com.google.code.morphia.state.MappedField field, Element element) {
        DocumentReference ref = null;
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.DOCUMENT) {
            ref = ((DocumentElement) element).asDocumentReference();
        }

        if (ref == null) {
            throw new ConverterException(
                    String.format(
                            "Cannot convert %s to Key because it isn't a DocumentReference",
                            element.toString()));
        }
        return refToKey(ref);
    }
}
