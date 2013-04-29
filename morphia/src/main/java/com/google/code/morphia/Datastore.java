/*
 *         Copyright 2010-2013  Scott Hernandez 
 *             and Allanbank Consulting, Inc.
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
import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.bson.DocumentReference;
import com.google.code.morphia.annotations.CappedAt;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Indexes;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.utils.IndexDirection;

/**
 * Datastore interface providing basic CRUD operations.
 * 
 * @author Scott Hernandez
 * @copyright 2010-2013, Scott Hernandez and Allanbank Consulting, Inc. All
 *            Rights Reserved
 */
public interface Datastore {
    /**
     * Returns a new query bound to the kind (a specific {@link MongoCollection}
     * ). This method is equivalent to {@link #find(Class)}.
     * 
     * @param clazz
     *            The {@link Class} of entityies to query.
     * @return A builder for the {@link Query}.
     */
    public <T> Query<T> createQuery(Class<T> clazz);

    /**
     * Creates a reference to the entity (using the current DB, the
     * collectionName, and id).
     * 
     * @param clazz
     *            The type of the referenced entity.
     * @param id
     *            The id value for the entity.
     * @return The {@link DocumentReference} for the entity.
     */
    public <T, V> DocumentReference createRef(Class<T> clazz, V id);

    /**
     * Creates a reference to the entity (using the current DB, the
     * collectionName, and id).
     * 
     * @param entity
     *            The entity to reference.
     * @return The {@link DocumentReference} for the entity.
     */
    public <T> DocumentReference createRef(T entity);

    /**
     * Creates a builder for all update operations.
     * 
     * @param clazz
     *            The {@link Class} of entityies to update.
     * @return The update builder.
     */
    public <T> UpdateOperations<T> createUpdateOperations(Class<T> clazz);

    /**
     * Deletes the given entities by the provided {@code ids}.
     * 
     * @param clazz
     *            The class to delete the entities from.
     * @param ids
     *            The ids of the entities to delete.
     * @return The number of deleted entities.
     */
    public <T, V> long delete(Class<T> clazz, Iterable<V> ids);

    /**
     * Deletes the given entity by the provided {@code id} value.
     * 
     * @param clazz
     *            The class to delete the entity from.
     * @param id
     *            The id of the entity to delete.
     * @return The number of deleted entities.
     */
    public <T, V> long delete(Class<T> clazz, V id);

    /**
     * Deletes the given entities based on the query.
     * 
     * @param query
     *            The query to usee when deleting the entities.
     * @return The number of documents deleted.
     */
    public <T> long delete(Query<T> query);

    /**
     * Deletes the given entities based on the query, with the Durability
     * 
     * @param query
     *            The query to usee when deleting the entities.
     * @param durability
     *            The durability required for the delete.
     * @return The number of documents removed.
     */
    public <T> long delete(Query<T> query, Durability durability);

    /**
     * Deletes the given entity by its id field.
     * 
     * @param entity
     *            The entity to delete.
     * @return The number of documents deleted.
     */
    public <T> long delete(T entity);

    /**
     * Deletes the given entity by its id field.
     * 
     * @param entity
     *            The entity to delete.
     * @param durability
     *            The durability required for the delete.
     * @return The number of documents deleted.
     */
    public <T> long delete(T entity, Durability durability);

    /**
     * Ensures that the collections for all {@link CappedAt} entities are
     * created. If the collection is not created it will be created using the
     * {@link CappedAt} values.
     * <p>
     * <em>Note:</em> This method Will block until all of the capped collections
     * have been created.
     * </p>
     */
    public void ensureCaps();

    /**
     * Ensures the index exists including the field(s) + directions.
     * 
     * @param clazz
     *            The {@link Class} of entities to create the index for.
     * @param fields
     *            The fields for the index. e.g., "field1, -field2"
     */
    public <T> void ensureIndex(Class<T> clazz, String fields);

    /**
     * Ensures the index exists including the field + directions.
     * 
     * @param clazz
     *            The {@link Class} of entities to create the index for.
     * @param field
     *            The fields for the index.
     * @param dir
     *            The direction for the index.
     */
    public <T> void ensureIndex(Class<T> clazz, String field, IndexDirection dir);

