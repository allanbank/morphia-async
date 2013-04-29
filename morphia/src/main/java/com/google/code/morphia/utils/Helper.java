package com.google.code.morphia.utils;

import java.util.Map;

import com.allanbank.mongodb.ClosableIterator;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.bson.Document;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.MorphiaIterator;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryImpl;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateOpsImpl;

/**
 * Exposes driver related DBOBject stuff from Morphia objects
 * 
 * @author scotthernandez
 */
@SuppressWarnings("rawtypes")
public class Helper {
    public static MongoCollection getCollection(final Query q) {
        final QueryImpl qi = (QueryImpl) q;
        return qi.getCollection();
    }

    public static Document getCriteria(final Query q) {
        final QueryImpl qi = (QueryImpl) q;
        return qi.getQueryObject();
    }

    public static ClosableIterator<Document> getCursor(final Iterable it) {
        return ((MorphiaIterator) it).getCursor();
    }

    public static MongoDatabase getDB(final Datastore ds) {
        return ds.getDatabase();
    }

    public static Document getFields(final Query q) {
        final QueryImpl qi = (QueryImpl) q;
        return qi.getFieldsObject();
    }

    public static Document getSort(final Query q) {
        final QueryImpl qi = (QueryImpl) q;
        return qi.getSortObject();
    }

    public static Map<String, Map<String, Object>> getUpdateOperations(
            final UpdateOperations ops) {
        final UpdateOpsImpl uo = (UpdateOpsImpl) ops;
        return uo.getOps();
    }
}