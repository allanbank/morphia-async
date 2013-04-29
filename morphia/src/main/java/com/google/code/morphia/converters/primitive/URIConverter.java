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

import java.net.URI;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.element.NullElement;
import com.allanbank.mongodb.bson.element.StringElement;
import com.google.code.morphia.mapping.MappingException;

/**
 * FieldConverter to and from a URI.
 * 
 * @author Scott Hernandez
 * @copyright 2010-2013, Scott Hernandez and Allanbank Consulting, Inc., All
 *            Rights Reserved
 */
@SuppressWarnings({ "rawtypes" })
public class URIConverter extends AbstractBasicFieldConverter<URI> {

    /**
     * Creates a new URIConverter.
     */
    public URIConverter() {
        this(URI.class);
    }

    /**
     * Creates a new URIConverter.
     * 
     * @param clazz
     *            The sub type of URI being converted.
     */
    protected URIConverter(final Class clazz) {
        super(clazz);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return the element's value as a {@link Short}.
     * </p>
     */
    @Override
    public URI fromElement(final Class<?> mappingType, final Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }
        else if ((element.getType() == ElementType.STRING)
                || (element.getType() == ElementType.SYMBOL)) {
            final String sVal = element.getValueAsString();

            return URI.create(sVal.replace("%46", "."));
        }

        throw new MappingException("Could not figure out how to map a "
                + element.getClass().getSimpleName() + " into a UUID.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert the {@code object} into a {@link StringElement}.
     * </p>
     */
    @Override
    public Element toElement(final Class<?> mappingType, final String name,
            final URI object) {
        if (object == null) {
            return new NullElement(name);
        }
        return new StringElement(name, object.toString().replace(".", "%46"));
    }
}
