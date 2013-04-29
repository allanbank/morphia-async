/*
 *         Copyright 2010-2013 Olafur Gauti Gudmundsson, 
 *         Scott Hernandez and Allanbank Consulting, Inc.
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

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoCollection;
import com.google.code.morphia.DAO;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;

/**
 * Implementation of the {@link DAO} interface.
 * 
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 * @copyright 2010-2013, Olafur Gauti Gudmundsson, Scott Hernandez and Allanbank
 *            Consulting, Inc., All Rights Reserved
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DAOImpl<T, K> implements DAO<T, K> {

    protected Datastore ds;
    protected Class<T> entityClazz;

    public DAOImpl(final Class<T> entityClass, final Datastore ds) {
        this.ds = ds;
        initType(entityClass);
    }

    public DAOImpl(final Class<T> entityClass, final MongoClient mongo,
            final Morphia morphia, final String dbName) {
        initDS(mongo, morphia, dbName);
        initType(entityClass);
    }

    protected DAOImpl(final Datastore ds) {
        this.ds = ds;
        initType(((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]));
    }

    /**
     * <p>
     * Only calls this from your derived class when you explicitly declare the
     * generic types with concrete classes
     * </p>
     * <p>
     * {@code class MyDao extends DAO<MyEntity, String>}
     * </p>
     * */
    protected DAOImpl(final MongoClient mongo, final Morphia morphia,
            final String dbName) {
        initDS(mongo, morphia, dbName);
        initType(((Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#count()
     */
    @Override
    public long count() {
        return ds.getCount(entityClazz);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.morphia.DAO#count(com.google.code.morphia.query.Query)
     */
    @Override
    public long count(final Query<T> q) {
        return ds.getCount(q);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#count(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public long count(final String key, final Object value) {
        return count(ds.find(entityClazz, key, value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#createQuery()
     */
    @Override
    public Query<T> createQuery() {
        return ds.createQuery(entityClazz);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#createUpdateOperations()
     */
    @Override
    public UpdateOperations<T> createUpdateOperations() {
        return ds.createUpdateOperations(entityClazz);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#delete(T)
     */
    @Override
    public long delete(final T entity) {
        return ds.delete(entity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#delete(T, com.mongodb.WriteConcern)
     */
    @Override
    public long delete(final T entity, final Durability wc) {
        return ds.delete(entity, wc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#deleteById(K)
     */
    @Override
    public long deleteById(final K id) {
        return ds.delete(entityClazz, id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.morphia.DAO#deleteByQuery(com.google.code.morphia.query
     * .Query)
     */
    @Override
    public long deleteByQuery(final Query<T> q) {
        return ds.delete(q);
    }

    @Override
    public void ensureIndexes() {
        ds.ensureIndexes(entityClazz);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.morphia.DAO#exists(com.google.code.morphia.query.Query)
     */
    @Override
    public boolean exists(final Query<T> q) {
        return ds.getCount(q) > 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#exists(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public boolean exists(final String key, final Object value) {
        return exists(ds.find(entityClazz, key, value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#find()
     */
    @Override
    public QueryResults<T> find() {
        return createQuery();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.morphia.DAO#find(com.google.code.morphia.query.Query)
     */
    @Override
    public QueryResults<T> find(final Query<T> q) {
        return q;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#findIds()
     */
    @Override
    public List<K> findIds() {
        return (List<K>) keysToIds(ds.find(entityClazz).asKeyList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.morphia.DAO#findIds(com.google.code.morphia.query.Query)
     */
    @Override
    public List<K> findIds(final Query<T> q) {
        return (List<K>) keysToIds(q.asKeyList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#findIds(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public List<K> findIds(final String key, final Object value) {
        return (List<K>) keysToIds(ds.find(entityClazz, key, value).asKeyList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.morphia.DAO#findOne(com.google.code.morphia.query.Query)
     */
    @Override
    public T findOne(final Query<T> q) {
        return q.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#findOne(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public T findOne(final String key, final Object value) {
        return ds.find(entityClazz, key, value).get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#get(K)
     */
    @Override
    public T get(final K id) {
        return ds.get(entityClazz, id);
    }

    /** The underlying collection for this DAO */
    @Override
    public MongoCollection getCollection() {
        return ds.getCollection(entityClazz);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#getDatastore()
     */
    @Override
    public Datastore getDatastore() {
        return ds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#getEntityClass()
     */
    @Override
    public Class<T> getEntityClass() {
        return entityClazz;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#save(T)
     */
    @Override
    public Key<T> save(final T entity) {
        return ds.save(entity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.code.morphia.DAO#save(T, com.mongodb.WriteConcern)
     */
    @Override
    public Key<T> save(final T entity, final Durability wc) {
        return ds.save(entity, wc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.morphia.DAO#update(com.google.code.morphia.query.Query,
     * com.google.code.morphia.query.UpdateOperations)
     */
    @Override
    public UpdateResults<T> update(final Query<T> q,
            final UpdateOperations<T> ops) {
        return ds.update(q, ops);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.code.morphia.DAO#updateFirst(com.google.code.morphia.query
     * .Query, com.google.code.morphia.query.UpdateOperations)
     */
    @Override
    public UpdateResults<T> updateFirst(final Query<T> q,
            final UpdateOperations<T> ops) {
        return ds.updateFirst(q, ops);
    }

    protected void initDS(final MongoClient mongo, final Morphia mor,
            final String dbName) {
        ds = mor.createDatastore(mongo, dbName);
    }

    protected void initType(final Class<T> type) {
        this.entityClazz = type;
    }

    /**
     * Converts from a List<Key> to their id values
     * 
     * @param keys
     * @return
     */
    protected List<?> keysToIds(final List<Key<T>> keys) {
        final ArrayList ids = new ArrayList(keys.size() * 2);
        for (final Key<T> key : keys) {
            ids.add(key.getId());
        }
        return ids;
    }

}
