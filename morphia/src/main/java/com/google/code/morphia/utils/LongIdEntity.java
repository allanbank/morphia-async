package com.google.code.morphia.utils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.annotations.Transient;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;

public abstract class LongIdEntity {
    @Transient
    final protected Datastore ds;

    @Id
    protected Long myLongId;

    protected LongIdEntity(final Datastore ds) {
        this.ds = ds;
    }

    @PrePersist
    void prePersist() {
        if (myLongId == null) {
            final String collName = ds.getCollection(getClass()).getName();
            final Query<StoredId> q = ds.find(StoredId.class, "_id", collName);
            final UpdateOperations<StoredId> uOps = ds.createUpdateOperations(
                    StoredId.class).inc("value");
            StoredId newId = ds.findAndModify(q, uOps);
            if (newId == null) {
                newId = new StoredId(collName);
                ds.save(newId);
            }

            myLongId = newId.getValue();
        }
    }

    /**
     * Used to store counters for other entities.
     * 
     * @author skot
     * 
     */

    @Entity(value = "ids", noClassnameStored = true)
    public static class StoredId {
        protected Long value = 1L;
        final @Id
        String className;

        public StoredId(final String name) {
            className = name;
        }

        protected StoredId() {
            className = "";
        }

        public Long getValue() {
            return value;
        }
    }
}
