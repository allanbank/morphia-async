package com.google.code.morphia;

import com.allanbank.mongodb.MongoClient;
import com.google.code.morphia.dao.BasicDAO;

@Deprecated //use dao.BasicDAO
public class DAO<T, K> extends BasicDAO<T, K> {
	public DAO(Class<T> entityClass, MongoClient mongo, Morphia morphia, String dbName) {
		super(entityClass, mongo, morphia, dbName);
	}
	
	public DAO(Class<T> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
	
}
