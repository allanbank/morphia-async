package com.google.code.morphia.mapping.cache;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.google.code.morphia.Key;
import com.google.code.morphia.logging.Logr;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.mapping.lazy.LazyFeatureDependencies;
import com.google.code.morphia.mapping.lazy.proxy.ProxyHelper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultEntityCache implements EntityCache {

    private static final Logr log = MorphiaLoggerFactory
            .get(DefaultEntityCache.class);

    private final Map<Key, WeakReference<Object>> entityMap = new HashMap<Key, WeakReference<Object>>();
    private final Map<Key, WeakReference<Object>> proxyMap = new WeakHashMap<Key, WeakReference<Object>>();
    private final Map<Key, Boolean> existenceMap = new HashMap<Key, Boolean>();
    private final EntityCacheStatistics stats = new EntityCacheStatistics();

    public Boolean exists(Key<?> k) {
        if (entityMap.containsKey(k)) {
            stats.hits++;
            return true;
        }

        Boolean b = existenceMap.get(k);
        if (b == null) {
            stats.misses++;
        }
        else {
            stats.hits++;
        }
        return b;
    }

    public void notifyExists(Key<?> k, boolean exists) {
        existenceMap.put(k, exists);
        stats.entities++;
    }

    public <T> T getEntity(Key<T> k) {
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

    public <T> T getProxy(Key<T> k) {
        Object o = null;
        WeakReference<Object> ref = proxyMap.get(k);
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

    public <T> void putProxy(Key<T> k, T t) {
        proxyMap.put(k, new WeakReference<Object>(t));
        stats.entities++;

    }

    public <T> void putEntity(Key<T> k, T t) {
        notifyExists(k, true); // already registers a write
        entityMap.put(k, new WeakReference<Object>(t));
    }

    public void flush() {
        entityMap.clear();
        existenceMap.clear();
        proxyMap.clear();
        stats.reset();
    }

    public EntityCacheStatistics stats() {
        return stats.copy();
    }

}
