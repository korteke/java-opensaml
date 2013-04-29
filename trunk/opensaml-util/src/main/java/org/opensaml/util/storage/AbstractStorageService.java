/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.util.storage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.AbstractDestructableIdentifiableInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentValidationException;

/**
 * Abstract base class for {@link StorageService} implementations.
 * 
 * <p>The base class handles support for a background cleanup task, supplies Text method
 * implementations that call through to unimplemented String-based versions, and handles
 * calling of custom object serializers. 
 */
public abstract class AbstractStorageService extends AbstractDestructableIdentifiableInitializableComponent
    implements StorageService {

    /**
     * Number of milliseconds between cleanup checks. Default value: {@value} (5 minutes)
     */
    private long cleanupInterval = 300000;

    /** Timer used to schedule cleanup tasks. */
    private Timer cleanupTaskTimer;

    /** Task that cleans up expired records. */
    private TimerTask cleanupTask;
        
    /**
     * Constructor.
     *
     */
    public AbstractStorageService() {
        
    }

    /**
     * Gets the number of milliseconds between one cleanup and another. A value of 0 or less indicates that no
     * cleanup will be performed.
     * 
     * @return number of milliseconds between one cleanup and another
     */
    public long getCleanupInterval() {
        return cleanupInterval;
    }

    /**
     * Sets the number of milliseconds between one cleanup and another. A value of 0 or less indicates that no
     * cleanup will be performed.
     * 
     * This setting cannot be changed after the service has been initialized.
     * 
     * @param interval number of milliseconds between one cleanup and another
     */
    public synchronized void setCleanupInterval(long interval) {
        if (isInitialized()) {
            return;
        }

        cleanupInterval = interval;
    }

    /**
     * Gets the timer used to schedule cleanup tasks.
     * 
     * @return timer used to schedule cleanup tasks
     */
    @Nullable public Timer getCleanupTaskTimer() {
        return cleanupTaskTimer;
    }

    /**
     * Sets the timer used to schedule cleanup tasks.
     * 
     * This setting can not be changed after the service has been initialized.
     * 
     * @param timer timer used to schedule configuration reload tasks
     */
    public synchronized void setCleanupTaskTimer(@Nullable final Timer timer) {
        if (isInitialized()) {
            return;
        }

        cleanupTaskTimer = timer;
    }
    
    /**
     * Returns a cleanup task function to schedule for background cleanup.
     * 
     * <p>The default implementation does not supply one.</p>
     * 
     * @return a task object, or null
     */
    @Nullable protected TimerTask getCleanupTask() {
        return null;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (cleanupInterval > 0) {
            cleanupTask = getCleanupTask();
            if (cleanupTask == null || cleanupTaskTimer == null) {
                throw new ComponentInitializationException(
                        "Cleanup task and timer cannot be null if cleanupInterval is set.");
            }
            cleanupTaskTimer.schedule(cleanupTask, cleanupInterval, cleanupInterval);
        }
    }
    
    /** {@inheritDoc} */
    protected void doDestroy() {
        cleanupTask.cancel();
        cleanupTask = null;
        super.doDestroy();
    }
    
    /** {@inheritDoc} */
    public void validate() throws ComponentValidationException {

    }

    /** {@inheritDoc} */
    public boolean createText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            @Nonnull @NotEmpty String value) throws IOException {
        return createString(context, key, value);
    }

    /** {@inheritDoc} */
    public boolean createText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            @Nonnull @NotEmpty String value, long expiration) throws IOException {
        return createString(context, key, value, expiration);
    }

    /** {@inheritDoc} */
    public boolean createText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key, @Nonnull Object value,
            @Nonnull StorageSerializer serializer) throws IOException {
        return createString(context, key, serializer.serialize(value));
    }

    /** {@inheritDoc} */
    public boolean createText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key, @Nonnull Object value,
            @Nonnull StorageSerializer serializer, long expiration) throws IOException {
        return createString(context, key, serializer.serialize(value), expiration);
    }

    /** {@inheritDoc} */
    @Nullable public StorageRecord readText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key)
            throws IOException {
        return readString(context, key);
    }

    /** {@inheritDoc} */
    @Nonnull public Pair<Integer, StorageRecord> readText(@Nonnull @NotEmpty String context,
            @Nonnull @NotEmpty String key, int version) throws IOException {
        return readString(context, key, version);
    }

    /** {@inheritDoc} */
    @Nullable public Integer updateText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            @Nonnull @NotEmpty String value) throws IOException {
        return updateString(context, key, value);
    }

    /** {@inheritDoc} */
    @Nullable public Integer updateText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            @Nonnull @NotEmpty String value, long expiration) throws IOException {
        return updateString(context, key, value, expiration);
    }

    /** {@inheritDoc} */
    @Nullable public Integer updateTextWithVersion(int version, @Nonnull @NotEmpty String context,
            @Nonnull @NotEmpty String key, @Nonnull @NotEmpty String value)
                    throws IOException, VersionMismatchException {
        return updateStringWithVersion(version, context, key, value);
    }

    /** {@inheritDoc} */
    @Nullable public Integer updateTextWithVersion(int version, @Nonnull @NotEmpty String context,
            @Nonnull @NotEmpty String key, @Nonnull @NotEmpty String value, long expiration)
                    throws IOException, VersionMismatchException {
        return updateStringWithVersion(version, context, key, value, expiration);
    }

    /** {@inheritDoc} */
    @Nullable public Integer updateText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            @Nonnull Object value, @Nonnull StorageSerializer serializer) throws IOException {
        return updateString(context, key, serializer.serialize(value));
    }

    /** {@inheritDoc} */
    @Nullable public Integer updateText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            @Nonnull Object value, @Nonnull StorageSerializer serializer, long expiration) throws IOException {
        return updateString(context, key, serializer.serialize(value), expiration);
    }

    /** {@inheritDoc} */
    @Nullable public Integer updateTextWithVersion(int version, @Nonnull @NotEmpty String context,
            @Nonnull @NotEmpty String key, @Nonnull Object value, @Nonnull StorageSerializer serializer)
                    throws IOException, VersionMismatchException {
        return updateStringWithVersion(version, context, key, serializer.serialize(value));
    }

    /** {@inheritDoc} */
    @Nullable public Integer updateTextWithVersion(int version, @Nonnull @NotEmpty String context,
            @Nonnull @NotEmpty String key, @Nonnull Object value, @Nonnull StorageSerializer serializer,
            long expiration) throws IOException, VersionMismatchException {
        return updateStringWithVersion(version, context, key, serializer.serialize(value), expiration);
    }

    /** {@inheritDoc} */
    @Nullable public Integer updateTextExpiration(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key)
            throws IOException {
        return updateStringExpiration(context, key);
    }

    /** {@inheritDoc} */
    @Nullable public Integer updateTextExpiration(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            long expiration) throws IOException {
        return updateStringExpiration(context, key, expiration);
    }

    /** {@inheritDoc} */
    public boolean deleteText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key) throws IOException {
        return deleteString(context, key);
    }

}
