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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.allanbank.mongodb.Durability;
import com.allanbank.mongodb.ReadPreference;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.google.code.morphia.AbstractEntityInterceptor;
import com.google.code.morphia.EntityInterceptor;
import com.google.code.morphia.annotations.AlsoLoad;
import com.google.code.morphia.annotations.ConstructorArgs;
import com.google.code.morphia.annotations.Converters;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.EntityListeners;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Indexes;
import com.google.code.morphia.annotations.NotSaved;
import com.google.code.morphia.annotations.Polymorphic;
import com.google.code.morphia.annotations.PostLoad;
import com.google.code.morphia.annotations.PostPersist;
import com.google.code.morphia.annotations.PreLoad;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.annotations.PreSave;
import com.google.code.morphia.annotations.Property;
import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.annotations.Serialized;
import com.google.code.morphia.annotations.Transient;
import com.google.code.morphia.annotations.Version;
import com.google.code.morphia.converters.Converter;
import com.google.code.morphia.mapping.MappingException;
import com.google.code.morphia.state.MappedField.Strategy;

/**
 * Scanner provides the logic for constructing a {@link MappedClass} (and
 * associated {@link MappedField} and {@link MappedMethod} objects from a
 * {@link Class} via reflection.
 * 
 * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
 */
public class Scanner {

    /** The set of class annotations. */
    public static final List<Class<? extends Annotation>> CLASS_ANNOTATIONS;

    /** The default name in many of the annotations. */
    public static final String DEFAULT_FIELD_NAME = ".";

    /** The set of field annotations. */
    public static final List<Class<? extends Annotation>> FIELD_ANNOTATIONS;

    /** The set of method annotations. */
    public static final List<Class<? extends Annotation>> METHOD_ANNOTATIONS;

    /** The root class to stop searching for fields and methods. */
    public static final Class<Object> ROOT = Object.class;

    static {
        List<Class<? extends Annotation>> annotations;

        annotations = new ArrayList<Class<? extends Annotation>>(8);
        annotations.add(Converters.class);
        annotations.add(Embedded.class);
        annotations.add(Entity.class);
        annotations.add(EntityListeners.class);
        annotations.add(Index.class);
        annotations.add(Indexes.class);
        annotations.add(NotSaved.class);
        annotations.add(Polymorphic.class);
        CLASS_ANNOTATIONS = Collections.unmodifiableList(annotations);

        annotations = new ArrayList<Class<? extends Annotation>>(11);
        annotations.add(AlsoLoad.class);
        annotations.add(ConstructorArgs.class);
        annotations.add(Embedded.class);
        annotations.add(Id.class);
        annotations.add(Indexed.class);
        annotations.add(NotSaved.class);
        annotations.add(Property.class);
        annotations.add(Reference.class);
        annotations.add(Serialized.class);
        annotations.add(Transient.class);
        annotations.add(Version.class);
        FIELD_ANNOTATIONS = Collections.unmodifiableList(annotations);

        annotations = new ArrayList<Class<? extends Annotation>>(5);
        annotations.add(PostLoad.class);
        annotations.add(PostPersist.class);
        annotations.add(PreLoad.class);
        annotations.add(PrePersist.class);
        annotations.add(PreSave.class);
        METHOD_ANNOTATIONS = Collections.unmodifiableList(annotations);
    }

    /**
     * Creates a new Scanner.
     */
    public Scanner() {
        super();
    }

    /**
     * Scans the class for the Morphia annotations and returns a
     * {@link MappedClass} contain the details of the class.
     * 
     * @param clazz
     *            The {@link Class} to build a representation of.
     * @return The {@link MappedClass} representing the class.
     */
    public MappedClass scan(final Class<?> clazz) {
        clazz.getInterfaces();
        clazz.getSuperclass();
        clazz.getDeclaredFields();

        // Scan the class.
        final MappedClass classState = doScan(clazz);

        // Collect the fields.
        final Collection<Field> fields = extractFields(clazz);

        // Scan the fields.
        final Collection<MappedField> mappedFields = scan(classState, fields);
        classState.setFields(mappedFields);

        // Collect the methods.
        final Collection<Method> methods = extractMethods(clazz);

        // Scan the methods.
        final Collection<EntityInterceptor> methodCallbacks = scan(methods);
        classState.setMethods(methodCallbacks);

        return classState;
    }

