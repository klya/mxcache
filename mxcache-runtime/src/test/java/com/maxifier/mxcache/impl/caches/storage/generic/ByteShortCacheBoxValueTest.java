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
 * ByteShortCacheBoxValueTest
 *
 * THIS IS GENERATED CLASS! DON'T EDIT IT MANUALLY!
 *
 * GENERATED FROM P2PBoxValueCacheTest.template
 *
 * @author Andrey Yakoushin (andrey.yakoushin@maxifier.com)
 * @author Alexander Kochurov (alexander.kochurov@maxifier.com)
 */
@Test
public class ByteShortCacheBoxValueTest {
    private static final ByteShortCalculatable CALCULATABLE = new ByteShortCalculatable() {
        @Override
        public short calculate(Object owner, byte o) {
            assert o == (byte)42;
            return (short)42;
        }
    };

    public void testMiss() {
        ByteObjectStorage storage = mock(ByteObjectStorage.class);

        when(storage.load((byte)42)).thenReturn(Storage.UNDEFINED);
        when(storage.size()).thenReturn(0);

        ByteShortCache cache = (ByteShortCache) Wrapping.getFactory(new Signature(byte.class, Object.class), new Signature(byte.class, short.class), false).
                wrap("123", CALCULATABLE, storage, new MutableStatisticsImpl());
        cache.setDependencyNode(DependencyTracker.DUMMY_NODE);

        assert cache.getSize() == 0;
        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 0;

        assert cache.getOrCreate((byte)42) == (short)42;

        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 1;

        verify(storage).size();
        verify(storage, atLeast(1)).load((byte)42);
        verify(storage).save((byte)42, (short)42);
        verifyNoMoreInteractions(storage);
    }

    public void testHit() {
        ByteObjectStorage storage = mock(ByteObjectStorage.class);

        ByteShortCache cache = (ByteShortCache) Wrapping.getFactory(new Signature(byte.class, Object.class), new Signature(byte.class, short.class), false).
                wrap("123", CALCULATABLE, storage, new MutableStatisticsImpl());
        cache.setDependencyNode(DependencyTracker.DUMMY_NODE);

        when(storage.load((byte)42)).thenReturn((short)42);
        when(storage.size()).thenReturn(1);

        assert cache.getSize() == 1;
        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 0;

        assert cache.getOrCreate((byte)42) == (short)42;

        assert cache.getStatistics().getHits() == 1;
        assert cache.getStatistics().getMisses() == 0;

        verify(storage).size();
        verify(storage, atLeast(2)).load((byte)42);
        verifyNoMoreInteractions(storage);
    }

    public void testClear() {
        ByteObjectStorage storage = mock(ByteObjectStorage.class);

        ByteShortCache cache = (ByteShortCache) Wrapping.getFactory(new Signature(byte.class, Object.class), new Signature(byte.class, short.class), false).
                wrap("123", CALCULATABLE, storage, new MutableStatisticsImpl());
        cache.setDependencyNode(DependencyTracker.DUMMY_NODE);

        cache.clear();

        verify(storage).clear();
        verifyNoMoreInteractions(storage);
    }

    public void testSetDuringDependencyNodeOperations() {
        ByteObjectStorage storage = mock(ByteObjectStorage.class);

        when(storage.load((byte)42)).thenReturn(Storage.UNDEFINED, (short)42);

        ByteShortCalculatable calculatable = mock(ByteShortCalculatable.class);
        MxResource r = mock(MxResource.class);
        when(calculatable.calculate("123", (byte)42)).thenThrow(new ResourceOccupied(r));

        ByteShortCache cache = (ByteShortCache) Wrapping.getFactory(new Signature(byte.class, Object.class), new Signature(byte.class, short.class), false).
                wrap("123", calculatable, storage, new MutableStatisticsImpl());
        cache.setDependencyNode(DependencyTracker.DUMMY_NODE);

        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 0;

        assert cache.getOrCreate((byte)42) == (short)42;

        assert cache.getStatistics().getHits() == 1;
        assert cache.getStatistics().getMisses() == 0;

        verify(storage, atLeast(2)).load((byte)42);
        verifyNoMoreInteractions(storage);
        verify(calculatable).calculate("123", (byte)42);
        verifyNoMoreInteractions(calculatable);
    }

    public void testResetStat() {
        ByteObjectStorage storage = mock(ByteObjectStorage.class);

        when(storage.load((byte)42)).thenReturn(Storage.UNDEFINED);

        ByteShortCache cache = (ByteShortCache) Wrapping.getFactory(new Signature(byte.class, Object.class), new Signature(byte.class, short.class), false).
                wrap("123", CALCULATABLE, storage, new MutableStatisticsImpl());
        cache.setDependencyNode(DependencyTracker.DUMMY_NODE);

        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 0;

        assert cache.getOrCreate((byte)42) == (short)42;

        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 1;

        cache.getStatistics().reset();

        assert cache.getStatistics().getHits() == 0;
        assert cache.getStatistics().getMisses() == 0;

        verify(storage, atLeast(1)).load((byte)42);
        verify(storage).save((byte)42, (short)42);
        verifyNoMoreInteractions(storage);
    }
}