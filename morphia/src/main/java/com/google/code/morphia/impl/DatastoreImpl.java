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

package com.google.code.morphia.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.bson.DocumentReference;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.state.MappedClass;
import com.google.code.morphia.state.MappedClassCache;
import com.google.code.morphia.utils.IndexDirection;

/**
 * DatastoreImpl provides an implementation of the {@link Datastore} interface.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class DatastoreImpl implements Datastore {

    /** An empty base query. */
    public static final Map<String, Object> EMPTY_BASE_QUERY = Collections
            .emptyMap();

    /** The cache of the mappings for each class. */
    private final MappedClassCache classCache;

    /** The database containing the collections. */
    private final MongoDatabase database;

    /** The connection to MongoDB being used. */
    private final MongoClient mongo;

    /**
     * Creates a new DatastoreImpl.
     * 
     * @param mongo
     *            The connection to MongoDB being used.
     * @param databaseName
     *            The database containing the collections.
     * @param classCache
     *            The cache of the mappings for each class.
     */
    public DatastoreImpl(final MongoClient mongo, final String databaseName,
            final MappedClassCache classCache) {
        this.mongo = mongo;
        this.classCache = classCache;

        this.database = this.mongo.getDatabase(databaseName);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Query<T> createQuery(final Class<T> kind) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T, V> DocumentReference createRef(Class<T> clazz, V id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> DocumentReference createRef(T entity) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> UpdateOperations<T> createUpdateOperations(final Class<T> kind) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T, V> long delete(final Class<T> clazz, final Iterable<V> ids) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T, V> long delete(final Class<T> clazz, final V id) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long delete(final Query<T> query) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long delete(final Query<T> query, final Durability durability) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long delete(final T entity) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long delete(final T entity, final Durability durability) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public void ensureCaps() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> void ensureIndex(final Class<T> clazz, final String fields) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> void ensureIndex(final Class<T> clazz, final String field,
            final IndexDirection dir) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> void ensureIndex(final Class<T> clazz, final String name,
            final String fields, final boolean unique,
            final boolean dropDupsOnCreate) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public void ensureIndexes() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public void ensureIndexes(final boolean background) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> void ensureIndexes(final Class<T> clazz) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> void ensureIndexes(final Class<T> clazz, final boolean background) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public Key<?> exists(final Object keyOrEntity) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Query<T> find(final Class<T> clazz) {
        final MongoCollection collection = getCollection(clazz);

        return new QueryImpl<T>(clazz, collection, this, EMPTY_BASE_QUERY);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to call {@code find(clazz).filter(property, value);}.
     * </p>
     */
    @Override
    public <T, V> Query<T> find(final Class<T> clazz, final String property,
            final V value) {
        return find(clazz).filter(property, value);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to call
     * {@code find(clazz).filter(property, value).offset(offset).limit(size);}.
     * </p>
     */
    @Override
    public <T, V> Query<T> find(final Class<T> clazz, final String property,
            final V value, final int offset, final int size) {
        return find(clazz).filter(property, value).offset(offset).limit(size);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> T findAndDelete(final Query<T> query) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> T findAndModify(final Query<T> query,
            final UpdateOperations<T> ops) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> T findAndModify(final Query<T> query,
            final UpdateOperations<T> ops, final boolean oldVersion) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> T findAndModify(final Query<T> query,
            final UpdateOperations<T> ops, final boolean oldVersion,
            final boolean createIfMissing) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(Class<T> clazz, DocumentReference ref) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T, V> Query<T> get(final Class<T> clazz, final Iterable<V> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T, V> T get(final Class<T> clazz, final V id) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> T get(final T entity) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> T getByKey(final Class<T> clazz, final Key<T> key) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> List<T> getByKeys(final Class<T> clazz,
            final Iterable<Key<T>> keys) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> List<T> getByKeys(final Iterable<Key<T>> keys) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to return the Collection for the mapped class.
     * </p>
     */
    @Override
    public MongoCollection getCollection(final Class<?> clazz) {
        final MappedClass mapping = classCache.getMappingFor(clazz);
        return database.getCollection(mapping.getCollectionName());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long getCount(final Class<T> clazz) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long getCount(final Query<T> query) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long getCount(final T entity) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to return the encapsulated {@link MongoDatabase} instance.
     * </p>
     */
    @Override
    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to return the durability for the mapped database.
     * </p>
     */
    @Override
    public Durability getDefaultDurability() {
        return database.getDurability();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Key<T> getKey(final T entity) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to return the encapsulated {@link MongoClient} instance.
     * </p>
     */
    @Override
    public MongoClient getMongoClient() {
        return mongo;
    }

    @Override
    public <T> Iterable<Key<T>> insert(Iterable<T> entities,
            Durability durability) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> Key<T> insert(T entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> Iterable<Key<T>> insert(T... entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> Key<T> insert(T entity, Durability durability) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Key<T> merge(final T entity) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Key<T> merge(final T entity, final Durability durability) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Query<T> queryByExample(final T example) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Iterable<Key<T>> save(final Iterable<T> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Iterable<Key<T>> save(final Iterable<T> entities,
            final Durability durability) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Iterable<Key<T>> save(final T... entities) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Key<T> save(final T entity) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> Key<T> save(final T entity, final Durability durability) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to set the durability for the mapped database.
     * </p>
     */
    @Override
    public void setDefaultDurability(final Durability durability) {
        database.setDurability(durability);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long update(final Key<T> key, final UpdateOperations<T> ops) {
        // TODO Auto-generated method stub
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long update(final Query<T> query, final UpdateOperations<T> ops) {
        // TODO Auto-generated method stub
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long update(final Query<T> query, final UpdateOperations<T> ops,
            final boolean createIfMissing) {
        // TODO Auto-generated method stub
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long update(final Query<T> query, final UpdateOperations<T> ops,
            final boolean createIfMissing, final Durability durability) {
        // TODO Auto-generated method stub
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long update(final T ent, final UpdateOperations<T> ops) {
        // TODO Auto-generated method stub
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long updateFirst(final Query<T> query, final T entity,
            final boolean createIfMissing) {
        // TODO Auto-generated method stub
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long updateFirst(final Query<T> query,
            final UpdateOperations<T> ops) {
        // TODO Auto-generated method stub
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long updateFirst(final Query<T> query,
            final UpdateOperations<T> ops, final boolean createIfMissing) {
        // TODO Auto-generated method stub
        return -1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overriden to TODO Finish.
     * </p>
     */
    @Override
    public <T> long updateFirst(final Query<T> query,
            final UpdateOperations<T> ops, final boolean createIfMissing,
            final Durability durability) {
        // TODO Auto-generated method stub
        return -1;
    }

}
