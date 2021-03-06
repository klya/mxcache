/*
 * Copyright (c) 2008-2014 Maxifier Ltd. All Rights Reserved.
 */
package com.maxifier.mxcache.clean;

import com.maxifier.mxcache.caches.Cache;
import com.maxifier.mxcache.caches.CleaningNode;

import java.util.List;

/**
 * Cleanable - this class usually should not be implemented manually.
 * <p>It is generated by mxCache instrumentator for each class that contains @Cached methods.</p>
 * <p>Cleanable is used to obtain instances of caches that an instance of class holds. It also allows to obtain
 * static caches.</p>
 * <p>No matter how many cached methods are there in the class, a single Cleanable is generated for it.
 * A single instance of this class is created. It manages all the caches of class.
 * It doesn't allow to access superclass or subclass caches, consider getting corresponding Cleanable for it.</p>
 *
 * @author Andrey Yakoushin (andrey.yakoushin@maxifier.com)
 * @author Alexander Kochurov (alexander.kochurov@maxifier.com)
 */
public interface Cleanable<T> {
    /**
     * @param id id of cache
     * @return cache instance
     */
    Cache getStaticCache(int id);

    void appendInstanceCachesTo(List<CleaningNode> result, T instance);

    /**
     * @param t instance of cached class
     * @param id id of cache
     * @return cache instance
     */
    Cache getInstanceCache(T t, int id);
}
