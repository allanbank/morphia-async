/*
 *         Copyright 2013 Allanbank Consulting, Inc.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.utils.ReflectionUtils;

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
    private final Class<?> declaredClass;

    /** The field being mapped. */
    private final Field field;

    /** If true then this is the id field. */
    private boolean id;

    /** If true missing document references are silently ignored. */
    private boolean ignoreMissing;

    /** The index for the field. */
    private IndexState index;

    /** If true a proxy object should be created for use-time loading. */
    private boolean lazy;

    /**
     * The mapped field name for the field from the {@link Embedded},
     * {@link Reference} or {@link Property} annotations.
     */
    private String mappedFieldName;

    /** The parameter classes for the field. */
    private Class<?>[] parameterClasses = null;

    /**
     * The strategy for saving the field. Can be set to {@link Strategy#NONE} by
     * the {@link Transient} or {@link NotSaved} annotation.
     */
    private Strategy strategy = Strategy.MAP;

    /**
     * If true then the field is the version of the document and should be
     * incremented before each save and the previous version used as part of the
     * query on an update.
     */
    private boolean version;

    /**
     * If true then the field is written, may be false by the {@link Transient}
     * or {@link NotSaved} anonotation or marking the field as {@code transient}
     * .
     */
    private boolean written;

    /**
     * Creates a new MappedField. Used with entries of Collections.
     */
    public MappedField() {
        this.alsoLoadNames = new HashSet<String>();
        this.concreteClass = null;
        this.constructorArgs = new ArrayList<String>();
        this.declaredClass = null;
        this.field = null;
        this.id = false;
        this.ignoreMissing = false;
        this.index = null;
        this.lazy = false;
        this.mappedFieldName = null;
        this.strategy = Strategy.MAP;
        this.version = false;
        this.written = true;
    }

    /**
     * Creates a new MappedField.
     * 
     * @param field
     *            The reflected field being mapped.
     */
    public MappedField(final Field field) {
        this.alsoLoadNames = new HashSet<String>();
        this.concreteClass = field.getType();
        this.constructorArgs = new ArrayList<String>();
        this.declaredClass = field.getType();
        this.field = field;
        this.id = false;
        this.ignoreMissing = false;
        this.index = null;
        this.lazy = false;
        this.mappedFieldName = field.getName();
        this.strategy = Strategy.MAP;
        this.version = false;
        this.written = true;

        // So we can write the value.
        this.field.setAccessible(true);
    }

    /**
     * Gets the value of the field from the {@code object}.
     * 
     * @param object
     *            The object to get the value of the field from.
     * @return The value of the field from the {@code object}.
     * @throws MappingException
     *             On a faiilure reading the field from the {@code object}.
     */
    public Object get(final Object object) throws MappingException {
        try {
            return field.get(object);
        }
        catch (final IllegalArgumentException e) {
            throw new MappingException("Failure reading the '"
                    + field.getName() + "' field from a '"
                    + object.getClass().getSimpleName() + "': "
                    + e.getMessage(), e);
        }
        catch (final IllegalAccessException e) {
            throw new MappingException("Failure reading the '"
                    + field.getName() + "' field from a '"
                    + object.getClass().getSimpleName() + "': "
                    + e.getMessage(), e);
        }
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
     * Returns the index value.
     * 
     * @return The index value.
     */
    public IndexState getIndex() {
        return index;
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
     * Returns the resolved class for the field. This it the concrete class if
     * set or the declared class if not.
     * 
     * @return The resolved class for the field.
     */
    public Class<?> getResolvedClass() {
        if (concreteClass != null) {
            return concreteClass;
        }
        return declaredClass;
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
     * Returns the Class for the i'th type parameter.
     * 
     * @param i
     *            The intex for the parameter. The first parameter is index
     *            zero.
     * @return The i'th type parameter index.
     */
    public Class<? extends Object> getTypeArgumentClass(final int i) {
        if (parameterClasses == null) {
            final Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType) type;

                final Type[] args = pt.getActualTypeArguments();

                final Class<?>[] clazzes = new Class[args.length];
                for (int j = 0; j < args.length; ++j) {
                    final Type arg = args[j];
                    clazzes[j] = ReflectionUtils.getClass(arg);
                }
                parameterClasses = clazzes;
            }
            else {
                // This is not going to end pretty.
                parameterClasses = new Class[0];
            }
        }
        if (i < parameterClasses.length) {
            return parameterClasses[i];
        }
        return null;
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
     * Returns the version value.
     * 
     * @return The version value.
     */
    public boolean isVersion() {
        return version;
    }

    /**
     * Returns the written value.
     * 
     * @return The written value.
     */
    public boolean isWritten() {
        return written;
    }

    /**
     * Resets the {@link MappedField} for the item specified. Used with items in
     * Collections.
     * 
     * @param itemName
     *            The name for the field.
     * @param clazz
     *            The type of the field.
     */
    public void mapFor(final String itemName,
            final Class<? extends Object> clazz) {
        setMappedFieldName(itemName);
        setConcreteClass(clazz);
    }

    /**
     * Sets the value of the field into the {@code object}.
     * 
     * @param object
     *            The object to get the value of the field from.
     * @param value
     *            the value to set in the field.
     * @throws MappingException
     *             On a faiilure writing the {@code value} into the field of the
     *             {@code object}.
     */
    public void set(final Object object, final Object value)
            throws MappingException {
        try {
            field.set(object, value);
        }
        catch (final IllegalArgumentException e) {
            throw new MappingException("Failure writing the '"
                    + field.getName() + "' field to a '"
                    + object.getClass().getSimpleName() + "': "
                    + e.getMessage(), e);
        }
        catch (final IllegalAccessException e) {
            throw new MappingException("Failure writing the '"
                    + field.getName() + "' field to a '"
                    + object.getClass().getSimpleName() + "': "
                    + e.getMessage(), e);
        }
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
     * Sets the value of index to the new value.
     * 
     * @param index
     *            The new value for the index.
     */
    public void setIndex(final IndexState index) {
        this.index = index;
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
     * Sets the value of version to the new value.
     * 
     * @param version
     *            The new value for the version.
     */
    public void setVersion(final boolean version) {
        this.version = version;
    }

    /**
     * Sets the value of written to the new value.
     * 
     * @param written
     *            The new value for the written.
     */
    public void setWritten(final boolean written) {
        this.written = written;
    }

    /**
     * Strategy provides the highlevel method used to save the field.
     * 
     * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
     */
    public enum Strategy {
        /** Map the field into an appropriate BSON element. */
        MAP,

        /** Don't save or read the field. May be done by the entity listeners. */
        NONE,

        /**
         * Don't save the field in the enclosing object document. Use a document
         * reference.
         */
        REFERENCE,

        /** Serialize the field into a BSON binary element with compression. */
        SERIALIZED_COMPRESSED,

        /** Serialize the field into a BSON binary element without compression. */
        SERIALIZED_UNCOMPRESSED;
    }

    /**
     * Returns true if the field contains the specified annotation.
     * 
     * @param annotationClass
     *            The class for the annotation.
     * @return True if the field contains the annotation.
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return (field.getAnnotation(annotationClass) != null);
    }
}
