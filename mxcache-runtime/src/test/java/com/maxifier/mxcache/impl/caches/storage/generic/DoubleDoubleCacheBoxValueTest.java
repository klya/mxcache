/*
 * Copyright (c) 2008-2014 Maxifier Ltd. All Rights Reserved.
 */
package com.maxifier.mxcache.impl.caches.storage.generic;

import com.maxifier.mxcache.caches.*;
import com.maxifier.mxcache.storage.*;
import com.maxifier.mxcache.resource.MxResource;
import com.maxifier.mxcache.impl.MutableStatisticsImpl;
import com.maxifier.mxcache.impl.resource.DependencyTracker;
import com.maxifier.mxcache.impl.resource.ResourceOccupied;
import org.testng.annotations.Test;

import com.maxifier.mxcache.provider.Signature;
import com.maxifier.mxcache.impl.wrapping.Wrapping;

import static org.mockito.Mockito.*;

/**
 * DoubleDoubleCacheBoxValueTest
 *
 * THIS IS GENERATED CLASS! DON'T EDIT IT MANUALLY!
 *
 * GENERATED FROM P2PBoxValueCacheTest.template
 *
 * @author Andrey Yakoushin (andrey.yakoushin@maxifier.com)
 * @author Alexander Kochurov (alexander.kochurov@maxifier.com)
 */
@Test
public class DoubleDoubleCacheBoxValueTest {
    private static final DoubleDoubleCalculatable CALCULATABLE = new DoubleDoubleCalculatable() {
        @Override
        public double calculate(Object owner, double o) {
            assert o == 42d;
            return 42d;
        }
    };

    public void testMiss() {
        DoubleObjectStorage storage = mock(DoubleObjectStorage.class);

        when(storage.load(42d)).thenReturn(Storage.UNDEFINED);
        when(storage.size()).thenReturn(0);

        DoubleDoubleCache cache = (DoubleDoubleCache) Wrapping.getFactory(new Signature(double.class, Object.class), new Signature(double.class, double.class), false).
                wrap("123", CALCULATABLE, storage, new MutableStatisticsImpl());
        cache.setDependencyNode(DependencyTracker.DUMMY_NODE);

        assert cache.getSize() == 0;
        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 0;

        assert cache.getOrCreate(42d) == 42d;

        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 1;

        verify(storage).size();
        verify(storage, atLeast(1)).load(42d);
        verify(storage).save(42d, 42d);
        verifyNoMoreInteractions(storage);
    }

    public void testHit() {
        DoubleObjectStorage storage = mock(DoubleObjectStorage.class);

        DoubleDoubleCache cache = (DoubleDoubleCache) Wrapping.getFactory(new Signature(double.class, Object.class), new Signature(double.class, double.class), false).
                wrap("123", CALCULATABLE, storage, new MutableStatisticsImpl());
        cache.setDependencyNode(DependencyTracker.DUMMY_NODE);

        when(storage.load(42d)).thenReturn(42d);
        when(storage.size()).thenReturn(1);

        assert cache.getSize() == 1;
        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 0;

        assert cache.getOrCreate(42d) == 42d;

        assert cache.getStatistics().getHits() == 1;
        assert cache.getStatistics().getMisses() == 0;

        verify(storage).size();
        verify(storage, atLeast(2)).load(42d);
        verifyNoMoreInteractions(storage);
    }

    public void testClear() {
        DoubleObjectStorage storage = mock(DoubleObjectStorage.class);

        DoubleDoubleCache cache = (DoubleDoubleCache) Wrapping.getFactory(new Signature(double.class, Object.class), new Signature(double.class, double.class), false).
                wrap("123", CALCULATABLE, storage, new MutableStatisticsImpl());
        cache.setDependencyNode(DependencyTracker.DUMMY_NODE);

        cache.clear();

        verify(storage).clear();
        verifyNoMoreInteractions(storage);
    }

    public void testSetDuringDependencyNodeOperations() {
        DoubleObjectStorage storage = mock(DoubleObjectStorage.class);

        when(storage.load(42d)).thenReturn(Storage.UNDEFINED, 42d);

        DoubleDoubleCalculatable calculatable = mock(DoubleDoubleCalculatable.class);
        MxResource r = mock(MxResource.class);
        when(calculatable.calculate("123", 42d)).thenThrow(new ResourceOccupied(r));

        DoubleDoubleCache cache = (DoubleDoubleCache) Wrapping.getFactory(new Signature(double.class, Object.class), new Signature(double.class, double.class), false).
                wrap("123", calculatable, storage, new MutableStatisticsImpl());
        cache.setDependencyNode(DependencyTracker.DUMMY_NODE);

        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 0;

        assert cache.getOrCreate(42d) == 42d;

        assert cache.getStatistics().getHits() == 1;
        assert cache.getStatistics().getMisses() == 0;

        verify(storage, atLeast(2)).load(42d);
        verifyNoMoreInteractions(storage);
        verify(calculatable).calculate("123", 42d);
        verifyNoMoreInteractions(calculatable);
    }

    public void testResetStat() {
        DoubleObjectStorage storage = mock(DoubleObjectStorage.class);

        when(storage.load(42d)).thenReturn(Storage.UNDEFINED);

        DoubleDoubleCache cache = (DoubleDoubleCache) Wrapping.getFactory(new Signature(double.class, Object.class), new Signature(double.class, double.class), false).
                wrap("123", CALCULATABLE, storage, new MutableStatisticsImpl());
        cache.setDependencyNode(DependencyTracker.DUMMY_NODE);

        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 0;

        assert cache.getOrCreate(42d) == 42d;

        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 1;

        cache.getStatistics().reset();

        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 0;

        verify(storage, atLeast(1)).load(42d);
        verify(storage).save(42d, 42d);
        verifyNoMoreInteractions(storage);
    }
}