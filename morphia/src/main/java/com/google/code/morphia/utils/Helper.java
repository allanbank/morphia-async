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
    public static Document getCriteria(Query q) {
        QueryImpl qi = (QueryImpl) q;
        return qi.getQueryObject();
    }

    public static Document getSort(Query q) {
        QueryImpl qi = (QueryImpl) q;
        return qi.getSortObject();
    }

    public static Document getFields(Query q) {
        QueryImpl qi = (QueryImpl) q;
        return qi.getFieldsObject();
    }

    public static MongoCollection getCollection(Query q) {
        QueryImpl qi = (QueryImpl) q;
        return qi.getCollection();
    }

    public static ClosableIterator<Document> getCursor(Iterable it) {
        return ((MorphiaIterator) it).getCursor();
    }

    public static Map<String, Map<String, Object>> getUpdateOperations(UpdateOperations ops) {
        UpdateOpsImpl uo = (UpdateOpsImpl) ops;
        return uo.getOps();
    }

    public static MongoDatabase getDB(Datastore ds) {
        return ds.getDB();
    }
}