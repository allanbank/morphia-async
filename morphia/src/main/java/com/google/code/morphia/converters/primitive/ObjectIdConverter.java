/*
 *         Copyright 2010-2013 Scott Hernandez
 *            and Allanbank Consulting, Inc.
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
package com.google.code.morphia.converters.primitive;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.element.NullElement;
import com.allanbank.mongodb.bson.element.ObjectId;
import com.allanbank.mongodb.bson.element.ObjectIdElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * Convert to an ObjectId from string
 * 
 * @copyright 2010-2013, Scott Hernandez and Allanbank Consulting, Inc., All
 *            Rights Reserved
 */
public class ObjectIdConverter extends AbstractBasicFieldConverter<ObjectId> {

    /**
     * Creates a new ObjectIdConverter.
     */
    public ObjectIdConverter() {
        super(ObjectId.class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link ObjectIdElement}.
     * </p>
     */
    @Override
    public Element toElement(Class<?> mappingType, String name, ObjectId object) {
        if (object == null) {
            return new NullElement(name);
        }
        return new ObjectIdElement(name, object);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the element's value as a {@link ObjectId}.
     * </p>
     */
    @Override
    public ObjectId fromElement(Class<?> mappingType, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.OBJECT_ID) {
            return ((ObjectIdElement) element).getId();
        }
        else if ((element.getType() == ElementType.STRING)
                || (element.getType() == ElementType.SYMBOL)) {
            String sVal = element.getValueAsString();

            if (sVal.startsWith("ObjectId('")) {
                sVal = sVal.substring("ObjectId('".length());
            }
            if (sVal.endsWith("')")) {
                sVal = sVal.substring(0, sVal.length() - 2);
            }
            try {
                return new ObjectId(element.getValueAsString());
            }
            catch (IllegalArgumentException e) {
                throw new MappingException("Invalid ObjectId string '"
                        + element.getValueAsString() + "'.", e);
            }
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into an ObjectId.");
    }
}
