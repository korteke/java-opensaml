/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package org.opensaml.storage.impl.memcached;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;

import java.nio.charset.StandardCharsets;

/**
 * Handles conversion of String values to bytes and back.
 *
 * @author Marvin S. Addison
 */
public class StringTranscoder implements Transcoder<String> {

    /** Max size is maximum default memcached value size, 1MB. */
    private static final int MAX_SIZE = 1024 * 1024;


    @Override
    public boolean asyncDecode(CachedData d) {
        return false;
    }

    @Override
    public CachedData encode(final String o) {
        return new CachedData(0, o.getBytes(StandardCharsets.UTF_8), MAX_SIZE);
    }

    @Override
    public String decode(final CachedData d) {
        return new String(d.getData(), StandardCharsets.UTF_8);
    }

    @Override
    public int getMaxSize() {
        return MAX_SIZE;
    }
}