    /**
     * Extracts all of the fields from the class and base classes.
     * 
     * @param clazz
     *            The class to extract the fields from.
     * @return The list of fields from the class.
     */
    /* package */Collection<Field> extractFields(final Class<?> clazz) {
        final Map<String, Field> fields = new TreeMap<String, Field>();
        Class<?> c = clazz;
        while (c != ROOT) {
            for (final Field f : c.getDeclaredFields()) {
                final int fModifiers = f.getModifiers();
                if (!fields.containsKey(f.getName())
                        && !Modifier.isFinal(fModifiers)
                        && !Modifier.isStatic(fModifiers)) {
                    fields.put(f.getName(), f);
                }
            }
            c = c.getSuperclass();
        }
        return fields.values();
    }

    /**
     * Performs the scan of the class's annotations.
     * 
     * @param clazz
     *            The scanned class.
     * @return The details on the scanned class.
     */
    @SuppressWarnings("deprecation")
    private MappedClass doScan(final Class<?> clazz) {
        final MappedClass mapped = new MappedClass(clazz);

        final Converters converters = clazz.getAnnotation(Converters.class);
        if (converters != null) {
            // TODO - Add the concept of entity private converters.
            mapped.setAdditionalFieldConverters(instantiate(converters.value()));
        }

        final Entity entity = clazz.getAnnotation(Entity.class);
        if (entity != null) {
            if (entity.cap() != null) {
                mapped.setCappedAt(entity.cap());
            }

            if (!isEmpty(entity.concern())) {
                mapped.setDurability(Durability.valueOf(entity.concern()));
            }

            if (!isEmpty(entity.value())) {
                mapped.setCollectionName(entity.value());
            }
            if (entity.queryNonPrimary()) {
                mapped.setReadPreference(ReadPreference.secondary());
            }
            else {
                mapped.setReadPreference(ReadPreference.primary());
            }

            mapped.setClassnameStored(!entity.noClassnameStored());
        }

        final EntityListeners listeners = clazz
                .getAnnotation(EntityListeners.class);
        if (listeners != null) {
            // Listeners can take two form. Those with methods marked with the
            // listener annotations and those implementing the EntityListener
            // interface.
            final List<Object> listenersObjs = instantiate(listeners.value());
            final List<EntityInterceptor> interceptors = new ArrayList<EntityInterceptor>(
                    listenersObjs.size());
            for (final Object listenersObj : listenersObjs) {
                if (listenersObj instanceof EntityInterceptor) {
                    interceptors.add((EntityInterceptor) listenersObj);
                }
                else {
                    final Collection<Method> methods = extractMethods(listenersObj
                            .getClass());
                    for (final Method m : methods) {
                        interceptors.addAll(scan(m, listenersObj));
                    }
                }
            }
        }

        // Collect all of the indexes.
        final List<Index> indexes = new ArrayList<Index>();
        final Indexes indexesAnnotation = clazz.getAnnotation(Indexes.class);
        if (indexesAnnotation != null) {
            indexes.addAll(Arrays.asList(indexesAnnotation.value()));
        }
        final Index indexAnnotation = clazz.getAnnotation(Index.class);
        if (indexAnnotation != null) {
            indexes.add(indexAnnotation);
        }

        final List<IndexState> indexStates = new ArrayList<IndexState>();
        for (final Index index : indexes) {
            final DocumentBuilder key = BuilderFactory.start();
            final StringTokenizer tokenizer = new StringTokenizer(
                    index.value(), " \t\n\r\f,;:");
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                if (token.startsWith("-")) {
                    key.add(token.substring(1).trim(), -1);
                }
                else {
                    key.add(token, 1);
                }
            }

            final DocumentBuilder options = BuilderFactory.start();

            options.add("background", index.background());
            options.add("sparse", index.sparse());
            if (index.unique()) {
                options.add("unique", true);
                options.add("dropDups", index.dropDups());
            }

            indexStates.add(new IndexState(index.name(), key, options));
        }
        mapped.setIndexStates(indexStates);

        final Polymorphic polymorphic = clazz.getAnnotation(Polymorphic.class);
        if (polymorphic != null) {
            mapped.setClassnameStored(true);
        }

