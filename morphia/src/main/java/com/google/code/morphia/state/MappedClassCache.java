/*
 *         Copyright 2010-2013 Allanbank Consulting, Inc.
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
package com.google.code.morphia.state;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * MappedClassCache provides a cached version of the mapped classes. This class
 * also maintains the connections between the base classes and inherited classes
 * for mapping resolution.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class MappedClassCache {

    /** The cache of classes by the class's canonical name. */
    private final ConcurrentMap<String, MappedClass> cache;

    /**
     * Creates a new MappedClassCache.
     */
    public MappedClassCache() {
        cache = new ConcurrentHashMap<String, MappedClass>();
    }

    /**
     * Returns the mapping for the class.
     * 
     * @param clazz
     *            The class to return a mapping for.
     * @return The mapping information for the class.
     */
    public MappedClass getMappingFor(final Class<?> clazz) {
        final String name = clazz.getCanonicalName();
        MappedClass mapping = cache.get(name);
        if (mapping == null) {
            final Scanner scanner = new Scanner();

            mapping = scanner.scan(clazz);
            Class<?> c = clazz.getSuperclass();
            while (c != Scanner.ROOT) {
                getMappingFor(c).addDerivedClass(clazz, mapping, this);
                c = c.getSuperclass();
            }

            final MappedClass existing = cache.putIfAbsent(name, mapping);
            if (existing != null) {
                mapping = existing;
            }
        }

        return mapping;
    }
}
