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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.ReadPreference;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.google.code.morphia.EntityInterceptor;
import com.google.code.morphia.annotations.CappedAt;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.converters.FieldConverter;
import com.google.code.morphia.mapping.MappingException;

/**
 * MappedClass provides details on how to map the class.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class MappedClass {

    /**
     * Name of the field holding the name of the class mapped into the document.
     */
    public static final String CLASS_NAME_FIELD = "className";

    /** The logger for the class. */
    public static final Logger LOG = Logger.getLogger(MappedClass.class
            .getName());

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
     */
    private final Class<?> clazz;

    /** The name of the collection to use for the entity. */
    private String collectionName;

    /**
     * The complete set of derived classes we have seen.
     */
    private final Set<Class<?>> derivedClasses;

    /**
     * The mapping of a field name that uniquely identifies a derived class and
     * the class it maps to. This map is used to quickly identify which derived
     * class is being deserialized based on the fields seen in the document.
     */
    private Map<String, Class<?>> derivedMapping;

    /**
     * Controls the default write concern or {@link Durability} to use. See
     * {@link Durability#valueOf(String)} for acceptable values.
     */
    private Durability durability;

    /** The mapped fields from the class other than the id and version fields. */
    private final List<MappedField> fields;

    /** The mapped fields indexed by name. */
    private final Map<String, MappedField> fieldsByNames;

    /** The id field for the class. */
    private MappedField idField;

    /** The set of indexes to create on the collection for the class. */
    private final List<IndexState> indexes;

    /** The mapped fields from the class other than the id and version fields. */
    private final List<EntityInterceptor> methods;

    /** The list of listeners for entity persistence events. */
    private final List<EntityInterceptor> persistenceListeners;

    /** Controls if non-primary queries are OK. */
    private boolean queryNonPrimaryOk;

    /** The default read preference for the class. */
    private ReadPreference readPreference;

    /** Marker for if automatic field saving should be performed. */
    private boolean saved;

    /** The version field for the class. */
    private MappedField versionField;

    /**
     * Creates a new MappedClass.
     * 
     * @param clazz
     *            The wrapped class.
     */
    public MappedClass(final Class<?> clazz) {
        this.clazz = clazz;

        final String name = clazz.getSimpleName();
        if (!name.isEmpty()) {
            this.collectionName = name.substring(0, 1).toLowerCase(
                    Locale.ENGLISH)
                    + name.substring(1);
        }
        else {
            this.collectionName = name;
        }

        this.additionalFieldConverters = new ArrayList<FieldConverter<?>>();
        this.cappedAt = null;
        this.classnameStored = false;
        this.derivedClasses = new HashSet<Class<?>>();
        this.derivedMapping = new HashMap<String, Class<?>>();
        this.durability = Durability.ACK;
        this.fields = new ArrayList<MappedField>();
        this.fieldsByNames = new HashMap<String, MappedField>();
        this.idField = null;
        this.indexes = new ArrayList<IndexState>();
        this.methods = new ArrayList<EntityInterceptor>();
        this.persistenceListeners = new ArrayList<EntityInterceptor>();
        this.queryNonPrimaryOk = false;
        this.readPreference = ReadPreference.primary();
        this.saved = false;
        this.versionField = null;

    }

    /**
     * Determines the class for the document being loaded.
     * 
     * @param document
     *            The document being loaded.
     * @return The class for the document.
     */
    public Class<?> determineClassFor(final Document document) {
        Class<?> result = null;
        for (final Element element : document) {
            // The class name element is "definitive".
            if (CLASS_NAME_FIELD.equals(element.getName())) {
                try {
                    return Class.forName(element.getValueAsString());
                }
                catch (final ClassNotFoundException e) {
                    throw new MappingException("Could not load class '"
                            + element.getValueAsString() + "' for document.", e);
                }
            }

            final Class<?> presumptive = derivedMapping.get(element.getName());
            if (presumptive != null) {
                result = presumptive;
            }
        }

        if (result == null) {
            // Assume this class.
            result = clazz;
        }

        return result;
    }

    /**
     * Finds the {@link MappedField} by the name of the field.
     * 
     * @param name
     *            The name of the field.
     * @return The mapped field for the name, if any.
     */
    public MappedField findField(final String name) {
        return fieldsByNames.get(name);
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
     * Returns the collectionName value.
     * 
     * @return The collectionName value.
     */
    public String getCollectionName() {
        return collectionName;
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
    public Collection<MappedField> getFields() {
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
    public List<IndexState> getIndexStates() {
        return Collections.unmodifiableList(indexes);
    }

    /**
     * Returns the mapped class value.
     * 
     * @return The mapped class value.
     */
    public Class<?> getMappedClass() {
        return clazz;
    }

    /**
     * Returns the methods value.
     * 
     * @return The methods value.
     */
    public Collection<EntityInterceptor> getMethods() {
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
     * Returns the readPreference value.
     * 
     * @return The readPreference value.
     */
    public ReadPreference getReadPreference() {
        return readPreference;
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
     * Returns true if the class contains the specified annotation.
     * 
     * @param annotationClass
     *            The class for the annotation.
     * @return True if the class contains the annotation.
     */
    public boolean hasAnnotation(
            final Class<? extends Annotation> annotationClass) {
        return (clazz.getAnnotation(annotationClass) != null);
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
     * Sets the value of collectionName to the new value.
     * 
     * @param collectionName
     *            The new value for the collectionName.
     */
    public void setCollectionName(final String collectionName) {
        this.collectionName = collectionName;
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
    public void setFields(final Collection<MappedField> fields) {
        this.fields.clear();
        this.fieldsByNames.clear();
        if (fields != null) {
            this.fields.addAll(fields);

            // Two scans.
            for (final MappedField field : fields) {
                // First also names.
                for (final String name : field.getAlsoLoadNames()) {
                    fieldsByNames.put(name, field);
                }
            }

            for (final MappedField field : fields) {
                // Now the canonical name.
                fieldsByNames.put(field.getMappedFieldName(), field);
            }
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
    public void setIndexStates(final List<IndexState> indexes) {
        this.indexes.clear();
        if (indexes != null) {
            this.indexes.addAll(indexes);
        }
    }

    /**
     * Sets the value of methods to the new value.
     * 
     * @param methods
     *            The new value for the methods.
     */
    public void setMethods(final Collection<EntityInterceptor> methods) {
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
     * Sets the value of readPreference to the new value.
     * 
     * @param readPreference
     *            The new value for the readPreference.
     */
    public void setReadPreference(final ReadPreference readPreference) {
        this.readPreference = readPreference;
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

    /**
     * Adds the derived class to the derived class mapping used to determine the
     * right class to use when deserializing a document based on the fields in
     * the document alone.
     * 
     * @param derived
     *            The deerived class to add.
     * @param derivedMapped
     *            The mapping information for the derived class.
     * @param cache
     *            The cache of other derived class information.
     */
    /* package */synchronized void addDerivedClass(final Class<?> derived,
            final MappedClass derivedMapped, final MappedClassCache cache) {
        if (derivedClasses.add(derived)) {
            // Need to remap everything as order matters here.
            //
            // Need to ensure that in a hierarchy A <-- B <-- C (where B is-a A
            // and A is this mapped class) that the fields for B are added first
            // and then C. If there is a collision that field is removed from
            // the map as it cannot be used to disambiguate the hierarchy.
            final Map<String, Class<?>> newMapping = new HashMap<String, Class<?>>();

            // Build up the complete list of names add all of this
            // mapped class's fields to make sure they are not used
            // to identify a derived class.
            final Set<String> fieldNames = new HashSet<String>();
            for (final MappedField field : fields) {
                fieldNames.add(field.getMappedFieldName());
            }

            final List<Class<?>> order = new ArrayList<Class<?>>();
            for (final Class<?> derivedClass : derivedClasses) {
                // For each derived class build the heirarchy to this class in
                // least derived order e.g., [B, C] from above.
                order.clear();
                Class<?> c0 = derivedClass;
                while (c0 != clazz) {
                    order.add(0, c0);
                    c0 = c0.getSuperclass();
                }

                for (final Class<?> c1 : order) {
                    MappedClass mapped;
                    if (c1 == derived) {
                        mapped = derivedMapped;
                    }
                    else {
                        mapped = cache.getMappingFor(c1);
                    }

                    for (final MappedField field : mapped.getFields()) {
                        final String name = field.getMappedFieldName();
                        if (fieldNames.add(name)) {
                            newMapping.put(name, c1);
                        }
                        else {
                            // Make sure it is not already in the mapping.
                            newMapping.remove(name);
                        }
                    }
                }
            }

            // See if the set of fields mapped and the set of derivedClasses are
            // the same. if they are not there is a class we cannot identify
            // based on the
            // fields alone.
            final Set<Class<?>> mapped = new HashSet<Class<?>>(
                    newMapping.values());
            if (mapped.size() < derivedClasses.size()) {
                // Flip on storing the class name - Have no choice.
                if (!isClassnameStored()) {
                    setClassnameStored(true);

                    final Set<Class<?>> unmappable = new HashSet<Class<?>>(
                            derivedClasses);
                    unmappable.removeAll(mapped);

                    LOG.warning("Turning on saving class names for the class '"
                            + clazz.getCanonicalName()
                            + "'.  Cannot distinguish the following class(es) by field name: "
                            + unmappable + ".");
                }
            }

            // Now swap the mapping results into place.
            derivedMapping = newMapping;
        }
    }
}
