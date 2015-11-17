/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package org.opensaml.storage.impl.memcached;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.cryptacular.util.ByteUtil;

import java.nio.charset.StandardCharsets;

/**
 * Handles conversion of {@link MemcachedStorageRecord} to bytes and back.
 *
 * @author Marvin S. Addison
 */
public class StorageRecordTranscoder implements Transcoder<MemcachedStorageRecord> {

    /** Max size is maximum default memcached value size, 1MB. */
    private static final int MAX_SIZE = 1024 * 1024;


    @Override
    public boolean asyncDecode(CachedData d) {
        return false;
    }

    @Override
    public CachedData encode(final MemcachedStorageRecord o) {
        final byte[] value = o.getValue().getBytes(StandardCharsets.UTF_8);
        final byte[] encoded = new byte[value.length + 8];
        ByteUtil.toBytes(o.getExpiration() == null ? 0 : o.getExpiration().longValue(), encoded, 0);
        System.arraycopy(value, 0, encoded, 8, value.length);
        return new CachedData(0, encoded, MAX_SIZE);
    }

    @Override
    public MemcachedStorageRecord decode(final CachedData d) {
        final byte[] bytes = d.getData();
        final String value = new String(bytes, 8, bytes.length - 8, StandardCharsets.UTF_8);
        final long exp = ((long) bytes[0] << 56) | (((long) bytes[1] & 0xff) << 48) |
                (((long) bytes[2] & 0xff) << 40) | (((long) bytes[3] & 0xff) << 32) |
                (((long) bytes[4] & 0xff) << 24) | (((long) bytes[5] & 0xff) << 16) |
                (((long) bytes[6] & 0xff) << 8) | ((long) bytes[7] & 0xff);
        return new MemcachedStorageRecord(value, exp == 0 ? null : new Long(exp));
    }

    @Override
    public int getMaxSize() {
        return MAX_SIZE;
    }
}
