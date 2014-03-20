/*
 * Copyright (c) 2008-2014 Maxifier Ltd. All Rights Reserved.
 */
package com.maxifier.mxcache.impl.caches.def;

import com.maxifier.mxcache.storage.ObjectObjectStorage;
import gnu.trove.THashMap;
import gnu.trove.TObjectHashingStrategy;

/**
 * ObjectObjectTroveStorage
 *
 * @author Andrey Yakoushin (andrey.yakoushin@maxifier.com)
 * @author Alexander Kochurov (alexander.kochurov@maxifier.com)
 */
public class ObjectObjectTroveStorage<K, V> extends THashMap<K, Object> implements ObjectObjectStorage<K, V> {
    public ObjectObjectTroveStorage() {
    }

    public ObjectObjectTroveStorage(TObjectHashingStrategy<K> strategy) {
        super(strategy);        
    }

    @Override
    public Object load(K key) {
        Object v = get(key);
        if (v == null) {
            return UNDEFINED;
        }
        if (v == TroveHelper.NULL_REPLACEMENT) {
            return null;
        }
        return v;
    }

    @Override
    public void save(K key, V value) {
        put(key, value == null ? TroveHelper.NULL_REPLACEMENT : value);
    }
}