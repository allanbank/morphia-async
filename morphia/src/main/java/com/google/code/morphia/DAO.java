/*
 *         Copyright 2010-2013 Allanbank Consulting, Inc. 
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

import java.util.List;

import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.MongoCollection;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.google.code.morphia.query.UpdateOperations;

/**
 * DAO provides an interface for interacting with a single entity type.
 * 
 * @param <T>
 *            The type for the entity.
 * @param <K>
 *            The type for the key of the entity.
 * @copyright 2013, Allanbank Consulting, Inc., and others, All Rights Reserved
 */
public interface DAO<T, K> {
    /**
     * Returns the number of entities in the collection for the DAO's entity
     * type.
     * 
     * @return The number of entities in the collection for the DAO's entity
     *         type.
     */
    public long count();

    /**
     * Returns the number of entities in the collection for the DAO's entity
     * type that match the query.
     * 
     * @param query
     *            The filter for the documents to count.
     * @return The number of entities in the collection for the DAO's entity
     *         type that match the query.
     */
    public long count(Query<T> query);

    /**
     * Returns the number of entities in the collection that match the criteria
     * {key:value}.
     * 
     * @param key
     *            The key for the value to match.
     * @param value
     *            The value to match.
     * @return The number of entities in the collection that match the criteria
     *         {key:value}.
     */
    public long count(String key, Object value);

    /**
     * Starts a query for this DAO's entity type.
     * 
     * @return The builder for a query of this DAO's entity type.
     */
    public Query<T> createQuery();

    /**
     * Starts a update-operations definitial for this DAO's entity type
     * 
     * @return The builder for the update operations.
     */
    public UpdateOperations<T> createUpdateOperations();

    /**
     * Deletes the entity.
     * 
     * @param entity
     *            The entity to delete.
     * @return The number of Documents deleted.
     */
    public long delete(T entity);

    /**
     * Deletes the entity.
     * 
     * @param entity
     *            The entity to delete.
     * @param durability
     *            The durability required for the delete.
     * @return The number of Documents deleted.
     */
    public long delete(T entity, Durability durability);

    /**
     * Delete the entity by id.
     * 
     * @param id
     *            The id for the entity to delete.
     * @return The number of documents deleted.
     */
    public long deleteById(K id);

    /**
     * Delete the entities matching a criteria/query.
     * 
     * @param query
     *            The query for the entities to delete.
     * @return The number of documents deleted.
     */
    public long deleteByQuery(Query<T> query);

    /**
     * Ensures indexed for this DAO's entity type.
     */
    public void ensureIndexes();

    /**
     * Checks that atleast 1 entity matches the query.
     * 
     * @param query
     *            The query for the entities to find.
     * @return True if atleast 1 document matches the query.
     */
    public boolean exists(Query<T> query);

    /**
     * Checks that atleast 1 entity matches the query.
     * 
     * @param key
     *            The key for the value to match.
     * @param value
     *            The value to match.
     * @return True if atleast 1 document matches the query.
     */
    public boolean exists(String key, Object value);

    /**
     * Returns all of the the entities.
     * 
     * @return The results containing all of the entities.
     */
    public QueryResults<T> find();

    /**
     * Returns all of the the entities matching a query.
     * 
     * @param query
     *            The query for the entities to find.
     * @return The results containing all of the entities matching the query.
     */
    public QueryResults<T> find(Query<T> query);

    /**
     * Returns all of the the entity ids.
     * 
     * @return The list containing all of the entity ids.
     */
    public List<K> findIds();

    /**
     * Returns all of the the entity ids matching a query.
     * 
     * @param query
     *            The query for the entities to find.
     * @return The list containing all of the entity ids matching a query..
     */
    public List<K> findIds(Query<T> query);

    /**
     * Returns all of the the entity ids matching a query on a key/value.
     * 
     * @param key
     *            The key for the value to match.
     * @param value
     *            The value to match.
     * @return The list containing all of the entity ids matching the query.
     */
    public List<K> findIds(String key, Object value);

    /**
     * Returns the first entity which matches the criteria.
     * 
     * @param query
     *            The query for the entity to find.
     * @return The first matching entity.
     */
    public T findOne(Query<T> query);

    /**
     * Returns the first entity which matches the criteria.
     * 
     * @param key
     *            The key for the value to match.
     * @param value
     *            The value to match.
     * @return The first matching entity.
     */
    public T findOne(String key, Object value);

    /**
     * Loads the entity by id value.
     * 
     * @param id
     *            The id of the entity.
     * @return The entity.
     */
    public T get(K id);

    /**
     * Returns the collection containing the entities.
     * 
     * @return The collection containing the entities.
     */
    public MongoCollection getCollection();

    /**
     * Returns the underlying datastore.
     * 
     * @return The underlying datastore.
     */
    public Datastore getDatastore();

    /**
     * Returns the type of entities for this DAO.
     * 
     * @return The type of entities for this DAO.
     */
    public Class<T> getEntityClass();

    /**
     * Saves the entity; either inserting or overriding the existing document.
     * 
     * @param entity
     *            The entity to save or insert.
     * @return The key for the saved entity.
     */
    public Key<T> save(T entity);

    /**
     * Saves the entity; either inserting or overriding the existing document.
     * 
     * @param entity
     *            The entity to save or insert.
     * @param durability
     *            The durability required for the save.
     * @return The key for the saved entity.
     */
    public Key<T> save(T entity, Durability durability);

    /**
     * Updates all entities matched by the constraints with the modifiers
     * supplied.
     * 
     * @param query
     *            The query to select documents to update.
     * @param ops
     *            The update operations to apply to the selected documents.
     * @return The number of updated documents.
     */
    public long update(Query<T> query, UpdateOperations<T> ops);

    /**
     * Updates the first entity matched by the constraints with the modifiers
     * supplied.
     * 
     * @param query
     *            The query to select documents to update.
     * @param ops
     *            The update operations to apply to the selected documents.
     * @return The number of updated documents.
     */
    public long updateFirst(Query<T> query, UpdateOperations<T> ops);
}