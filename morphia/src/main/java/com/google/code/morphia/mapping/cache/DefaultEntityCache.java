package com.google.code.morphia.mapping.cache;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import com.google.code.morphia.Key;
import com.google.code.morphia.mapping.lazy.LazyFeatureDependencies;
import com.google.code.morphia.mapping.lazy.proxy.ProxyHelper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultEntityCache implements EntityCache {

    private static final Logger log = Logger.getLogger(DefaultEntityCache.class
            .getName());

    private final Map<Key, WeakReference<Object>> entityMap = new HashMap<Key, WeakReference<Object>>();
    private final Map<Key, Boolean> existenceMap = new HashMap<Key, Boolean>();
    private final Map<Key, WeakReference<Object>> proxyMap = new WeakHashMap<Key, WeakReference<Object>>();
    private final EntityCacheStatistics stats = new EntityCacheStatistics();

    @Override
    public Boolean exists(final Key<?> k) {
        if (entityMap.containsKey(k)) {
            stats.hits++;
            return true;
        }

        final Boolean b = existenceMap.get(k);
        if (b == null) {
            stats.misses++;
        }
        else {
            stats.hits++;
        }
        return b;
    }

    @Override
    public void flush() {
        entityMap.clear();
        existenceMap.clear();
        proxyMap.clear();
        stats.reset();
    }

    @Override
    public <T> T getEntity(final Key<T> k) {
        Object o = null;
        WeakReference<Object> ref = entityMap.get(k);
        if (ref != null) {
            o = ref.get();
        }

        if (o == null) {
            if (LazyFeatureDependencies.testDependencyFullFilled()) {
                Object proxy = null;
                ref = proxyMap.get(k);
                if (ref != null) {
                    proxy = ref.get();
                }
                if (proxy != null) {
                    ProxyHelper.isFetched(proxy);
                    stats.hits++;
                    return (T) ProxyHelper.unwrap(proxy);
                }
            }
            // System.out.println("miss entity " + k + ":" + this);
            stats.misses++;
        }
        else {
            stats.hits++;
        }
        return (T) o;
    }

    @Override
    public <T> T getProxy(final Key<T> k) {
        Object o = null;
        final WeakReference<Object> ref = proxyMap.get(k);
        if (ref != null) {
            o = ref.get();
        }

        if (o == null) {
            // System.out.println("miss proxy " + k);
            stats.misses++;
        }
        else {
            stats.hits++;
        }
        return (T) o;
    }

    @Override
    public void notifyExists(final Key<?> k, final boolean exists) {
        existenceMap.put(k, exists);
        stats.entities++;
    }

    @Override
    public <T> void putEntity(final Key<T> k, final T t) {
        notifyExists(k, true); // already registers a write
        entityMap.put(k, new WeakReference<Object>(t));
    }

    @Override
    public <T> void putProxy(final Key<T> k, final T t) {
        proxyMap.put(k, new WeakReference<Object>(t));
        stats.entities++;

    }

    @Override
    public EntityCacheStatistics stats() {
        return stats.copy();
    }

}
