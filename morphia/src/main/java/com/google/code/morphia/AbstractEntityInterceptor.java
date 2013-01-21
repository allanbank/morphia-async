/**
 * 
 */
package com.google.code.morphia;

import com.allanbank.mongodb.bson.Document;
import com.google.code.morphia.mapping.Mapper;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 *
 */
public class AbstractEntityInterceptor implements EntityInterceptor {
	
	public void postLoad(Object ent, Document dbObj, Mapper mapr) {
	}
	
	public void postPersist(Object ent, Document dbObj, Mapper mapr) {
	}
	
	public void preLoad(Object ent, Document dbObj, Mapper mapr) {
	}
	
	public void prePersist(Object ent, Document dbObj, Mapper mapr) {
	}
	
	public void preSave(Object ent, Document dbObj, Mapper mapr) {
	}
}
