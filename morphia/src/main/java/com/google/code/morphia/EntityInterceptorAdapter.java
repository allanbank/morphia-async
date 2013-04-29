/*
 *         Copyright 2010-2013 Uwe Schaefer and 
 *              Allanbank Consulting, Inc.
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
package com.google.code.morphia;

import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.google.code.morphia.converters.Converter;

/**
 * Provides an adapter for the {@link EntityInterceptor} interface.
 * 
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @copyright 2010-2013, Uwe Schaefer and Allanbank Consulting, Inc. All Rights
 *            Reserved
 */
public abstract class EntityInterceptorAdapter implements EntityInterceptor {

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to do nothing.
     * </p>
     */
    @Override
    public void postLoad(final Object ent, final DocumentBuilder dbObj,
            final Converter converter) {
        // Nothing.
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to do nothing.
     * </p>
     */
    @Override
    public void postPersist(final Object ent, final DocumentBuilder dbObj,
            final Converter converter) {
        // Nothing.
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to do nothing.
     * </p>
     */
    @Override
    public void preLoad(final Object ent, final DocumentBuilder dbObj,
            final Converter converter) {
        // Nothing.
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to do nothing.
     * </p>
     */
    @Override
    public void prePersist(final Object ent, final DocumentBuilder dbObj,
            final Converter converter) {
        // Nothing.
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to do nothing.
     * </p>
     */
    @Override
    public void preSave(final Object ent, final DocumentBuilder dbObj,
            final Converter converter) {
        // Nothing.
    }
}