    /**
     * Ensures the index exists including the field(s) + directions.
     * 
     * @param clazz
     *            The {@link Class} of entities to create the index for.
     * @param name
     *            The name of the index to create.
     * @param fields
     *            The fields for the index. e.g., "field1, -field2"
     * @param unique
     *            If the index should be unique.
     * @param dropDupsOnCreate
     *            If creating a unique index if the duplicate fields should be
     *            dropped.
     */
    public <T> void ensureIndex(Class<T> clazz, String name, String fields,
            boolean unique, boolean dropDupsOnCreate);

    /**
     * Ensures all the indexes found during class mapping (using {@link Indexed}
     * and/or {@link Indexes}) have been created.
     */
    public void ensureIndexes();

    /**
     * Ensures all the indexes found during class mapping (using {@link Indexed}
     * and/or {@link Indexes}) have been created, possibly in the background
     * 
     * @param background
     *            If true then the indexes will be created in the background and
     *            not block the caller.
     */
    public void ensureIndexes(boolean background);

    /**
     * Ensures the indexes for a single class found during class mapping (using
     * {@link Indexed} and/or {@link Indexes}) have been created, possibly in
     * the background
     * 
     * @param clazz
     *            The class to ensure the indexes have been created for.
     */
    public <T> void ensureIndexes(Class<T> clazz);

    /**
     * Ensures the indexes for a single class found during class mapping (using
     * {@link Indexed} and/or {@link Indexes}) have been created, possibly in
     * the background
     * 
     * @param clazz
     *            The class to ensure the indexes have been created for.
     * @param background
     *            If true then the indexes will be created in the background and
     *            not block the caller.
     */
    public <T> void ensureIndexes(Class<T> clazz, boolean background);

    /**
     * Does a query to check if the keyOrEntity exists in MongoDB. If it does
     * returns the {@link Key} for the entity, otherwise <code>null</code> is
     * returned.
     * 
     * @param keyOrEntity
     *            The {@link Key} or entity to determine if it exists.
     * @return The {@link Key} for the entity if it exists, otherwise
     *         <code>null</code>.
     */
    public Key<?> exists(Object keyOrEntity);

    /**
     * Starts the construction of a Query over the {@link Class} of entities.
     * 
     * @param clazz
     *            The {@link Class} of entityies to query.
     * @return A builder for the {@link Query}.
     */
    public <T> Query<T> find(Class<T> clazz);

    /**
     * Starts the construction of a Query over the {@link Class} of entities.
     * <p>
     * This is the same as: {@code find(clazz).filter(property, value); }
     * </p>
     * 
     * @param clazz
     *            The {@link Class} of entityies to query.
     * @param property
     *            The initial property to filter on.
     * @param value
     *            The value for the property.
     * @return A builder for the {@link Query}.
     */
    public <T, V> Query<T> find(Class<T> clazz, String property, V value);

    /**
     * Starts the construction of a Query over the {@link Class} of entities.
     * <p>
     * This is the same as:
     * {@code find(clazz).filter(property, value).offset(offset).limit(size); }
     * </p>
     * 
     * @param clazz
     *            The {@link Class} of entityies to query.
     * @param property
     *            The initial property to filter on.
     * @param value
     *            The value for the property.
     * @param offset
     *            The offset or first document to return.
     * @param size
     *            The number of Documents to return. Also known as the limit.
     * @return A builder for the {@link Query}.
     */
    public <T, V> Query<T> find(Class<T> clazz, String property, V value,
            int offset, int size);

    /**
     * Deletes the given entities based on the query (first item only).
     * 
     * @param query
     *            the query to find the Entity with; You are not allowed to
     *            offset/skip in the query.
     * @return the deleted Entity
     */
    public <T> T findAndDelete(Query<T> query);

    /**
     * Find the first Entity from the Query, and modify it.
     * 
     * @param query
     *            the query to find the Entity with; You are not allowed to
     *            offset/skip in the query.
     * @param ops
     *            The updates to apply.
     * @return The modified Entity (the result of the update)
     */
    public <T> T findAndModify(Query<T> query, UpdateOperations<T> ops);

    /**
     * Find the first Entity from the Query, and modify it.
     * 
     * @param query
     *            the query to find the Entity with; You are not allowed to
     *            offset/skip in the query.
     * @param ops
     *            The updates to apply.
     * @param oldVersion
     *            indicated the old version of the Entity should be returned
     * @return The Entity (the result of the update if oldVersion is false)
     */
    public <T> T findAndModify(Query<T> query, UpdateOperations<T> ops,
            boolean oldVersion);

