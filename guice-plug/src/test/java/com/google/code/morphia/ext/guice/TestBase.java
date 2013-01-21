/**
 * 
 */
package com.google.code.morphia.ext.guice;

import org.junit.After;
import org.junit.Before;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.MongoFactory;
import com.google.code.morphia.AdvancedDatastore;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.mapping.MappedClass;

public abstract class TestBase {
    protected MongoClient mongo;
    protected MongoDatabase db;
    protected Datastore ds;
    protected AdvancedDatastore ads;
    protected Morphia morphia = new Morphia();

    protected TestBase() {
        try {
            this.mongo = MongoFactory.createClient("mongodb://localhost:27017");
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void setUp() {
        this.db = this.mongo.getDatabase("morphia_test");
        this.ds = this.morphia.createDatastore(this.mongo, this.db.getName());
        this.ads = (AdvancedDatastore) this.ds;
    }

    protected void dropDB() {
        // this.mongo.dropDatabase("morphia_test");
        for (final MappedClass mc : this.morphia.getMapper().getMappedClasses()) {
            // if( mc.getEntityAnnotation() != null )
            this.db.getCollection(mc.getCollectionName()).drop();
        }

    }

    @After
    public void tearDown() {
        dropDB();
    }
}
