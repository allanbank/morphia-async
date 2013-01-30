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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.code.morphia.annotations.AlsoLoad;
import com.google.code.morphia.annotations.ConstructorArgs;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.NotSaved;
import com.google.code.morphia.annotations.Property;
import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.annotations.Transient;

/**
 * MappedField provides the details on the how a field should be mapped.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class MappedField {

    /**
     * The set of names associated with the field via the {@link AlsoLoad}
     * annotation.
     */
    private final Set<String> alsoLoadNames;

    /**
     * The concrete type for the Class from the {@link Embedded},
     * {@link Reference} or {@link Property} annotations.
     */
    private Class<?> concreteClass;

    /**
     * The list of object fields that are used to construct the embedded object
     * from the embedded document. Derived from the {@link ConstructorArgs}
     * annotation.
     */
    private final List<String> constructorArgs;

    /** The declared type for the field from the class definition. */
    private Class<?> declaredClass;

    /** The field being mapped. */
    private Field field;

    /** If true then this is the id field. */
    private boolean id;

    /** If true missing document references are silently ignored. */
    private boolean ignoreMissing;

    /** If true a proxy object should be created for use-time loading. */
    private boolean lazy;

    /**
     * The mapped field name for the field from the {@link Embedded},
     * {@link Reference} or {@link Property} annotations.
     */
    private String mappedFieldName;

    /**
     * The strategy for saving the field. Can be set to {@link Strategy#NONE} by
     * the {@link Transient} or {@link NotSaved} annotation.
     */
    private Strategy strategy = Strategy.MAP;

    /**
     * Creates a new MappedField.
     */
    public MappedField() {
        alsoLoadNames = new HashSet<String>();
        constructorArgs = new ArrayList<String>();
    }

    /**
     * Returns the alsoLoadNames value.
     * 
     * @return The alsoLoadNames value.
     */
    public Set<String> getAlsoLoadNames() {
        return Collections.unmodifiableSet(alsoLoadNames);
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
     * Returns the constructorArgs value.
     * 
     * @return The constructorArgs value.
     */
    public List<String> getConstructorArgs() {
        return Collections.unmodifiableList(constructorArgs);
    }

    /**
     * Returns the declaredClass value.
     * 
     * @return The declaredClass value.
     */
    public Class<?> getDeclaredClass() {
        return declaredClass;
    }

    /**
     * Returns the field value.
     * 
     * @return The field value.
     */
    public Field getField() {
        return field;
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
     * Returns the strategy value.
     * 
     * @return The strategy value.
     */
    public Strategy getStrategy() {
        return strategy;
    }

    /**
     * Returns the id value.
     * 
     * @return The id value.
     */
    public boolean isId() {
        return id;
    }

    /**
     * Returns the ignoreMissing value.
     * 
     * @return The ignoreMissing value.
     */
    public boolean isIgnoreMissing() {
        return ignoreMissing;
    }

    /**
     * Returns the lazy value.
     * 
     * @return The lazy value.
     */
    public boolean isLazy() {
        return lazy;
    }

    /**
     * Sets the value of alsoLoadNames to the new value.
     * 
     * @param alsoLoadNames
     *            The new value for the alsoLoadNames.
     */
    public void setAlsoLoadNames(final Set<String> alsoLoadNames) {
        this.alsoLoadNames.clear();
        if (alsoLoadNames != null) {
            this.alsoLoadNames.addAll(alsoLoadNames);
        }
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
     * Sets the value of constructorArgs to the new value.
     * 
     * @param constructorArgs
     *            The new value for the constructorArgs.
     */
    public void setConstructorArgs(final List<String> constructorArgs) {
        this.constructorArgs.clear();
        if (constructorArgs != null) {
            this.constructorArgs.addAll(constructorArgs);
        }
    }

    /**
     * Sets the value of declaredClass to the new value.
     * 
     * @param declaredClass
     *            The new value for the declaredClass.
     */
    public void setDeclaredClass(final Class<?> declaredClass) {
        this.declaredClass = declaredClass;
    }

    /**
     * Sets the value of field to the new value.
     * 
     * @param field
     *            The new value for the field.
     */
    public void setField(final Field field) {
        this.field = field;
    }

    /**
     * Sets the value of id to the new value.
     * 
     * @param id
     *            The new value for the id.
     */
    public void setId(final boolean id) {
        this.id = id;
    }

    /**
     * Sets the value of ignoreMissing to the new value.
     * 
     * @param ignoreMissing
     *            The new value for the ignoreMissing.
     */
    public void setIgnoreMissing(final boolean ignoreMissing) {
        this.ignoreMissing = ignoreMissing;
    }

    /**
     * Sets the value of lazy to the new value.
     * 
     * @param lazy
     *            The new value for the lazy.
     */
    public void setLazy(final boolean lazy) {
        this.lazy = lazy;
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
     * Sets the value of strategy to the new value.
     * 
     * @param strategy
     *            The new value for the strategy.
     */
    public void setStrategy(final Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Strategy provides the highlevel method used to save the field.
     * 
     * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
     */
    public enum Strategy {
        /** Map the field into an appropriate BSON element. */
        MAP,

        /** Don't save the field. May be done by the entity listeners. */
        NONE,

        /** Serialize the field into a BSON binary element without compression. */
        SERIALIZE_UNCOMPRESSED,

        /** Serialize the field into a BSON binary element with compression. */
        SERIALIZED_COMPRESSED;
    }
}
