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
package com.google.code.morphia.converters.primitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Provides the basic functionality for a {@link BasicFieldConverter}. Mainly
 * type checking.
 * 
 * @param <T>
 *            The type of object supported by this converter.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public abstract class AbstractBasicFieldConverter<T> implements
        BasicFieldConverter<T> {

    /** The complete set of classes this converter supports. */
    private final List<Class<?>> supportedTypes;

    /**
     * Creates a new AbstractBasicFieldConverter.
     * 
     * @param supportedTypes
     *            The complete set of classes this converter supports.
     */
    public AbstractBasicFieldConverter(Class<?>... supportedTypes) {
        super();

        this.supportedTypes = Collections
                .unmodifiableList(new ArrayList<Class<?>>(Arrays
                        .asList(supportedTypes)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks the input type against the supported type list.
     * </p>
     */
    @Override
    public boolean canConvert(Class<?> mappingType) {
        for (Class<?> supportedType : supportedTypes) {
            if (mappingType == supportedType) {
                return true;
            }
        }
        return false;
    }
}
