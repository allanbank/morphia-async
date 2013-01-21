/**
 * 
 */
package com.google.code.morphia.validation;

import org.junit.After;
import org.junit.Before;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.MongoFactory;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;

public abstract class TestBase {
	protected MongoClient mongo;
	protected MongoDatabase db;
	protected Datastore ds;
	protected Morphia morphia = new Morphia();
	
	protected TestBase() {
		try {
			this.mongo = MongoFactory.createClient("mongodb://localhost:27017");
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Before
	public void setUp() {
		this.mongo.getDatabase("morphia_test").drop();
		this.db = this.mongo.getDatabase("morphia_test");
		this.ds = this.morphia.createDatastore(this.mongo, this.db.getName());
	}
	
	@After
	public void tearDown() {
		// new ScopedFirstLevelCacheProvider().release();
	}
}
