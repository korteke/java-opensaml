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

package org.opensaml.storage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.Duration;
import net.shibboleth.utilities.java.support.annotation.constraint.NonNegative;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.component.AbstractIdentifiedInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.storage.annotation.AnnotationSupport;

/**
 * Abstract base class for {@link StorageService} implementations.
 * 
 * <p>
 * The base class handles support for a background cleanup task, and handles calling of custom object serializers.
 * </p>
 */
public abstract class AbstractStorageService extends AbstractIdentifiedInitializableComponent implements
        StorageService, StorageCapabilities {

    /**
     * Number of seconds between cleanup checks. Default value: (0)
     */
    @Duration @NonNegative private long cleanupInterval;

    /** Timer used to schedule cleanup tasks. */
    private Timer cleanupTaskTimer;

    /** Timer used to schedule cleanup tasks if no external one set. */
    private Timer internalTaskTimer;

    /** Task that cleans up expired records. */
    private TimerTask cleanupTask;

    /** Configurable context size limit. */
    @Positive private int contextSize;

    /** Configurable key size limit. */
    @Positive private int keySize;

    /** Configurable value size limit. */
    @Positive private int valueSize;

    /**
     * Constructor.
     * 
     */
    public AbstractStorageService() {
        setId(getClass().getName());
    }

    /**
     * Gets the number of seconds between one cleanup and another. A value of 0 indicates that no cleanup will be
     * performed.
     * 
     * @return number of seconds between one cleanup and another
     */
    @NonNegative public long getCleanupInterval() {
        return cleanupInterval;
    }

    /**
     * Sets the number of seconds between one cleanup and another. A value of 0 indicates that no cleanup will be
     * performed.
     * 
     * This setting cannot be changed after the service has been initialized.
     * 
     * @param interval number of seconds between one cleanup and another
     */
    public synchronized void setCleanupInterval(@Duration @NonNegative final long interval) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        cleanupInterval =
                Constraint.isGreaterThanOrEqual(0, interval, "Cleanup interval must be greater than or equal to zero");
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
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        cleanupTaskTimer = timer;
    }

    /**
     * Returns a cleanup task function to schedule for background cleanup.
     * 
     * <p>
     * The default implementation does not supply one.
     * </p>
     * 
     * @return a task object, or null
     */
    @Nullable protected TimerTask getCleanupTask() {
        return null;
    }

    /**
     * Set the context size limit.
     * 
     * @param size limit on context size in characters
     */
    public void setContextSize(@Positive final int size) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        contextSize = (int) Constraint.isGreaterThan(0, size, "Size must be greater than zero");
    }

    /**
     * Set the key size limit.
     * 
     * @param size size limit on key size in characters
     */
    public void setKeySize(@Positive final int size) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        keySize = (int) Constraint.isGreaterThan(0, size, "Size must be greater than zero");
    }

    /**
     * Set the value size limit.
     * 
     * @param size size limit on value size in characters
     */
    public void setValueSize(@Positive final int size) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        valueSize = (int) Constraint.isGreaterThan(0, size, "Size must be greater than zero");
    }

    /** {@inheritDoc} */
    @Override protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (cleanupInterval > 0) {
            cleanupTask = getCleanupTask();
            if (cleanupTask == null) {
                throw new ComponentInitializationException("Cleanup task cannot be null if cleanupInterval is set.");
            } else if (cleanupTaskTimer == null) {
                internalTaskTimer = new Timer();
            } else {
                internalTaskTimer = cleanupTaskTimer;
            }
            internalTaskTimer.schedule(cleanupTask, cleanupInterval * 1000, cleanupInterval * 1000);
        }
    }

    /** {@inheritDoc} */
    @Override protected void doDestroy() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
            if (cleanupTaskTimer == null) {
                internalTaskTimer.cancel();
            }
            internalTaskTimer = null;
        }
        super.doDestroy();
    }

    /** {@inheritDoc} */
    @Override public synchronized void setId(@Nonnull @NotEmpty final String componentId) {
        super.setId(componentId);
    }

    /** {@inheritDoc} */
    @Override @Nonnull public StorageCapabilities getCapabilities() {
        return this;
    }

    /** {@inheritDoc} */
    @Override public int getContextSize() {
        return contextSize;
    }

    /** {@inheritDoc} */
    @Override public int getKeySize() {
        return keySize;
    }

    /** {@inheritDoc} */
    @Override public long getValueSize() {
        return valueSize;
    }

    /** {@inheritDoc} */
    @Override public boolean create(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull final Object value, @Nonnull final StorageSerializer serializer,
            @Nullable @Positive final Long expiration) throws IOException {
        return create(context, key, serializer.serialize(value), expiration);
    }

    /** {@inheritDoc} */
    @Override public boolean create(@Nonnull final Object value) throws IOException {
        return create(AnnotationSupport.getContext(value), AnnotationSupport.getKey(value),
                AnnotationSupport.getValue(value), AnnotationSupport.getExpiration(value));
    }

    /** {@inheritDoc} */
    @Override @Nullable public Object read(@Nonnull final Object value) throws IOException {
        StorageRecord record = read(AnnotationSupport.getContext(value), AnnotationSupport.getKey(value));
        if (record != null) {
            AnnotationSupport.setValue(value, record.getValue());
            AnnotationSupport.setExpiration(value, record.getExpiration());
            return value;
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override @Nullable public Integer update(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull final Object value,
            @Nonnull final StorageSerializer serializer, @Nullable @Positive final Long expiration) throws IOException {
        return update(context, key, serializer.serialize(value), expiration);
    }

    /** {@inheritDoc} */
    // Checkstyle: ParameterNumber OFF
    @Override @Nullable public Integer updateWithVersion(@Positive final int version,
            @Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key, @Nonnull final Object value,
            @Nonnull final StorageSerializer serializer, @Nullable @Positive final Long expiration) throws IOException,
            VersionMismatchException {
        return updateWithVersion(version, context, key, serializer.serialize(value), expiration);
    }

    // Checkstyle: ParameterNumber ON

    /** {@inheritDoc} */
    @Override @Nullable public Integer update(@Nonnull final Object value) throws IOException {
        return update(AnnotationSupport.getContext(value), AnnotationSupport.getKey(value),
                AnnotationSupport.getValue(value), AnnotationSupport.getExpiration(value));
    }

    /** {@inheritDoc} */
    @Override @Nullable public Integer updateWithVersion(@Positive final int version, @Nonnull final Object value)
            throws IOException, VersionMismatchException {
        return updateWithVersion(version, AnnotationSupport.getContext(value), AnnotationSupport.getKey(value),
                AnnotationSupport.getValue(value), AnnotationSupport.getExpiration(value));
    }

    /** {@inheritDoc} */
    @Override @Nullable public Integer updateExpiration(@Nonnull final Object value) throws IOException {
        return updateExpiration(AnnotationSupport.getContext(value), AnnotationSupport.getKey(value),
                AnnotationSupport.getExpiration(value));
    }

    /** {@inheritDoc} */
    @Override public boolean delete(@Nonnull final Object value) throws IOException {
        return delete(AnnotationSupport.getContext(value), AnnotationSupport.getKey(value));
    }

    /** {@inheritDoc} */
    @Override public boolean deleteWithVersion(@Positive final int version, @Nonnull final Object value)
            throws IOException, VersionMismatchException {
        return deleteWithVersion(version, AnnotationSupport.getContext(value), AnnotationSupport.getKey(value));
    }

}