        // TODO: Figure out if we need to re-evaluate Embedded annotation for
        // the class. Already handled in the field lookup.
        // Embedded embedded = clazz.getAnnotation(Embedded.class);

        // TODO: Figure out if we need to re-evaluate NotSaved annotation for
        // the class. Already handled in the field lookup.
        // NotSaved notSaved = clazz.getAnnotation(NotSaved.class);

        return mapped;
    }

    /**
     * Extracts all of the methods from the class and base classes (except
     * Object).
     * 
     * @param clazz
     *            The class to extract the fields from.
     * @return The list of fields from the class.
     */
    private Collection<Method> extractMethods(final Class<?> clazz) {
        final Map<String, Method> methods = new TreeMap<String, Method>();
        Class<?> c = clazz;
        while (c != ROOT) {
            for (final Method m : c.getDeclaredMethods()) {
                final String methodToken = toToken(m);

                if (!methods.containsKey(methodToken)) {
                    methods.put(methodToken, m);
                }
            }
            c = c.getSuperclass();
        }
        return methods.values();
    }

    /**
     * Creates an instance of the {@link Class} converting any exception into a
     * {@link MappingException}.
     * 
     * @param clazz
     *            The class to instantiate and object from.
     * @return The instantiated object.
     */
    private <T> T instantiate(final Class<? extends T> clazz) {
        try {
            return clazz.newInstance();
        }
        catch (final InstantiationException e) {
            throw new MappingException(
                    "Failure instatiating an instance of the '"
                            + clazz.getName() + "' class: " + e.getMessage(), e);

        }
        catch (final IllegalAccessException e) {
            throw new MappingException(
                    "Failure instatiating an instance of the '"
                            + clazz.getName() + "' class: " + e.getMessage(), e);
        }
    }

    /**
     * Creates an instance of the {@link Class Classes} converting any exception
     * into a {@link MappingException}.
     * 
     * @param classes
     *            The classes to instantiate and object from.
     * @return The instantiated objects.
     */
    private <T> List<T> instantiate(final Class<? extends T>[] classes) {
        final List<T> objects = new ArrayList<T>(classes.length);
        for (final Class<? extends T> clazz : classes) {
            objects.add(instantiate(clazz));
        }
        return objects;
    }

    /**
     * Returns true if the value is <code>null</code> or empty.
     * 
     * @param value
     *            The value to test.
     * @return True if the value is <code>null</code> or empty.
     */
    private boolean isEmpty(final String value) {
        return ((value == null) || value.trim().isEmpty() || value.trim()
                .equals(DEFAULT_FIELD_NAME));
    }

    /**
     * Scans the class's methods and returns the {@link EntityInterceptor}s for
     * handling pre and post mapping annotation callbacks.
     * 
     * @param methods
     *            The methods to scan for annotations.
     * @return The {@link EntityInterceptor}s for handling pre and post mapping
     *         annotation callbacks.
     */
    private Collection<EntityInterceptor> scan(final Collection<Method> methods) {
        final List<EntityInterceptor> interceptors = new ArrayList<EntityInterceptor>();
        for (final Method m : methods) {
            interceptors.addAll(scan(m, null));
        }
        return interceptors;
    }

    /**
     * Scans the class's fields and returns the mapped field for the same.
     * 
     * @param clazz
     *            The class's state.
     * @param fields
     *            The fields to scan for annotations.
     * @return The {@link MappedField}s with the settings from the field's
     *         annotations.
     */
    private Collection<MappedField> scan(final MappedClass clazz,
            final Collection<Field> fields) {
        final SortedMap<String, MappedField> mapped = new TreeMap<String, MappedField>();
        for (final Field f : fields) {
            final MappedField m = scan(clazz, f);

            mapped.put(m.getMappedFieldName(), m);
        }
        return mapped.values();
    }

    /**
     * Scans the fields annotations and builds the MappedField containing the
     * complete set of settings.
     * 
     * @param clazz
     *            The class's state.
     * @param field
     *            The field to scan for annotations.
     * @return The {@link MappedField} with the settings from the field's
     *         annotations.
     */
    private MappedField scan(final MappedClass clazz, final Field field) {
        final MappedField mapped = new MappedField(field);

        final AlsoLoad alsoLoad = field.getAnnotation(AlsoLoad.class);
        if (alsoLoad != null) {
            mapped.setAlsoLoadNames(new HashSet<String>(Arrays.asList(alsoLoad
                    .value())));
        }

        final ConstructorArgs constructorArg = field
                .getAnnotation(ConstructorArgs.class);
        if (constructorArg != null) {
            mapped.setConstructorArgs(Arrays.asList(constructorArg.value()));
        }

        Embedded embedded = field.getAnnotation(Embedded.class);
        if (embedded == null) {
            // Look in the declared type for the annotation.
            embedded = field.getType().getAnnotation(Embedded.class);
        }
        if (embedded != null) {
            mapped.setStrategy(Strategy.MAP);
            if (embedded.concreteClass() != null) {
                mapped.setConcreteClass(embedded.concreteClass());
            }
            if (!isEmpty(embedded.value())) {
                mapped.setMappedFieldName(embedded.value().trim());
            }
        }

        // TODO: Property looks just like Embedded - Why two annotations?
        final Property property = field.getAnnotation(Property.class);
        if (property != null) {
            mapped.setStrategy(Strategy.MAP);
            if (property.concreteClass() != null) {
                mapped.setConcreteClass(property.concreteClass());
            }
            if (!isEmpty(property.value())) {
                mapped.setMappedFieldName(property.value().trim());
            }
        }

        final Id id = field.getAnnotation(Id.class);
        if (id != null) {
            mapped.setId(true);
            clazz.setIdField(mapped);
        }

        NotSaved notSaved = field.getAnnotation(NotSaved.class);
        if (notSaved == null) {
            // Look in the declared type for the annotation.
            notSaved = field.getType().getAnnotation(NotSaved.class);
        }
        if (notSaved != null) {
            mapped.setWritten(false);
        }

        final Reference reference = field.getAnnotation(Reference.class);
        if (reference != null) {
            if (reference.concreteClass() != null) {
                mapped.setConcreteClass(reference.concreteClass());
            }

            if (!isEmpty(reference.value())) {
                mapped.setMappedFieldName(reference.value().trim());
            }
            mapped.setIgnoreMissing(reference.ignoreMissing());
            mapped.setLazy(reference.lazy());
        }

        final Serialized serialized = field.getAnnotation(Serialized.class);
        if (serialized != null) {
            if (serialized.disableCompression()) {
                mapped.setStrategy(Strategy.SERIALIZED_UNCOMPRESSED);
            }
            else {
                mapped.setStrategy(Strategy.SERIALIZED_COMPRESSED);
            }
            if (!isEmpty(serialized.value())) {
                mapped.setMappedFieldName(serialized.value().trim());
            }
        }

        final Transient transientAnnotation = field
                .getAnnotation(Transient.class);
        if (transientAnnotation != null) {
            mapped.setStrategy(Strategy.NONE);
            mapped.setWritten(false);
        }
        else if ((field.getModifiers() & Modifier.TRANSIENT) == Modifier.TRANSIENT) {
            mapped.setStrategy(Strategy.NONE);
            mapped.setWritten(false);
        }

        final Version version = field.getAnnotation(Version.class);
        if (version != null) {
            mapped.setVersion(true);
            if (!isEmpty(version.value())) {
                mapped.setMappedFieldName(version.value().trim());
            }
            clazz.setVersionField(mapped);
        }

        // Do this last so the field name is fully resolved.
        final Indexed indexed = field.getAnnotation(Indexed.class);
        if (indexed != null) {
            final DocumentBuilder key = BuilderFactory.start();
            if (indexed.value() != null) {
                key.add(mapped.getMappedFieldName(), indexed.value()
                        .toIndexValue());
            }
            else {
                key.add(mapped.getMappedFieldName(), 1);
            }

            final DocumentBuilder options = BuilderFactory.start();

            options.add("background", indexed.background());
            options.add("sparse", indexed.sparse());
            if (indexed.unique()) {
                options.add("unique", true);
                options.add("dropDups", indexed.dropDups());
            }

            mapped.setIndex(new IndexState(indexed.name(), key, options));
        }

        return mapped;
    }

    /**
     * Scans the method for callback annotations and returns the list of
     * interceptors to call the method based on those annotations.
     * 
     * @param m
     *            The method to scan for annotations.
     * @param instance
     *            The instance to invoke the method on.
     * @return The list of interceptors to call the method based on the
     *         annotations.
     */
    private Collection<? extends EntityInterceptor> scan(final Method m,
            final Object instance) {
        final List<EntityInterceptor> interceptors = new ArrayList<EntityInterceptor>(
                1);
        final PostLoad postLoad = m.getAnnotation(PostLoad.class);
        if (postLoad != null) {
            interceptors.add(new PostLoadInterceptor(m, instance));
        }

        final PostPersist postPersist = m.getAnnotation(PostPersist.class);
        if (postPersist != null) {
            interceptors.add(new PostPersistInterceptor(m, instance));
        }

        final PreLoad preLoad = m.getAnnotation(PreLoad.class);
        if (preLoad != null) {
            interceptors.add(new PreLoadInterceptor(m, instance));
        }

        final PrePersist prePersist = m.getAnnotation(PrePersist.class);
        if (prePersist != null) {
            interceptors.add(new PrePersistInterceptor(m, instance));
        }

        final PreSave preSave = m.getAnnotation(PreSave.class);
        if (preSave != null) {
            interceptors.add(new PreSaveInterceptor(m, instance));
        }

        return interceptors;
    }

    /**
     * Creates a token (composed of the method name and parameter types) to
     * represent the method.
     * 
     * @param method
     *            The method to create a token for.
     * @return The token for the method.
     */
    private String toToken(final Method method) {
        final StringBuilder b = new StringBuilder();
        b.append(method.getName());
        for (final Class<?> paramType : method.getParameterTypes()) {
            b.append(paramType.getCanonicalName());
        }
        return b.toString();
    }

    /**
     * AbstractEntityInterceptorCallback provides a helper base class for the
     * entity callbacks.
     * 
     * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
     */
    static class AbstractEntityInterceptorCallback extends
            AbstractEntityInterceptor {
        /** The class for the {@link DocumentBuilder} interface. */
        public static final Class<DocumentBuilder> DOCUMENT_BUILDER_CLASS = DocumentBuilder.class;

        /** An empty argument array. */
        public static final Object[] EMPTY_ARGS = new Object[0];

        /** The method to invoke on the event. */
        private final Method callback;

        /**
         * If true then the interceptor method is expecting to be passed the
         * DocumentBuilder.
         */
        private final boolean passDocumentBuilder;

        /**
         * If true then the interceptor method is expecting to be passed the
         * entity.
         */
        private final boolean passEntity;

        /** The object to invoke the callback on for the event. */
        private final Object target;

        /**
         * Creates a new PreSaveInterceptor.
         * 
         * @param callback
         *            The callback method.
         * @param target
         *            The target for the callback invocations.
         */
        public AbstractEntityInterceptorCallback(final Method callback,
                final Object target) {
            this.callback = callback;
            this.target = target;
            this.callback.setAccessible(true);

            boolean passDoc = false;
            boolean passEnt = false;
            final Class<?>[] parameterType = this.callback.getParameterTypes();
            if (parameterType.length == 1) {
                if (DOCUMENT_BUILDER_CLASS.isAssignableFrom(parameterType[0])) {
                    passDoc = true;
                }
                else {
                    passEnt = true;
                }
            }
            passDocumentBuilder = passDoc;
            passEntity = passEnt;
        }

        /**
         * Invokes the callback method.
         * 
         * @param entity
         *            The mapped entity.
         * @param documentBuilder
         *            The document builder for the constructed entity.
         */
        protected void callback(final Object entity,
                final DocumentBuilder documentBuilder) {
            try {
                if (target != null) {
                    callback.invoke(target,
                            determineArgs(entity, documentBuilder));
                }
                else {
                    callback.invoke(entity, EMPTY_ARGS);
                }
            }
            catch (final IllegalArgumentException e) {
                throw new MappingException("Failure calling the '"
                        + callback.getName() + "' method of the '"
                        + entity.getClass().getSimpleName() + "' class: "
                        + e.getMessage(), e);
            }
            catch (final IllegalAccessException e) {
                throw new MappingException("Failure calling the '"
                        + callback.getName() + "' method of the '"
                        + entity.getClass().getSimpleName() + "' class: "
                        + e.getMessage(), e);
            }
            catch (final InvocationTargetException e) {
                throw new MappingException("Failure calling the '"
                        + callback.getName() + "' method of the '"
                        + entity.getClass().getSimpleName() + "' class: "
                        + e.getMessage(), e);
            }
        }

        /**
         * Determines the arguments to pass to the method, if any.
         * 
         * @param entity
         *            The entity being mapped.
         * @param documentBuilder
         *            The document builder for the mapped entity.
         * @return The arguments to pass to the entity.
         */
        private Object[] determineArgs(final Object entity,
                final DocumentBuilder documentBuilder) {
            if (passDocumentBuilder) {
                return new Object[] { documentBuilder };
            }
            else if (passEntity) {
                return new Object[] { documentBuilder };
            }
            return EMPTY_ARGS;
        }
    }

    /**
     * PostLoadInterceptor provides provides a callback for post-load events.
     * 
     * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
     */
    static class PostLoadInterceptor extends AbstractEntityInterceptorCallback {
        /**
         * Creates a new PostLoadInterceptor.
         * 
         * @param callback
         *            The callback method.
         * @param target
         *            The target for the callback invocations.
         */
        public PostLoadInterceptor(final Method callback, final Object target) {
            super(callback, target);
        }

        /**
         * {@inheritDoc}
         * <p>
         * Overriden to invoke the callback method.
         * </p>
         */
        @Override
        public void postLoad(final Object ent, final DocumentBuilder dbObj,
                final Converter converter) {
            callback(ent, dbObj);
        }
    }

    /**
     * PostPersistInterceptor provides provides a callback for post-persist
     * events.
     * 
     * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
     */
    static class PostPersistInterceptor extends
            AbstractEntityInterceptorCallback {
        /**
         * Creates a new PostPersistInterceptor.
         * 
         * @param callback
         *            The callback method.
         * @param target
         *            The target for the callback invocations.
         */
        public PostPersistInterceptor(final Method callback, final Object target) {
            super(callback, target);
        }

        /**
         * {@inheritDoc}
         * <p>
         * Overriden to invoke the callback method.
         * </p>
         */
        @Override
        public void postPersist(final Object ent, final DocumentBuilder dbObj,
                final Converter converter) {
            callback(ent, dbObj);
        }
    }

    /**
     * PreLoadInterceptor provides provides a callback for pre-load events.
     * 
     * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
     */
    static class PreLoadInterceptor extends AbstractEntityInterceptorCallback {
        /**
         * Creates a new PreLoadInterceptor.
         * 
         * @param callback
         *            The callback method.
         * @param target
         *            The target for the callback invocations.
         */
        public PreLoadInterceptor(final Method callback, final Object target) {
            super(callback, target);
        }

        /**
         * {@inheritDoc}
         * <p>
         * Overriden to invoke the callback method.
         * </p>
         */
        @Override
        public void preLoad(final Object ent, final DocumentBuilder dbObj,
                final Converter converter) {
            callback(ent, dbObj);
        }
    }

    /**
     * PrePersistInterceptor provides provides a callback for pre-persist
     * events.
     * 
     * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
     */
    static class PrePersistInterceptor extends
            AbstractEntityInterceptorCallback {
        /**
         * Creates a new PrePersistInterceptor.
         * 
         * @param callback
         *            The callback method.
         * @param target
         *            The target for the callback invocations.
         */
        public PrePersistInterceptor(final Method callback, final Object target) {
            super(callback, target);
        }

        /**
         * {@inheritDoc}
         * <p>
         * Overriden to invoke the callback method.
         * </p>
         */
        @Override
        public void prePersist(final Object ent, final DocumentBuilder dbObj,
                final Converter converter) {
            callback(ent, dbObj);
        }
    }

    /**
     * PreSaveInterceptor provides provides a callback for pre-save events.
     * 
     * @copyright 2013, Allanbank Consulting, Inc., All Rights Reserved
     */
    static class PreSaveInterceptor extends AbstractEntityInterceptorCallback {
        /**
         * Creates a new PreSaveInterceptor.
         * 
         * @param callback
         *            The callback method.
         * @param target
         *            The target for the callback invocations.
         */
        public PreSaveInterceptor(final Method callback, final Object target) {
            super(callback, target);
        }

        /**
         * {@inheritDoc}
         * <p>
         * Overriden to invoke the callback method.
         * </p>
         */
        @Override
        public void preSave(final Object ent, final DocumentBuilder dbObj,
                final Converter converter) {
            callback(ent, dbObj);
        }
    }
}
