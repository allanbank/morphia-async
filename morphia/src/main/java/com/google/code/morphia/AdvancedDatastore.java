package com.google.code.morphia;

import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.DocumentReference;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

/**
 * <p>
 * This interface exposes advanced {@link Datastore} features, like interacting
 * with DBObject and low-level options.
 * 
 * <ul>
 * <li>Implements matching methods from the {@code Datastore} interface but with
 * a specified kind (collection name), or raw types (DBObject).</li>
 * </ul>
 * </p>
 * 
 * @author ScottHernandez
 */
public interface AdvancedDatastore extends Datastore {
    /**
     * Creates a reference to the entity (using the current DB -can be null-,
     * the collectionName, and id)
     */
    <T, V> DocumentReference createRef(Class<T> clazz, V id);

    /**
     * Creates a reference to the entity (using the current DB -can be null-,
     * the collectionName, and id)
     */
    <T> DocumentReference createRef(T entity);

    /** Find the given entity (by collectionName/id); */
    <T> T get(Class<T> clazz, DocumentReference ref);

    /** Gets the count this kind */
    long getCount(String kind);

    <T, V> T get(String kind, Class<T> clazz, V id);

    <T> Query<T> find(String kind, Class<T> clazz);

    <T, V> Query<T> find(String kind, Class<T> clazz, String property, V value,
            int offset, int size);

    <T> Key<T> save(String kind, T entity);

    /** No validation or conversion is done to the id */
    @Deprecated
    <T> long delete(String kind, T id);

    <T, V> long delete(String kind, Class<T> clazz, V id);

    <T> Key<T> insert(String kind, T entity);

    <T> Key<T> insert(T entity);

    <T> Key<T> insert(T entity, Durability wc);

    <T> Iterable<Key<T>> insert(T... entities);

    <T> Iterable<Key<T>> insert(Iterable<T> entities, Durability wc);

    <T> Iterable<Key<T>> insert(String kind, Iterable<T> entities);

    <T> Iterable<Key<T>> insert(String kind, Iterable<T> entities, Durability wc);

    <T> Query<T> createQuery(String kind, Class<T> clazz);

    // DBObject implementations; in case we don't have features impl'd yet
    <T> Query<T> createQuery(Class<T> kind, Document q);

    <T> Query<T> createQuery(String kind, Class<T> clazz, Document q);

    /** Returns a new query based on the example object */
    <T> Query<T> queryByExample(String kind, T example);

    <T> UpdateOperations<T> createUpdateOperations(Class<T> kind, Document ops);
}