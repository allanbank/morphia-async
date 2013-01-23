package com.google.code.morphia.mapping;

import java.util.Map;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.google.code.morphia.mapping.cache.EntityCache;
import com.mongodb.DBObject;

/**
 * Simple mapper that just uses the Mapper.getOptions().converts
 * @author Scott Hernnadez
 *
 */
class ValueMapper implements CustomMapper {
	public void toDocument(Object entity, MappedField mf, DocumentBuilder builder, Map<Object, Document> involvedObjects, Mapper mapr) {
		try {
			mapr.converters.toDBObject(entity, mf, dbObject, mapr.getOptions());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void fromDocument(Document dbObject, MappedField mf, Object entity, EntityCache cache, Mapper mapr) {
		try {
			mapr.converters.fromDBObject(dbObject, mf, entity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
}
