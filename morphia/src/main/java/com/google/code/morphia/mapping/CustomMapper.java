/**
 * 
 */
package com.google.code.morphia.mapping;

import java.util.Map;

import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.google.code.morphia.mapping.cache.EntityCache;

/**
 * A CustomMapper if one that implements the methods needed to map to/from
 * POJO/DBObject
 * 
 * @author skot
 * 
 */
public interface CustomMapper {
    void toDocument(Object entity, MappedField mf, DocumentBuilder builder,
            Map<Object, Document> involvedObjects, Mapper mapr);

    void fromDocument(Document dbObject, MappedField mf, Object entity,
            EntityCache cache, Mapper mapr);
}