    /**
     * Find the first Entity from the Query, and modify it.
     * 
     * @param query
     *            the query to find the Entity with; You are not allowed to
     *            offset/skip in the query.
     * @param ops
     *            The updates to apply.
     * @param oldVersion
     *            indicated the old version of the Entity should be returned
     * @param createIfMissing
     *            if the query returns no results, then a new object will be
     *            created (sets upsert=true)
     * @return The Entity (the result of the update if oldVersion is false)
     */
    public <T> T findAndModify(Query<T> query, UpdateOperations<T> ops,
            boolean oldVersion, boolean createIfMissing);

    /**
     * Find the given entity (by collectionName/id).
     * 
     * @param clazz
     *            The type of the entity.
     * @param ref
     *            The reference to the entity.
     * @return The entity.
     */
    public <T> T get(Class<T> clazz, DocumentReference ref);

    /**
     * Find the given entities (by id); shorthand for
     * {@code find("_id in", ids)}
     * 
     * @param clazz
     *            The {@link Class} of entityies to query.
     * @param ids
     *            The ids of the documents to query for.
     * @return A builder for the {@link Query}.
     */
    public <T, V> Query<T> get(Class<T> clazz, Iterable<V> ids);

    /**
     * Find the given entity (by id); shorthand for {@code find("_id ", id)}.
     * 
     * @param clazz
     *            The {@link Class} of entityies to query.
     * @param id
     *            The id of the document to query for.
     * @return The document with the specified id.
     */
    public <T, V> T get(Class<T> clazz, V id);

    /**
     * Find the given entity (by collectionName/id); think of this as refresh.
     * 
     * @param entity
     *            The entity to search for and update.
     * @return The updated entity.
     */
    public <T> T get(T entity);

    /**
     * Find the given entity (by collectionName/id).
     * 
     * @param clazz
     *            The class of object to load.
     * @param key
     *            The key for the document to load.
     * @return The object with the specified id.
     */
    public <T> T getByKey(Class<T> clazz, Key<T> key);

    /**
     * Find the given entities (by id), verifying they are of the correct type;
     * shorthand for {@code find("_id in", ids)}.
     * 
     * @param clazz
     *            The class of objects to load.
     * @param keys
     *            The keys for the documents to load.
     * @return The objects with the specified ids.
     */
    public <T> List<T> getByKeys(Class<T> clazz, Iterable<Key<T>> keys);

    /**
     * Find the given entities (by id); shorthand for
     * {@code find("_id in", ids)}.
     * 
     * @param keys
     *            The keys for the documents to load.
     * @return The objects with the specified ids.
     */
    public <T> List<T> getByKeys(Iterable<Key<T>> keys);

    /**
     * Returns the {@link MongoCollection} for the specified class.
     * 
     * @param clazz
     *            The {@link Class} to return the collection for.
     * @return The default durability for write operations.
     */
    public MongoCollection getCollection(Class<?> clazz);

    /**
     * Gets the count this kind ({@link MongoCollection}).
     * 
     * @param clazz
     *            The class of objects to count.
     * @return The number of objects in the collection for the {@link Class}.
     */
    public <T> long getCount(Class<T> clazz);

    /**
     * Gets the count of items returned by this query; same as
     * {@code query.countAll()}.
     * 
     * @param query
     *            The criteria for the objects to count.
     * @return The number of objects in the collection matching the query.
     */
    public <T> long getCount(Query<T> query);

    /**
     * Gets the count this kind ({@link MongoCollection}).
     * 
     * @param entity
     *            The prototype for the kind of objects to count.
     * @return The number of objects in the collection for the entity.
     */
    public <T> long getCount(T entity);

    /**
     * Returns the {@link MongoDatabase} for the data store.
     * 
     * @return The {@link MongoDatabase} for the data store.
     */
    public MongoDatabase getDatabase();

    /**
     * Gets the default durability for write operations.
     * 
     * @return The default durability for write operations.
     */
    public Durability getDefaultDurability();

    /**
     * Creates a (type-safe) reference to the entity; if stored this will become
     * a {@link DocumentReference}
     * 
     * @param entity
     *            The entity to retreive the key for.
     * @return The key for the entity.
     */
    public <T> Key<T> getKey(T entity);

