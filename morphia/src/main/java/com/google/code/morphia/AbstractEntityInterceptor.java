/**
 * 
 */
package com.google.code.morphia;

import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.google.code.morphia.converters.Converter;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
public class AbstractEntityInterceptor implements EntityInterceptor {

    public void postLoad(Object ent, DocumentBuilder dbObj, Converter converter) {
    }

    public void postPersist(Object ent, DocumentBuilder dbObj,
            Converter converter) {
    }

    public void preLoad(Object ent, DocumentBuilder dbObj, Converter converter) {
    }

    public void prePersist(Object ent, DocumentBuilder dbObj,
            Converter converter) {
    }

    public void preSave(Object ent, DocumentBuilder dbObj, Converter converter) {
    }
}
