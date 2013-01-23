/**
 * 
 */
package com.google.code.morphia;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.MongoFactory;
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
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void setUp() {
        this.db = this.mongo.getDatabase("morphia_test");
        this.ds = this.morphia.createDatastore(this.mongo, this.db.getName());
        this.ads = (AdvancedDatastore) ds;
        // ads.setDecoderFact(LazyDBDecoder.FACTORY);
    }

    protected void cleanup() {
        // this.mongo.dropDatabase("morphia_test");
        for (MappedClass mc : morphia.getMapper().getMappedClasses())
            // if( mc.getEntityAnnotation() != null )
            db.getCollection(mc.getCollectionName()).drop();

    }

    @After
    public void tearDown() throws IOException {
        cleanup();
        mongo.close();
    }
}
