/*
 * Copyright (c) 2008-2014 Maxifier Ltd. All Rights Reserved.
 */
package com.maxifier.mxcache.impl.caches.def;

import com.maxifier.mxcache.storage.CharacterByteStorage;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * CharacterByteCacheTest - test for CharacterByteTroveStorage
 *
 * THIS IS GENERATED CLASS! DON'T EDIT IT MANUALLY!
 *
 * GENERATED FROM P2PCacheTest.template
 *
 * @author Andrey Yakoushin (andrey.yakoushin@maxifier.com)
 * @author Alexander Kochurov (alexander.kochurov@maxifier.com)
 */
@Test
public class CharacterByteCacheTest {
    public void testCache() {
        CharacterByteStorage cache = new CharacterByteTroveStorage();

        assert cache.size() == 0;

        assert !cache.isCalculated('*');

        cache.save('*', (byte)42);

        assert cache.size() == 1;
        Assert.assertEquals(cache.load('*'), (byte)42);

        cache.clear();

        assert !cache.isCalculated('*');
    }
}