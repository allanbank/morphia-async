/*
 *         Copyright 2010-2013   Uwe Schaefer, 
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
package com.google.code.morphia.converters;

import java.io.IOException;
import java.io.Serializable;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.element.BinaryElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.mapping.Serializer;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;
import com.google.code.morphia.state.MappedField.Strategy;

/**
 * Converts a field value into a serialized binary value.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @copyright 2010-2013, Uwe Schaefer and Allanbank Consulting, Inc., All Rights
 *            Reserved
 */
public class SerializedObjectConverter implements FieldConverter<Serializable> {

    /** The Serializable interface. */
    public static final Class<Serializable> SERIALIZABLE_IF = Serializable.class;

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to check if the field is serializable and is using the
     * serialization strategy.
     * </p>
     */
    @Override
    public boolean canConvert(MappedClass clazz, MappedField field) {
        boolean serializeStrategy = (field.getStrategy() == Strategy.SERIALIZED_UNCOMPRESSED)
                || (field.getStrategy() == Strategy.SERIALIZED_COMPRESSED);

        return serializeStrategy
                && SERIALIZABLE_IF.isAssignableFrom(field.getResolvedClass());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the value to a {@link BinaryElement} containing the
     * serialized value.
     * </p>
     */
    @Override
    public Element toElement(MappedClass clazz, MappedField field, String name,
            Serializable value) {
        if (value == null) {
            return new NullElement(name);
        }

        boolean useCompression = (field.getStrategy() == Strategy.SERIALIZED_COMPRESSED);
        try {
            return new BinaryElement(name, Serializer.serialize(value,
                    useCompression));
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the value from a {@link BinaryElement} containing
     * the serialized value.
     * </p>
     */
    @Override
    public Serializable fromElement(MappedClass clazz,
            com.google.code.morphia.state.MappedField field, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.BINARY) {
            boolean useCompression = (field.getStrategy() == Strategy.SERIALIZED_COMPRESSED);
            try {
                return (Serializable) Serializer.deserialize(
                        ((BinaryElement) element).getValue(), useCompression);
            }
            catch (IOException e) {
                throw new MappingException("While deserializing to "
                        + field.getField().getName(), e);
            }
            catch (ClassNotFoundException e) {
                throw new MappingException("While deserializing to "
                        + field.getField().getName(), e);
            }
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName()
                + " into a Serialized Object of type "
                + field.getConcreteClass().getName());
    }

}
