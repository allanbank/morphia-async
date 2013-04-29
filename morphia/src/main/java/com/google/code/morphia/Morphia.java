/*
 *         Copyright 2010-2013  Olafur Gauti Gudmundsson, 
 *           Scott Hernandez and Allanbank Consulting, Inc.
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

import java.io.IOException;
import java.util.Set;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.bson.Document;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.converters.Converter;
import com.google.code.morphia.impl.DatastoreImpl;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.mapping.cache.EntityCache;
import com.google.code.morphia.state.MappedClassCache;
import com.google.code.morphia.utils.ReflectionUtils;

/**
 * Morphia provides the central class for interacting with the Morphia
 * Object/Document Mapper. This class contains the global mapping information
 * for the mapped classes. Most users of the class will start using Morphia by
 * creating a {@link Datastore} via one of the
 * {@link #createDatastore(MongoClient, String) createDatastore(...)} methods.
 * <p>
 * This class contains
 * 
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 * @copyright 2010-2013, Olafur Gauti Gudmundsson, Scott Hernandez and Allanbank
 *            Consulting, Inc., All Rights Reserved
 **/
@SuppressWarnings({ "rawtypes" })
public class Morphia {
    /** The cache of class mappings. */
    MappedClassCache classCache = new MappedClassCache();

    /**
     * Creates a new Morphia.
     */
    public Morphia() {
        super();
    }

    /**
     * Creates a new Morphia.
     * 
     * @param classesToMap
     *            The classes to add to the mapping. This is strictly for
     *            convenience as we will discover and map classes as they are
     *            seen.
     */
    public Morphia(final Set<Class<?>> classesToMap) {
        for (final Class c : classesToMap) {
            map(c);
        }
    }

    /**
     * Creates a {@link Datastore} implementation.
     * 
     * @param mongo
     *            The {@link MongoClient} instance. It is best to use a
     *            MongoClient singleton.
     * @param databaseName
     *            The name of the database to mapp all of the collections into.
     * @return The {@link Datastore} instance.
     * 
     */
    public Datastore createDatastore(final MongoClient mongo,
            final String databaseName) {
        return new DatastoreImpl(mongo, databaseName, classCache);
    }

    /**
     * Convenience method for converting a {@link Document} back into an object.
     * 
     * @param entityClass
     *            The class of the object to restore.
     * @param document
     *            The {@link Document} to convert to an object.
     * @return The object.
     */
    public <T> T fromDocument(final Class<T> entityClass,
            final Document document) {
        final Converter converter = new Converter(classCache);

        return converter.fromDocument(entityClass, document);
    }

    /**
     * Convenience method for converting a {@link Document} back into an object
     * using an entity cache.
     * 
     * @param entityClass
     *            The class of the object to restore.
     * @param document
     *            The {@link Document} to convert to an object.
     * @param cache
     *            The cache of documents already converted.
     * @return The object.
     * 
     * @todo Actually use the entity cache.
     */
    public <T> T fromDocument(final Class<T> entityClass,
            final Document document, final EntityCache cache) {
        // FIXME - Use the entity cache.
        final Converter converter = new Converter(classCache);

        return converter.fromDocument(entityClass, document);
    }

    /**
     * Adds classes to the mapping.
     * 
     * @param entityClasses
     *            The classes to add to the mapping. This is strictly for
     *            convenience as we will discover and map classes as they are
     *            seen.
     * @return This {@link Morphia} instance for call chaining.
     */
    public synchronized Morphia map(final Class<?>... entityClasses) {
        if ((entityClasses != null) && (entityClasses.length > 0)) {
            for (final Class<?> entityClass : entityClasses) {
                classCache.getMappingFor(entityClass);
            }
        }
        return this;
    }

    /**
     * Tries to map all classes in the package specified. Fails if one of the
     * classes is not valid for mapping.
     * 
     * @param packageName
     *            The name of the package to process
     * @return the Morphia instance
     */
    public synchronized Morphia mapPackage(final String packageName) {
        return mapPackage(packageName, false);
    }

    /**
     * Tries to map all classes in the package specified.
     * 
     * @param packageName
     *            the name of the package to process
     * @param ignoreInvalidClasses
     *            specifies whether to ignore classes in the package that cannot
     *            be mapped
     * @return the Morphia instance
     */
    public synchronized Morphia mapPackage(final String packageName,
            final boolean ignoreInvalidClasses) {
        try {
            for (final Class<?> c : ReflectionUtils.getClasses(packageName)) {
                try {
                    final Embedded embeddedAnn = c
                            .getAnnotation(Embedded.class);
                    final Entity enityAnn = c.getAnnotation(Entity.class);
                    if ((enityAnn != null) || (embeddedAnn != null)) {
                        map(c);
                    }
                }
                catch (final MappingException ex) {
                    if (!ignoreInvalidClasses) {
                        throw ex;
                    }
                }
            }
            return this;
        }
        catch (final IOException ioex) {
            throw new MappingException(
                    "Could not get map classes from package " + packageName,
                    ioex);
        }
        catch (final ClassNotFoundException cnfex) {
            throw new MappingException(
                    "Could not get map classes from package " + packageName,
                    cnfex);
        }
    }

    /**
     * Maps all of the classes in the same package as the specified class.
     * 
     * @param clazz
     *            The class to mapp in addition to the classes in the same
     *            package.
     * @return This {@link Morphia} instance for call chaining.
     */
    public synchronized Morphia mapPackageFromClass(final Class<?> clazz) {
        return mapPackage(clazz.getPackage().getName(), false);
    }

    /**
     * Converts the object to a {@link Document}.
     * 
     * @param object
     *            The object to convert to a {@link Document}.
     * @return The Document form of the object.
     */
    public Document toDocument(final Object object) {
        final Converter converter = new Converter(classCache);

        return converter.toDocument(object);
    }

}
