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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.NumericElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.allanbank.mongodb.bson.element.TimestampElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * FieldConverter to and from a {@link Timestamp}.
 * 
 * @author Scott Hernandez
 * @copyright 2010-2013, Scott Hernandez and Allanbank Consulting, Inc., All
 *            Rights Reserved
 */
public class TimestampConverter extends AbstractBasicFieldConverter<Timestamp> {

    /**
     * Creates a new TimestampConverter.
     */
    public TimestampConverter() {
        super(Timestamp.class);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link TimestampElement}.
     * </p>
     */
    @Override
    public Element toElement(Class<?> mappingType, String name, Timestamp object) {
        if (object == null) {
            return new NullElement(name);
        }
        return new TimestampElement(name, object.getTime());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the element's value as a {@link Timestamp}.
     * </p>
     */
    @Override
    public Timestamp fromElement(Class<?> mappingType, Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if (element.getType() == ElementType.UTC_TIMESTAMP) {
            long ts = ((TimestampElement) element).getTime();
            return new Timestamp(ts);
        }
        else if (element instanceof NumericElement) {
            long ts = ((NumericElement) element).getLongValue();
            return new Timestamp(ts);
        }
        else if ((element.getType() == ElementType.STRING)
                || (element.getType() == ElementType.SYMBOL)) {
            String sVal = element.getValueAsString();

            SimpleDateFormat sdf = new SimpleDateFormat(DateConverter.FORMAT);
            sdf.setTimeZone(DateConverter.UTC);

            try {
                return new Timestamp(sdf.parse(sVal).getTime());
            }
            catch (ParseException e) {
                throw new MappingException("Invalid date string name '" + sVal
                        + "'.", e);
            }
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a Date.");
    }
}
