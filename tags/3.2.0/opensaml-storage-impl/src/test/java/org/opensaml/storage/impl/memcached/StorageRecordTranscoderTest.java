/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package org.opensaml.storage.impl.memcached;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit test for {@link StorageRecordTranscoder} class.
 */
public class StorageRecordTranscoderTest {

    private StorageRecordTranscoder transcoder = new StorageRecordTranscoder();

    @DataProvider
    public Object[][] testRecords() {
        return new Object[][] {
                new Object[] {new MemcachedStorageRecord("Whither the weather", null)},
                new Object[] {new MemcachedStorageRecord("x", Long.MAX_VALUE)},
                new Object[] {new MemcachedStorageRecord("床前明月光，疑是地上霜. 举头望明月，低头思故乡.", 2515878896L)},
        };
    }

    @Test(dataProvider = "testRecords")
    public void testEncodeDecode(final MemcachedStorageRecord expected) {
        final MemcachedStorageRecord actual = transcoder.decode(transcoder.encode(expected));
        assertEquals(actual.getValue(), expected.getValue());
        assertEquals(actual.getExpiration(), expected.getExpiration());
        assertEquals(actual.getVersion(), expected.getVersion());
    }
}