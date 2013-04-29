package com.google.code.morphia.query;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.allanbank.mongodb.bson.Document;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Id;

/**
 * 
 * @author Scott Hernandez
 */
@SuppressWarnings("unchecked")
public class MorphiaKeyIterator<T> implements Iterable<Key<T>>,
        Iterator<Key<T>> {
    Class<T> clazz;
    String kind;
    Morphia m;
    Iterator wrapped;

    public MorphiaKeyIterator(final Iterator it, final Morphia m,
            final Class<T> clazz, final String kind) {
        this.wrapped = it;
        this.m = m;
        this.clazz = clazz;
        this.kind = kind;
    }

    @Override
    public boolean hasNext() {
        if (wrapped == null) {
            return false;
        }
        return wrapped.hasNext();
    }

    @Override
    public Iterator<Key<T>> iterator() {
        return this;
    }

    @Override
    public Key<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        final Document dbObj = (Document) wrapped.next();
        final Key<T> key = new Key<T>(kind, dbObj.get(Id.ID_FIELD));
        key.setKindClass(this.clazz);
        return key;
    }

    @Override
    public void remove() {
        wrapped.remove();
    }
}