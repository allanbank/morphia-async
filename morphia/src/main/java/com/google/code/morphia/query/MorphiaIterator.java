package com.google.code.morphia.query;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.allanbank.mongodb.ClosableIterator;
import com.allanbank.mongodb.bson.Document;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.mapping.cache.EntityCache;

/**
 * 
 * @author Scott Hernandez
 */
@SuppressWarnings("unchecked")
public class MorphiaIterator<T, V> implements Iterable<V>, Iterator<V> {
    // private final String kind;
    private final EntityCache cache;
    private final Class<T> clazz;
    private long driverTime = 0;
    private final Morphia m;
    private long mapperTime = 0;
    private final ClosableIterator<Document> wrapped;

    public MorphiaIterator(final ClosableIterator<Document> it,
            final Morphia m, final Class<T> clazz, final String kind,
            final EntityCache cache) {
        this.wrapped = it;
        this.m = m;
        this.clazz = clazz;
        // this.kind = kind;
        this.cache = cache;
    }

    public void close() {
        if (wrapped != null) {
            wrapped.close();
        }
    }

    public ClosableIterator<Document> getCursor() {
        return wrapped;
    }

    /** Returns the time spent calling the driver in ms */
    public long getDriverTime() {
        return driverTime;
    }

    /** Returns the time spent calling the mapper in ms */
    public long getMapperTime() {
        return mapperTime;
    }

    @Override
    public boolean hasNext() {
        if (wrapped == null) {
            return false;
        }
        final long start = System.currentTimeMillis();
        final boolean ret = wrapped.hasNext();
        driverTime += System.currentTimeMillis() - start;
        return ret;
    }

    @Override
    public Iterator<V> iterator() {
        return this;
    }

    @Override
    public V next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        final Document dbObj = getNext();
        return processItem(dbObj);
    }

    @Override
    public void remove() {
        final long start = System.currentTimeMillis();
        wrapped.remove();
        driverTime += System.currentTimeMillis() - start;
    }

    protected Document getNext() {
        final long start = System.currentTimeMillis();
        final Document dbObj = wrapped.next();
        driverTime += System.currentTimeMillis() - start;
        return dbObj;
    }

    protected V processItem(final Document dbObj) {
        final long start = System.currentTimeMillis();
        final V entity = (V) m.fromDocument(clazz, dbObj, cache);
        mapperTime += System.currentTimeMillis() - start;
        return entity;
    }
}