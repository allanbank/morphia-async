/*
 * Copyright 2013, Allanbank Consulting, Inc. 
 *           All Rights Reserved
 */

package com.google.code.morphia.converters;

import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.element.NullElement;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedField;

/**
 * FieldConverterAdapter provides a stub implementation of the
 * {@link FieldConverter} interface.
 * 
 * @param <T>
 *            The type mapped by this converter.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class FieldConverterAdapter<T> implements FieldConverter<T> {

    /**
     * Creates a new FieldConverterAdapter.
     */
    public FieldConverterAdapter() {
        super();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to always return true. This is probably OK for custom
     * converters attached to specific fields but should not be used for general
     * converters registered with the runtime.
     * </p>
     */
    @Override
    public boolean canConvert(final MappedClass clazz, final MappedField field) {
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to return null.
     * </p>
     */
    @Override
    public T fromElement(final MappedClass clazz, final MappedField field,
            final Element element) {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to return a {@link NullElement}.
     * </p>
     */
    @Override
    public Element toElement(final MappedClass clazz, final MappedField field,
            final String name, final T value) {
        return new NullElement(name);
    }

}
