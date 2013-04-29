/*
 *         Copyright 2010-2013  Jeff Schnitzer, Scott Hernandez 
 *                    and Allanbank Consulting, Inc.
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
package com.google.code.morphia;

import java.io.Serializable;

import com.allanbank.mongodb.bson.DocumentReference;
import com.allanbank.mongodb.bson.Element;

/**
 * The key object; this class is take from the app-engine datastore (mostly)
 * implementation. It is also Serializable and GWT-safe, enabling your entity
 * objects to be used for GWT RPC should you so desire.
 * <p>
 * You may use normal {@link DocumentReference} objects as relationships in your
 * entities if you desire neither type safety nor GWTability.
 * </p>
 * 
 * @param <T>
 *            The type of entity refered to by the key.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org> (from Objectify codebase)
 * @author Scott Hernandez (adapted to morphia/mongodb)
 * @copyright 2010-2013, Jeff Schnitzer, Scott Hernandez and Allanbank
 *            Consulting, Inc., All Rights Reserved
 */
public class Key<T> implements Serializable, Comparable<Key<?>> {
    /** Serialization version of the class. */
    private static final long serialVersionUID = -5699542734467435513L;

    /**
     * Compares two object watching for nulls.
     * 
     * @param o1
     *            The first Comparable.
     * @param o2
     *            The second Comparable.
     * @return The relative ordering of {@code o1} and {@code o2}.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> int compareNullable(final Comparable o1,
            final Comparable o2) {
        if (o1 == null) {
            if (o2 == null) {
                return 0;
            }
            return -1;
        }
        else if (o2 == null) {
            return 1;
        }
        else {
            return o1.compareTo(o2);
        }
    }

    /** Id value */
    protected Element id;

    /**
     * The name of the class which represents the kind. As much as we'd like to
     * use the normal String kind value here, translating back to a Class for
     * getKind() would then require a link to the OFactory, making this object
     * non-serializable.
     */
    protected String kind;

    /** The class which represents the kind. */
    protected Class<? extends T> kindClass;

    /**
     * Create a key with an id.
     * 
     * @param kind
     *            The class which represents the kind.
     * @param idElement
     *            The {@code _id} element.
     */
    public Key(final Class<? extends T> kind, final Element idElement) {
        this.kindClass = kind;
        this.id = idElement;
    }

    /**
     * Create a key with an id.
     * 
     * @param kind
     *            The name of the class which represents the kind.
     * @param idElement
     *            The {@code _id} element.
     */
    public Key(final String kind, final Element idElement) {
        this.kind = kind.intern();
        this.id = idElement;
    }

    /** For GWT serialization */
    protected Key() {
    }

    /**
     * <p>
     * Compares based on the following traits, in order:
     * </p>
     * <ol>
     * <li>kind/kindClass</li>
     * <li>parent</li>
     * <li>id or name</li>
     * </ol>
     */
    @Override
    public int compareTo(final Key<?> other) {
        checkState();
        other.checkState();

        int cmp = 0;
        // First kind
        if ((other.kindClass != null) && (kindClass != null)) {
            cmp = this.kindClass.getName().compareTo(other.kindClass.getName());
            if (cmp != 0) {
                return cmp;
            }
        }
        cmp = compareNullable(this.kind, other.kind);
        if (cmp != 0) {
            return cmp;
        }

        try {
            cmp = compareNullable(this.id.getValueAsString(),
                    other.id.getValueAsString());
            if (cmp != 0) {
                return cmp;
            }
        }
        catch (final Exception e) {
            // Not a comparable, use equals and String.compareTo().
            cmp = this.id.equals(other.id) ? 0 : 1;
            if (cmp != 0) {
                return this.id.toString().compareTo(other.id.toString());
            }
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Key<?>)) {
            return false;
        }

        return this.compareTo((Key<?>) obj) == 0;
    }

    /**
     * Returns the Element identifying the entity.
     * 
     * @return the id associated with this key.
     */
    public Element getId() {
        return this.id;
    }

    /**
     * Returns the name of the Class for the entity.
     * 
     * @return the collection-name.
     */
    public String getKind() {
        return this.kind;
    }

    /**
     * Returns the {@link Class} for the entity.
     * 
     * @return The {@link Class} for the entity.
     */
    public Class<? extends T> getKindClass() {
        return this.kindClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /**
     * Sets the name of the class for the entity.
     * 
     * @param newKind
     *            The name of the class for the entity.
     */
    public void setKind(final String newKind) {
        kind = newKind.intern();
    }

    /**
     * Sets the class for the entity.
     * 
     * @param clazz
     *            The {@link Class} for the entity.
     */
    public void setKindClass(final Class<? extends T> clazz) {
        this.kindClass = clazz;
    }

    /**
     * Creates a human-readable version of this key
     */
    @Override
    public String toString() {
        final StringBuilder bld = new StringBuilder("Key{");

        if (kind != null) {
            bld.append("kind=");
            bld.append(this.kind);
        }
        else {
            bld.append("kindClass=");
            bld.append(this.kindClass.getName());
        }
        bld.append(", id=");
        bld.append(this.id);
        bld.append("}");

        return bld.toString();
    }

    /**
     * Validates that the key has the minimum required set of information.
     */
    private void checkState() {
        if ((kindClass == null) && (kind == null)) {
            throw new IllegalStateException(
                    "Kind must be specified (or a class).");
        }
        if (id == null) {
            throw new IllegalStateException("id must be specified");
        }
    }
}