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

package org.opensaml.storage.impl.client;

import java.io.IOException;
import java.security.KeyException;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.security.DataExpiredException;
import net.shibboleth.utilities.java.support.security.DataSealer;
import net.shibboleth.utilities.java.support.security.DataSealerException;
import net.shibboleth.utilities.java.support.security.DataSealerKeyStrategy;

import org.opensaml.storage.AbstractMapBackedStorageService;
import org.opensaml.storage.MutableStorageRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * Implementation of {@link StorageService} that stores data in-memory in a shared session attribute.
 * 
 * <p>The data for this service is managed in a {@link ClientStorageServiceStore} object, which must
 * be created by some operation within the container for this implementation to function. Actual
 * load/store of the data to/from that object is driven via companion classes, but the serialization
 * of data via JSON is implemented here.</p>
 */
public class ClientStorageService extends AbstractMapBackedStorageService {

    /** Name of session attribute for session lock. */
    @Nonnull protected static final String LOCK_ATTRIBUTE =
            "org.opensaml.storage.impl.client.ClientStorageService.lock";

    /** Name of session attribute for context map. */
    @Nonnull protected static final String STORAGE_ATTRIBUTE = 
            "org.opensaml.storage.impl.client.ClientStorageService.store";

    /** Default label for storage tracking. */
    @Nonnull @NotEmpty private static final String DEFAULT_STORAGE_NAME = "shib_idp_req_ss";
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ClientStorageService.class);

    /** Servlet request. */
    @NonnullAfterInit private HttpServletRequest httpServletRequest;
    
    /** Label used to track storage. */
    @Nonnull @NotEmpty private String storageName;
    
    /** DataSealer instance to secure data. */
    @NonnullAfterInit private DataSealer dataSealer;

    /** KeyStrategy enabling us to detect whether data has been sealed with an older key. */
    @Nullable private DataSealerKeyStrategy keyStrategy;

    /** Constructor. */
    public ClientStorageService() {
        storageName = DEFAULT_STORAGE_NAME;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setCleanupInterval(final long interval) {
        // Don't allow a cleanup task.
        super.setCleanupInterval(0);
    }
    
    /**
     * Set the servlet request in which to manage per-request data.
     * 
     * @param request servlet request in which to manage data
     */
    public void setHttpServletRequest(@Nonnull final HttpServletRequest request) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest cannot be null");
    }

    /**
     * Get the label to use for storage tracking.
     * 
     * @return label to use
     */
    @Nonnull @NotEmpty public String getStorageName() {
        return storageName;
    }

    /**
     * Set the label to use for storage tracking.
     * 
     * @param name label to use
     */
    public void setStorageName(@Nonnull @NotEmpty final String name) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        storageName = Constraint.isNotNull(StringSupport.trimOrNull(name), "Storage name cannot be null or empty");
    }
    
    /**
     * Set the {@link DataSealer} to use for data security.
     * 
     * @param sealer {@link DataSealer} to use for data security
     */
    public void setDataSealer(@Nonnull final DataSealer sealer) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        dataSealer = Constraint.isNotNull(sealer, "DataSealer cannot be null");
    }

    /**
     * Set the {@link DataSealerKeyStrategy} to use for stale key detection.
     * 
     * @param strategy {@link DataSealerKeyStrategy} to use for stale key detection
     */
    public void setKeyStrategy(@Nonnull final DataSealerKeyStrategy strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        keyStrategy = strategy;
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (httpServletRequest == null) {
            throw new ComponentInitializationException("HttpServletRequest must be set");
        } else if (dataSealer == null) {
            throw new ComponentInitializationException("DataSealer must be set");
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nullable protected TimerTask getCleanupTask() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull protected ReadWriteLock getLock() {
        final HttpSession session = Constraint.isNotNull(httpServletRequest.getSession(), "HttpSession cannot be null");
        
        // Uses a lock bound to the session, creating one if this is the first attempt.
        
        Object lock = session.getAttribute(LOCK_ATTRIBUTE + '.' + storageName);
        if (lock == null || !(lock instanceof ReadWriteLock)) {
            synchronized (this) {
                // Recheck, somebody might have snuck in.
                lock = session.getAttribute(LOCK_ATTRIBUTE + '.' + storageName);
                if (lock == null) {
                    lock = new ReentrantReadWriteLock();
                    session.setAttribute(LOCK_ATTRIBUTE + '.' + storageName, lock);
                }
            }
        }
        
        return (ReadWriteLock) lock;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @Live protected Map<String, Map<String, MutableStorageRecord>> getContextMap() {
        
        final HttpSession session = Constraint.isNotNull(httpServletRequest.getSession(), "HttpSession cannot be null");
        final Object store = Constraint.isNotNull(session.getAttribute(STORAGE_ATTRIBUTE + '.' + storageName),
                "Storage object was not present in session");
        return ((ClientStorageServiceStore) store).getContextMap();
    }

    /** {@inheritDoc} */
    @Override
    protected void setDirty() {
        final HttpSession session = Constraint.isNotNull(httpServletRequest.getSession(), "HttpSession cannot be null");
        
        final Object store = session.getAttribute(STORAGE_ATTRIBUTE + '.' + storageName);
        if (store != null && store instanceof ClientStorageServiceStore) {
            ((ClientStorageServiceStore) store).setDirty(true);
        }
    }

    /**
     * Check whether data from the client has been loaded into the current session.
     * 
     * <p>This method should <strong>not</strong> be called while holding the session lock
     * returned by {@link #getLock()}.</p>
     *  
     * @return true iff the {@link HttpSession} contains a storage object
     */
    boolean isLoaded() {
       final Lock lock = getLock().readLock();
       try {
           lock.lock();
           
           final HttpSession session = Constraint.isNotNull(httpServletRequest.getSession(),
                   "HttpSession cannot be null");
           return session.getAttribute(STORAGE_ATTRIBUTE + '.' + storageName) instanceof ClientStorageServiceStore;
       } finally {
           lock.unlock();
       }
    }

    /**
     * Reconstitute stored data and inject it into the session.
     * 
     * <p>This method should <strong>not</strong> be called while holding the session lock
     * returned by {@link #getLock()}.</p>
     * 
     * @param raw encrypted data to load as storage contents
     * 
     * @throws IOException  if an error occurs reconstituting the data
     */
    void load(@Nonnull @NotEmpty final String raw) throws IOException {

        log.trace("Loading storage state from client into session");
        final ClientStorageServiceStore storageObject = new ClientStorageServiceStore();
        
        try {
            final StringBuffer keyAliasUsed = new StringBuffer();
            final String decrypted = dataSealer.unwrap(raw, keyAliasUsed);
            
            log.trace("Data after decryption: {}", decrypted);
            
            storageObject.load(decrypted);
            
            if (keyStrategy != null) {
                try {
                    if (!keyStrategy.getDefaultKey().getFirst().equals(keyAliasUsed.toString())) {
                        storageObject.setDirty(true);
                    }
                } catch (final KeyException e) {
                    log.error("Exception while accessing default key during stale key detection", e);
                }
            }
            
            log.debug("Successfully decrypted and loaded storage state from client");
        } catch (final DataExpiredException e) {
            log.debug("Secured data or key has expired");
        } catch (final DataSealerException e) {
            log.error("Exception unwrapping secured data", e);
        } catch (final IOException e) {
            log.error("Error while loading serialized storage data", e);
        }
        
        // The object should be loaded, and marked "clean", or in the event of just about any failure
        // it should be empty and marked "dirty" to force an overwrite of the corrupted data.
        
        final Lock lock = getLock().writeLock();
        try {
            lock.lock();
            
            final HttpSession session = Constraint.isNotNull(httpServletRequest.getSession(),
                    "HttpSession cannot be null");
            session.setAttribute(STORAGE_ATTRIBUTE + '.' + storageName, storageObject);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Serialize the stored data if it's in a "modified/dirty" state.
     * 
     * <p>This method should <strong>not</strong> be called while holding the session lock
     * returned by {@link #getLock()}.</p>
     * 
     * @return if dirty, the serialized data (or null if no data exists), if not dirty, an absent value  
     * 
     * @throws IOException  if an error occurs preserving the data
     */
    @Nullable Optional<String> save() throws IOException {
        
        log.trace("Preserving storage state from session for client");
        
        final Lock lock = getLock().writeLock();
        try {
            lock.lock();
            
            final HttpSession session = Constraint.isNotNull(httpServletRequest.getSession(),
                    "HttpSession cannot be null");
            
            final Object object = session.getAttribute(STORAGE_ATTRIBUTE + '.' + storageName);
            if (object == null || !(object instanceof ClientStorageServiceStore)) {
                log.error("No storage object found in session");
                return Optional.absent();
            }
            
            final ClientStorageServiceStore storageObject = (ClientStorageServiceStore) object;
            if (!storageObject.isDirty()) {
                log.trace("Storage state has not been modified, save operation skipped");
                return Optional.absent();
            }
            
            log.trace("Saving updated storage data to a string");
            try {
                final Pair<String,Long> toEncrypt = storageObject.save();
                log.trace("Size of data before encryption is {}", toEncrypt.getFirst().length());
                log.trace("Data before encryption is {}", toEncrypt.getFirst());
                
                try {
                    final String wrapped = dataSealer.wrap(toEncrypt.getFirst(),
                            toEncrypt.getSecond() > 0 ? toEncrypt.getSecond()
                                    : System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                    log.trace("Size of data after encryption is {}", wrapped.length());
                    storageObject.setDirty(false);
                    return Optional.of(wrapped);
                } catch (final DataSealerException e) {
                    throw new IOException(e);
                }
                
            } catch (final IOException e) {
                log.error("Error while serializing storage data", e);
                return Optional.absent();
            }
            
        } finally {
            lock.unlock();
        }
    }

}