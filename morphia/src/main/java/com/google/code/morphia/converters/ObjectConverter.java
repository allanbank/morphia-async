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

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.element.DocumentElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * ObjectConverter provides mapping of a object into a {@link DocumentElement}.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class ObjectConverter implements FieldConverter<Object> {

    /** The converter for the sub documents. */
    private final Converter converter;

    /**
     * Creates a new ObjectConverter.
     * 
     * @param converter
     *            The converter for the sub documents.
     */
    public ObjectConverter(Converter converter) {
        this.converter = converter;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to return true.
     * </p>
     */
    @Override
    public boolean canConvert(MappedClass clazz, MappedField field) {
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to convert the value into a document.
     * </p>
     */
    @Override
    public Element toElement(MappedClass clazz, MappedField field, String name,
            Object value) {
        if (value == null) {
            return new NullElement(name);
        }

        // TODO - replace with a reference. Maybe a different converter.
        return new DocumentElement(name, converter.toDocument(value));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to convert the document element back to an object.
     * </p>
     */
    @Override
    public Object fromElement(MappedClass clazz, MappedField field,
            Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.DOCUMENT) {
            return converter.fromDocument(field.getResolvedClass(),
                    ((DocumentElement) element).asDocument());
        }
        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into an Object for "
                + field.getResolvedClass().getSimpleName());
    }

}
