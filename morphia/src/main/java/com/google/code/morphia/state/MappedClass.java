/*
 *         Copyright 2010-2013 Allanbank Consulting, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.code.morphia.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.allanbank.mongodb.Durability;
import com.google.code.morphia.EntityInterceptor;
import com.google.code.morphia.annotations.CappedAt;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.converters.FieldConverter;

/**
 * MappedClass provides details on how to map the class.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class MappedClass {

    /**
     * The list of additional field converters provided by the application to
     * convert fields of this class.
     */
    private final List<FieldConverter<?>> additionalFieldConverters;

    /**
     * Returns the setting for creating a capped collection for the entity based
     * on the {@link Entity#cap()} value.
     */
    private CappedAt cappedAt;

    /**
     * Return true if the classname for the entity should be stored in the
     * document.
     */
    private boolean classnameStored;

    /**
     * The concrete type for the Class from the {@link Embedded} annotation.
     * TODO: Does this make sense?
     */
    private Class<?> concreteClass;

    /**
     * Controls the default write concern or {@link Durability} to use. See
     * {@link Durability#valueOf(String)} for acceptable values.
     */
    private Durability durability;

    /** The mapped fields from the class other than the id and version fields. */
    private final List<MappedField> fields;

    /** The id field for the class. */
    private MappedField idField;

    /** The set of indexes to create on the collection for the class. */
    private final List<Index> indexes;

    /**
     * The mapped field name for the field from the {@link Embedded} or
     * {@link Entity} annotation. TODO: Does this make sense?
     */
    private String mappedFieldName;

    /** The mapped fields from the class other than the id and version fields. */
    private final List<MappedMethod> methods;

    /** The list of listeners for entity persistence events. */
    private final List<EntityInterceptor> persistenceListeners;

    /** Controls if non-primary queries are OK. */
    private boolean queryNonPrimaryOk;

    /** Marker for if automatic field saving should be performed. */
    private boolean saved;

    /** The version field for the class. */
    private MappedField versionField;

    /**
     * Creates a new MappedClass.
     */
    public MappedClass() {
        fields = new ArrayList<MappedField>();
        methods = new ArrayList<MappedMethod>();
        additionalFieldConverters = new ArrayList<FieldConverter<?>>();
        persistenceListeners = new ArrayList<EntityInterceptor>();
        indexes = new ArrayList<Index>();
    }

    /**
     * Returns the additionalFieldConverters value.
     * 
     * @return The additionalFieldConverters value.
     */
    public List<FieldConverter<?>> getAdditionalFieldConverters() {
        return Collections.unmodifiableList(additionalFieldConverters);
    }

    /**
     * Returns the cappedAt value.
     * 
     * @return The cappedAt value.
     */
    public CappedAt getCappedAt() {
        return cappedAt;
    }

    /**
     * Returns the concreteClass value.
     * 
     * @return The concreteClass value.
     */
    public Class<?> getConcreteClass() {
        return concreteClass;
    }

    /**
     * Returns the durability value.
     * 
     * @return The durability value.
     */
    public Durability getDurability() {
        return durability;
    }

    /**
     * Returns the fields value.
     * 
     * @return The fields value.
     */
    public List<MappedField> getFields() {
        return Collections.unmodifiableList(fields);
    }

    /**
     * Returns the idField value.
     * 
     * @return The idField value.
     */
    public MappedField getIdField() {
        return idField;
    }

    /**
     * Returns the indexes value.
     * 
     * @return The indexes value.
     */
    public List<Index> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }

    /**
     * Returns the mappedFieldName value.
     * 
     * @return The mappedFieldName value.
     */
    public String getMappedFieldName() {
        return mappedFieldName;
    }

    /**
     * Returns the methods value.
     * 
     * @return The methods value.
     */
    public List<MappedMethod> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    /**
     * Returns the persistenceListeners value.
     * 
     * @return The persistenceListeners value.
     */
    public List<EntityInterceptor> getPersistenceListeners() {
        return Collections.unmodifiableList(persistenceListeners);
    }

    /**
     * Returns the versionField value.
     * 
     * @return The versionField value.
     */
    public MappedField getVersionField() {
        return versionField;
    }

    /**
     * Returns the classnameStored value.
     * 
     * @return The classnameStored value.
     */
    public boolean isClassnameStored() {
        return classnameStored;
    }

    /**
     * Returns the queryNonPrimaryOk value.
     * 
     * @return The queryNonPrimaryOk value.
     */
    public boolean isQueryNonPrimaryOk() {
        return queryNonPrimaryOk;
    }

    /**
     * Returns the saved value.
     * 
     * @return The saved value.
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Sets the value of additionalFieldConverters to the new value.
     * 
     * @param additionalFieldConverters
     *            The new value for the additionalFieldConverters.
     */
    public void setAdditionalFieldConverters(
            final List<FieldConverter<?>> additionalFieldConverters) {
        this.additionalFieldConverters.clear();
        if (additionalFieldConverters != null) {
            this.additionalFieldConverters.addAll(additionalFieldConverters);
        }
    }

    /**
     * Sets the value of cappedAt to the new value.
     * 
     * @param cappedAt
     *            The new value for the cappedAt.
     */
    public void setCappedAt(final CappedAt cappedAt) {
        this.cappedAt = cappedAt;
    }

    /**
     * Sets the value of classnameStored to the new value.
     * 
     * @param classnameStored
     *            The new value for the classnameStored.
     */
    public void setClassnameStored(final boolean classnameStored) {
        this.classnameStored = classnameStored;
    }

    /**
     * Sets the value of concreteClass to the new value.
     * 
     * @param concreteClass
     *            The new value for the concreteClass.
     */
    public void setConcreteClass(final Class<?> concreteClass) {
        this.concreteClass = concreteClass;
    }

    /**
     * Sets the value of durability to the new value.
     * 
     * @param durability
     *            The new value for the durability.
     */
    public void setDurability(final Durability durability) {
        this.durability = durability;
    }

    /**
     * Sets the value of fields to the new value.
     * 
     * @param fields
     *            The new value for the fields.
     */
    public void setFields(final List<MappedField> fields) {
        this.fields.clear();
        if (fields != null) {
            this.fields.addAll(fields);
        }
    }

    /**
     * Sets the value of idField to the new value.
     * 
     * @param idField
     *            The new value for the idField.
     */
    public void setIdField(final MappedField idField) {
        this.idField = idField;
    }

    /**
     * Sets the value of indexes to the new value.
     * 
     * @param indexes
     *            The new value for the indexes.
     */
    public void setIndexes(final List<Index> indexes) {
        this.indexes.clear();
        if (indexes != null) {
            this.indexes.addAll(indexes);
        }
    }

    /**
     * Sets the value of mappedFieldName to the new value.
     * 
     * @param mappedFieldName
     *            The new value for the mappedFieldName.
     */
    public void setMappedFieldName(final String mappedFieldName) {
        this.mappedFieldName = mappedFieldName;
    }

    /**
     * Sets the value of methods to the new value.
     * 
     * @param methods
     *            The new value for the methods.
     */
    public void setMethods(final List<MappedMethod> methods) {
        this.methods.clear();
        if (methods != null) {
            this.methods.addAll(methods);
        }
    }

    /**
     * Sets the value of persistenceListeners to the new value.
     * 
     * @param persistenceListeners
     *            The new value for the persistenceListeners.
     */
    public void setPersistenceListeners(
            final List<EntityInterceptor> persistenceListeners) {
        this.persistenceListeners.clear();
        if (persistenceListeners != null) {
            this.persistenceListeners.addAll(persistenceListeners);
        }
    }

    /**
     * Sets the value of queryNonPrimaryOk to the new value.
     * 
     * @param queryNonPrimaryOk
     *            The new value for the queryNonPrimaryOk.
     */
    public void setQueryNonPrimaryOk(final boolean queryNonPrimaryOk) {
        this.queryNonPrimaryOk = queryNonPrimaryOk;
    }

    /**
     * Sets the value of saved to the new value.
     * 
     * @param saved
     *            The new value for the saved.
     */
    public void setSaved(final boolean saved) {
        this.saved = saved;
    }

    /**
     * Sets the value of versionField to the new value.
     * 
     * @param versionField
     *            The new value for the versionField.
     */
    public void setVersionField(final MappedField versionField) {
        this.versionField = versionField;
    }

}