    /**
     * Returns the {@link MongoClient} for the data store.
     * 
     * @return The {@link MongoClient} for the data store.
     */
    public MongoClient getMongoClient();

    /**
     * Inserts the entities (Objects) and updates the @Id field
     * 
     * @param entities
     *            The entities to insert.
     * @param durability
     *            The required durability for the insert.
     * @return The keys identifying the documents.
     */
    public <T> Iterable<Key<T>> insert(Iterable<T> entities,
            Durability durability);

    /**
     * Inserts the entities (Objects) and updates the @Id field
     * 
     * @param entity
     *            The entity to insert.
     * @return The key identifying the document.
     */
    public <T> Key<T> insert(T entity);

    /**
     * Inserts the entities (Objects) and updates the @Id field
     * 
     * @param entities
     *            The entities to insert.
     * @return The keys identifying the documents.
     */
    public <T> Iterable<Key<T>> insert(T... entities);

    /**
     * Inserts the entities (Objects) and updates the @Id field
     * 
     * @param entity
     *            The entity to insert.
     * @param durability
     *            The required durability for the insert.
     * @return The key identifying the document.
     */
    public <T> Key<T> insert(T entity, Durability durability);

    /**
     * Work as if you did an update with each field in the entity doing a $set;
     * Only at the top level of the entity.
     * 
     * @param entity
     *            The entity to update in MongoDB.
     * @return The key identifying the document.
     */
    public <T> Key<T> merge(T entity);

    /**
     * Work as if you did an update with each field in the entity doing a $set;
     * Only at the top level of the entity.
     * 
     * @param entity
     *            The entity to update in MongoDB.
     * @param durability
     *            The required durability for the update.
     * @return The key identifying the document.
     */
    public <T> Key<T> merge(T entity, Durability durability);

    // FIXME - Add MapReduce command support back.
    // @SuppressWarnings("rawtypes")
    // /**
    // * Runs a map/reduce job at the server; this should be used with a server
    // version 1.7.4 or higher
    // * @param <T> The type of resulting data
    // * @param outputType The type of resulting data; inline is not working yet
    // * @param type MapreduceType
    // * @param q The query (only the criteria, limit and sort will be used)
    // * @param map The map function, in javascript, as a string
    // * @param reduce The reduce function, in javascript, as a string
    // * @param finalize The finalize function, in javascript, as a string; can
    // be null
    // * @param scopeFields Each map entry will be a global variable in all the
    // functions; can be null
    // * @return counts and stuff
    // */
    // <T> MapreduceResults<T> mapReduce(MapreduceType type, Query q, String
    // map,
    // String reduce, String finalize, Map<String, Object> scopeFields,
    // Class<T> outputType);
    //
    // /**
    // * Runs a map/reduce job at the server; this should be used with a server
    // * version 1.7.4 or higher
    // *
    // * @param <T>
    // * The type of resulting data
    // * @param type
    // * MapreduceType
    // * @param q
    // * The query (only the criteria, limit and sort will be used)
    // * @param outputType
    // * The type of resulting data; inline is not working yet
    // * @param baseCommand
    // * The base command to fill in and send to the server
    // * @return counts and stuff
    // */
    // <T> MapreduceResults<T> mapReduce(MapreduceType type, Query q,
    // Class<T> outputType, MapReduce baseCommand);

    /**
     * Returns a new query based on the example object.
     * 
     * @param example
     *            The Object with the query fields.
     * @return The constructed query with the specified filters.
     */
    public <T> Query<T> queryByExample(T example);

    /**
     * Saves the entities (Objects) and updates the @Id field
     * 
     * @param entities
     *            The entities to save.
     * @return The keys identifying the document.
     */
    public <T> Iterable<Key<T>> save(Iterable<T> entities);

    /**
     * Saves the entities (Objects) and updates the @Id field, with the
     * Durability
     * 
     * @param entities
     *            The entities to save.
     * @param durability
     *            The required durability for the update.
     * @return The keys identifying the document.
     */
    public <T> Iterable<Key<T>> save(Iterable<T> entities, Durability durability);

    /**
     * Saves the entities (Objects) and updates the @Id field.
     * 
     * @param entities
     *            The entities to save.
     * @return The keys identifying the document.
     */
    public <T> Iterable<Key<T>> save(T... entities);

