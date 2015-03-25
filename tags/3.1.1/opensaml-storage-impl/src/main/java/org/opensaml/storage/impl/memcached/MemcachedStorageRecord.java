/*
 * See LICENSE for licensing and NOTICE for copyright.
 */

package org.opensaml.storage.impl.memcached;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import org.opensaml.storage.StorageRecord;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Storage record implementation for use with {@link MemcachedStorageService}.
 *
 * @author Marvin S. Addison
 */
public class MemcachedStorageRecord extends StorageRecord {

    /**
     * Creates a new instance with specific record version.
     *
     * @param val Stored value.
     * @param exp Expiration instant in milliseconds, null for infinite expiration.
     */
    public MemcachedStorageRecord(@Nonnull @NotEmpty String val, @Nullable Long exp) {
        super(val, exp);
    }

    /**
     * Converts a {@link org.opensaml.storage.StorageRecord#getExpiration()} value in milliseconds to the corresponding value in seconds.
     *
     * @return 0 if given expiration is null, otherwise <code>exp/1000</code>.
     */
    public static int expiry(final Long exp) {
        return exp == null ? 0 : (int) (exp / 1000);
    }

    /**
     * Gets the expiration date as an integer representing seconds since the Unix epoch, 1970-01-01T00:00:00.
     * The value provided by this method is suitable for representing the memcached entry expiration.
     *
     * @return 0 if expiration is null, otherwise <code>getExpiration()/1000</code>.
     */
    public int getExpiry() {
        return expiry(getExpiration());
    }

    /**
     * Sets the record version.
     * @param version Record version; must be positive.
     */
    @Override
    protected void setVersion(@Positive final long version) {
        super.setVersion(version);
    }
}
