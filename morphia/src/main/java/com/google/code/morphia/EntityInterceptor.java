package com.google.code.morphia;

import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.google.code.morphia.annotations.PostLoad;
import com.google.code.morphia.annotations.PostPersist;
import com.google.code.morphia.annotations.PreLoad;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.annotations.PreSave;
import com.google.code.morphia.converters.Converter;

/** Interface for intercepting @Entity lifecycle events */
public interface EntityInterceptor {
    /** see {@link PrePersist} */
    void prePersist(Object entity, DocumentBuilder dbObj, Converter converter);

    /** see {@link PreSave} */
    void preSave(Object entity, DocumentBuilder dbObj, Converter converter);

    /** see {@link PostPersist} */
    void postPersist(Object entity, DocumentBuilder dbObj, Converter converter);

    /** see {@link PreLoad} */
    void preLoad(Object entity, DocumentBuilder dbObj, Converter converter);

    /** see {@link PostLoad} */
    void postLoad(Object entity, DocumentBuilder dbObj, Converter converter);
}
