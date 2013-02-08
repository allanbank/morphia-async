/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.morphia;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoFactory;
import com.allanbank.mongodb.bson.Document;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.converters.Converter;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.mapping.cache.EntityCache;
import com.google.code.morphia.state.MappedClassCache;
import com.google.code.morphia.utils.ReflectionUtils;

/**
 * 
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 **/
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Morphia {
    /** The cache of class mappings. */
    MappedClassCache classCache = new MappedClassCache();

    public Morphia() {
        super();
    }

    public Morphia(Set<Class<?>> classesToMap) {
        for (Class c : classesToMap) {
            map(c);
        }
    }

    public synchronized Morphia map(Class<?>... entityClasses) {
        if (entityClasses != null && entityClasses.length > 0)
            for (Class<?> entityClass : entityClasses) {
                classCache.getMappingFor(entityClass);
            }
        return this;
    }

    public synchronized Morphia mapPackageFromClass(Class<?> clazz) {
        return mapPackage(clazz.getPackage().getName(), false);
    }

    /**
     * Tries to map all classes in the package specified. Fails if one of the
     * classes is not valid for mapping.
     * 
     * @param packageName
     *            the name of the package to process
     * @return the Morphia instance
     */
    public synchronized Morphia mapPackage(String packageName) {
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
    public synchronized Morphia mapPackage(String packageName,
            boolean ignoreInvalidClasses) {
        try {
            for (Class<?> c : ReflectionUtils.getClasses(packageName)) {
                try {
                    Embedded embeddedAnn = c.getAnnotation(Embedded.class);
                    Entity enityAnn = c.getAnnotation(Entity.class);
                    if (enityAnn != null || embeddedAnn != null) {
                        map(c);
                    }
                }
                catch (MappingException ex) {
                    if (!ignoreInvalidClasses) {
                        throw ex;
                    }
                }
            }
            return this;
        }
        catch (IOException ioex) {
            throw new MappingException(
                    "Could not get map classes from package " + packageName,
                    ioex);
        }
        catch (ClassNotFoundException cnfex) {
            throw new MappingException(
                    "Could not get map classes from package " + packageName,
                    cnfex);
        }
    }

    public <T> T fromDocument(Class<T> entityClass, Document dbObject) {
        Converter converter = new Converter(classCache);

        return converter.fromDocument(entityClass, dbObject);
    }

    public <T> T fromDocument(Class<T> entityClass, Document dbObject,
            EntityCache cache) {
        try {
            return (T) mapper.fromDocument(entityClass, dbObject, cache);
        }
        catch (Exception e) {
            throw new MappingException("Could not map entity from DBObject", e);
        }
    }

    public Document toDocument(Object entity) {
        Converter converter = new Converter(classCache);

        return converter.toDocument(entity);
    }

    public Mapper getMapper() {
        return this.mapper;
    }

    /** It is best to use a MongoClient singleton instance here **/
    public Datastore createDatastore(MongoClient mon, String dbName,
            String user, char[] pw) {
        return new DatastoreImpl(this, mon, dbName, user, pw);
    }

    /** It is best to use a MongoClient singleton instance here **/
    public Datastore createDatastore(MongoClient mongo, String dbName) {
        return createDatastore(mongo, dbName, null, null);
    }

}
