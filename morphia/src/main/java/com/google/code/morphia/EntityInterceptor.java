/*
 *         Copyright 2010-2013  Allanbank Consulting, Inc.,
 *                         and others.
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
 * Interface for intercepting {@link com.google.code.morphia.annotations.Entity}
 * lifecycle events.
 * 
 * @copyright 2010-2013, Allanbank Consulting, Inc., and others. All Rights
 *            Reserved
 */
public interface EntityInterceptor {
    /**
     * Post-load interceptor method.
     * 
     * @param entity
     *            The entity being loaded.
     * @param documentBuilder
     *            The document for the entity.
     * @param converter
     *            The converter for the conversion.
     * 
     * @see com.google.code.morphia.annotations.PostLoad
     */
    public void postLoad(Object entity, DocumentBuilder documentBuilder,
            Converter converter);

    /**
     * Post-persist interceptor method.
     * 
     * @param entity
     *            The entity being persisted.
     * @param documentBuilder
     *            The document builder for the entity.
     * @param converter
     *            The converter for the conversion.
     * 
     * @see com.google.code.morphia.annotations.PostPersist
     */
    public void postPersist(Object entity, DocumentBuilder documentBuilder,
            Converter converter);

    /**
     * Pre-load interceptor method.
     * 
     * @param entity
     *            The entity being loaded.
     * @param documentBuilder
     *            The document for the entity.
     * @param converter
     *            The converter for the conversion.
     * 
     * @see com.google.code.morphia.annotations.PreLoad
     */
    public void preLoad(Object entity, DocumentBuilder documentBuilder,
            Converter converter);

    /**
     * Pre-persist interceptor method.
     * 
     * @param entity
     *            The entity being persisted.
     * @param documentBuilder
     *            The document builder for the entity.
     * @param converter
     *            The converter for the conversion.
     * 
     * @see com.google.code.morphia.annotations.PrePersist
     */
    public void prePersist(Object entity, DocumentBuilder documentBuilder,
            Converter converter);

    /**
     * Pre-save interceptor method.
     * 
     * @param entity
     *            The entity being saved.
     * @param documentBuilder
     *            The document builder for the entity.
     * @param converter
     *            The converter for the conversion.
     * 
     * @see com.google.code.morphia.annotations.PreSave
     */
    public void preSave(Object entity, DocumentBuilder documentBuilder,
            Converter converter);
}
