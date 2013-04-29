/**
 * 
 */
package com.google.code.morphia.mapping.lazy.proxy;

import java.io.IOException;
import java.io.Serializable;

import com.google.code.morphia.Key;
import com.google.code.morphia.mapping.lazy.DatastoreProvider;
import com.thoughtworks.proxy.kit.ObjectReference;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractReference implements Serializable,
        ObjectReference, ProxiedReference {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    protected final boolean ignoreMissing;
    protected Object object;
    protected final DatastoreProvider p;
    protected final Class referenceObjClass;
    private boolean isFetched = false;

    protected AbstractReference(final DatastoreProvider p,
            final Class referenceObjClass, final boolean ignoreMissing) {
        this.p = p;
        this.referenceObjClass = referenceObjClass;
        this.ignoreMissing = ignoreMissing;
    }

    @Override
    public final Class __getReferenceObjClass() {
        return referenceObjClass;
    }

    @Override
    public final boolean __isFetched() {
        return isFetched;
    }

    @Override
    public Object __unwrap() {
        return get();
    }

    @Override
    public final synchronized Object get() {
        if (isFetched) {
            return object;
        }

        object = fetch();
        isFetched = true;
        return object;
    }

    @Override
    public final void set(final Object arg0) {
        throw new UnsupportedOperationException();
    }

    protected void beforeWriteObject() {
    }

    protected abstract Object fetch();

    protected final Object fetch(final Key<?> id) {
        return p.get().getByKey(referenceObjClass, id);
    }

    private void writeObject(final java.io.ObjectOutputStream out)
            throws IOException {
        // excessive hoop-jumping in order not to have to recreate the
        // instance.
        // as soon as weÂ´d have an ObjectFactory, that would be unnecessary
        beforeWriteObject();
        isFetched = false;
        out.defaultWriteObject();
    }
}
