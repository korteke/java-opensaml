/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package org.opensaml.storage.impl.memcached;

import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.logic.Constraint;
import org.opensaml.storage.StorageCapabilities;

/**
 * Provides a description of memcached capabilities. Note that only value size is configurable since memcached supports
 * increasing the maximum slab size via the <code>-I</code> flag. {@link MemcachedStorageService} supports
 * arbitrarily large context names and keys by hashing values longer than 250 bytes, which is the maximum size allowed.
 *
 * @author Marvin S. Addison
 */
public class MemcachedStorageCapabilities implements StorageCapabilities {

    /** Memcached supports 1M slabs (i.e. values) by default and issues warning on increase. */
    private static long DEFAULT_MAX_VALUE = 1024 * 1024;

    /** Maximum size of memcached values. */
    @Positive
    private final long valueSize;


    public MemcachedStorageCapabilities() {
        this(DEFAULT_MAX_VALUE);
    }

    public MemcachedStorageCapabilities(@Positive final long maxValueSize) {
        Constraint.isGreaterThan(0, maxValueSize, "Maximum value size must be a positive integer");
        valueSize = maxValueSize;
    }

    @Override
    public int getContextSize(){
        return Integer.MAX_VALUE;
    }

    @Override
    public int getKeySize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long getValueSize() {
        return valueSize;
    }
}
