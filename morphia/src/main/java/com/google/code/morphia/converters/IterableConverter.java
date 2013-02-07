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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.ElementType;
import com.allanbank.mongodb.bson.element.ArrayElement;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * Converts any {@link Iterable} into an {@link ArrayElement}.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author Scott Hernandez
 * @copyright 2010-2013, Uwe Schaefer, Scott Hernandez and Allanbank Consulting,
 *            Inc., All Rights Reserved
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class IterableConverter implements FieldConverter<Object> {

    /**
     * Tne class for the {@link ArrayList} which is used as the default non-set
     * implementation.
     */
    public static final Class<ArrayList> ARRAY_LIST_CLASS = ArrayList.class;

    /**
     * The class for the {@link HashSet} which is used as the default Set
     * implementation.
     */
    public static final Class<HashSet> HASH_SET_CLASS = HashSet.class;

    /** The class for the {@link Set} interface. */
    public static final Class<Set> SET_CLASS = Set.class;

    /** The class for the {@link Iterable} interface. */
    public static final Class<Iterable> ITERABLE_CLASS = Iterable.class;

    /** The base object class. */
    public static final Class<Object> OBJECT_CLASS = Object.class;

    /** The converters for sub-fields. */
    private final CachingFieldConverter fieldConverter;

    /**
     * Creates a new IterableConverter.
     * 
     * @param fieldConverter
     *            The converters for sub-fields.
     */
    public IterableConverter(CachingFieldConverter fieldConverter) {
        this.fieldConverter = fieldConverter;
    }

    /**
     * Create the appropriate type of collections to be used.
     * 
     * @param field
     *            The field information.
     * @return The created collection.
     */
    private Collection<?> createNewCollection(final MappedField field) {
        Class<?> type = field.getResolvedClass();

        if (SET_CLASS.isAssignableFrom(type)) {
            return newInstance(type, HASH_SET_CLASS);
        }
        return newInstance(type, ARRAY_LIST_CLASS);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to return true if the field's type implements the
     * {@link Iterable} interface.
     * </p>
     */
    @Override
    public boolean canConvert(MappedClass clazz,
            com.google.code.morphia.state.MappedField field) {
        Class<?> type = field.getResolvedClass();

        return ITERABLE_CLASS.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to create an {@link ArrayElement} with the iterable values.
     * </p>
     */
    @Override
    public Element toElement(MappedClass clazz,
            com.google.code.morphia.state.MappedField field, String name,
            Object value) {
        if (value == null) {
            return new NullElement(name);
        }

        Iterable<?> iterableValues = toIterable(value);
        List<Element> elements = new ArrayList<Element>();
        int i = 0;
        MappedField subField = new MappedField();
        for (Object o : iterableValues) {

            String itemName = String.valueOf(i++);
            subField.mapFor(itemName,
                    (o != null) ? o.getClass() : field.getTypeArgumentClass(0));

            elements.add(fieldConverter.toElement(clazz, subField, itemName, o));
        }

        return new ArrayElement(name, elements);
    }

    /**
     * Converts the value into an iterable.
     * 
     * @param value
     *            The value to convert.
     * @return The value as an {@link Iterable}.
     */
    protected Iterable<?> toIterable(Object value) {
        return (Iterable<?>) value;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overridden to convert an {@link ArrayElement} to an {@link Iterable}
     * again.
     * </p>
     */
    @Override
    public Object fromElement(MappedClass clazz, MappedField field,
            Element element) {
        if ((element == null) || (element.getType() == ElementType.NULL)) {
            return null;
        }

        Class subtype = field.getTypeArgumentClass(0);
        MappedField subField = new MappedField();
        subField.mapFor("0", subtype);

        Collection vals = createNewCollection(field);
        if (element.getType() == ElementType.ARRAY) {
            for (Element o : ((ArrayElement) element).getEntries()) {
                vals.add(fieldConverter.fromElement(clazz, subField, o));
            }
        }
        else {
            // Single value case
            vals.add(fieldConverter.fromElement(clazz, subField, element));
        }

        return vals;
    }

    /**
     * Creates an object of the specified type or if that fails the fallback
     * type.
     * 
     * @param type
     *            The type to create a new instance of.
     * @param fallbackType
     *            The fallback type to create.
     * @return The object created.
     */
    private Collection<?> newInstance(Class<?> type,
            final Class<? extends Collection> fallbackType) {
        try {
            return (Collection<?>) type.newInstance();
        }
        catch (Exception error) {
            try {
                return fallbackType.newInstance();
            }
            catch (InstantiationException e) {
                throw new MappingException(
                        "Could not create a object of type '"
                                + fallbackType.getSimpleName() + "'.", e);
            }
            catch (IllegalAccessException e) {
                throw new MappingException(
                        "Could not create a object of type '"
                                + fallbackType.getSimpleName() + "'.", e);
            }
        }
    }
}