    /**
     * Saves the entity (Object) and updates the @Id field.
     * 
     * @param entity
     *            The entity to save.
     * @return The key identifying the document.
     */
    public <T> Key<T> save(T entity);

    /**
     * Saves the entity (Object) and updates the @Id field if not set using the
     * provided Durability
     * 
     * @param entity
     *            The entity to save.
     * @param durability
     *            The required durability for the update.
     * @return The key identifying the document.
     */
    public <T> Key<T> save(T entity, Durability durability);

    /**
     * Sets the default durability for the future write operations.
     * 
     * @param durability
     *            The default durability for the future write operations.
     */
    public void setDefaultDurability(Durability durability);

    /**
     * Updates the entity with the operations; this is an atomic operation.
     * 
     * @param key
     *            The key to select the document of interest.
     * @param ops
     *            The updates to apply.
     * @return The number of documents updated.
     */
    public <T> long update(Key<T> key, UpdateOperations<T> ops);

    /**
     * Updates all entities found with the operations; this is an atomic
     * operation per entity.
     * 
     * @param query
     *            The query to select the document of interest.
     * @param ops
     *            The updates to apply.
     * @return The number of documents updated.
     */
    public <T> long update(Query<T> query, UpdateOperations<T> ops);

    /**
     * Updates all entities found with the operations, if nothing is found
     * insert the update as an entity if "createIfMissing" is true; this is an
     * atomic operation per entity.
     * 
     * @param query
     *            The query to select the document of interest.
     * @param ops
     *            The updates to apply.
     * @param createIfMissing
     *            If true perform an upsert.
     * @return The number of documents updated.
     */
    public <T> long update(Query<T> query, UpdateOperations<T> ops,
            boolean createIfMissing);

    /**
     * Updates all entities found with the operations, if nothing is found
     * insert the update as an entity if "createIfMissing" is true; this is an
     * atomic operation per entity.
     * 
     * @param query
     *            The query to select the document of interest.
     * @param ops
     *            The updates to apply.
     * @param createIfMissing
     *            If true perform an upsert.
     * @param durability
     *            The required durability for the update.
     * @return The number of documents updated.
     */
    public <T> long update(Query<T> query, UpdateOperations<T> ops,
            boolean createIfMissing, Durability durability);

    /**
     * Updates the entity with the operations; this is an atomic operation
     * 
     * @param entity
     *            The entity to select the document of interest.
     * @param ops
     *            The updates to apply.
     * @return The number of documents updated.
     */
    public <T> long update(T entity, UpdateOperations<T> ops);

    /**
     * Updates the first entity found with the operations, if nothing is found
     * insert the update as an entity if "createIfMissing" is true; this is an
     * atomic operation per entity
     * 
     * @param query
     *            The query to select the document of interest.
     * @param entity
     *            The updated entity.
     * @param createIfMissing
     *            If true perform an upsert.
     * @return The number of documents updated.
     */
    public <T> long updateFirst(Query<T> query, T entity,
            boolean createIfMissing);

    /**
     * Updates the first entity found with the operations; this is an atomic
     * operation
     * 
     * @param query
     *            The query to select the document of interest.
     * @param ops
     *            The updates to apply.
     * @return The number of documents updated.
     */
    public <T> long updateFirst(Query<T> query, UpdateOperations<T> ops);

    /**
     * Updates the first entity found with the operations, if nothing is found
     * insert the update as an entity if "createIfMissing" is true; this is an
     * atomic operation per entity
     * 
     * @param query
     *            The query to select the document of interest.
     * @param ops
     *            The updates to apply.
     * @param createIfMissing
     *            If true perform an upsert.
     * @return The number of documents updated.
     */
    public <T> long updateFirst(Query<T> query, UpdateOperations<T> ops,
            boolean createIfMissing);

    /**
     * Updates the first entity found with the operations, if nothing is found
     * insert the update as an entity if "createIfMissing" is true; this is an
     * atomic operation per entity
     * 
     * @param query
     *            The query to select the document of interest.
     * @param ops
     *            The updates to apply.
     * @param createIfMissing
     *            If true perform an upsert.
     * @param durability
     *            The required durability for the update.
     * @return The number of documents updated.
     */
    public <T> long updateFirst(Query<T> query, UpdateOperations<T> ops,
            boolean createIfMissing, Durability durability);
}