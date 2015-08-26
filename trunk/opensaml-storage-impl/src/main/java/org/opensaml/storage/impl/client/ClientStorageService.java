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
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.CookieManager;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.security.DataExpiredException;
import net.shibboleth.utilities.java.support.security.DataSealer;
import net.shibboleth.utilities.java.support.security.DataSealerException;
import net.shibboleth.utilities.java.support.security.DataSealerKeyStrategy;

import org.opensaml.storage.AbstractMapBackedStorageService;
import org.opensaml.storage.MutableStorageRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link StorageService} that stores data in-memory in a shared session attribute.
 * 
 * <p>The data for this service is managed in a {@link ClientStorageServiceStore} object, which must
 * be created by some operation within the container for this implementation to function. Actual
 * load/store of the data to/from that object is driven via companion classes. The serialization
 * of data via JSON is inside the storage object class, but the encryption/decryption is here.</p>
 */
public class ClientStorageService extends AbstractMapBackedStorageService implements Filter {

    /** Name of session attribute for session lock. */
    @Nonnull protected static final String LOCK_ATTRIBUTE =
            "org.opensaml.storage.impl.client.ClientStorageService.lock";

    /** Name of session attribute for storage object. */
    @Nonnull protected static final String STORAGE_ATTRIBUTE = 
            "org.opensaml.storage.impl.client.ClientStorageService.store";
    
    /** Enumeration of possible sources for the data. */
    public enum ClientStorageSource {
        /** Source was a cookie. */
        COOKIE,
        
        /** Source was HTML Local Storage. */
        HTML_LOCAL_STORAGE,
    }

    /** Default label for storage tracking. */
    @Nonnull @NotEmpty private static final String DEFAULT_STORAGE_NAME = "shib_idp_client_ss";
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ClientStorageService.class);

    /** Sizes to report for context, key, and value limits when particular sources are used. */
    @Nonnull @NotEmpty private Map<ClientStorageSource,Integer> capabilityMap; 

    /** Servlet request. */
    @NonnullAfterInit private HttpServletRequest httpServletRequest;
    
    /** Manages creation of cookies. */
    @NonnullAfterInit private CookieManager cookieManager;
    
    /** Label used to track storage. */
    @Nonnull @NotEmpty private String storageName;
    
    /** DataSealer instance to secure data. */
    @NonnullAfterInit private DataSealer dataSealer;

    /** KeyStrategy enabling us to detect whether data has been sealed with an older key. */
    @Nullable private DataSealerKeyStrategy keyStrategy;

    /** Constructor. */
    public ClientStorageService() {
        storageName = DEFAULT_STORAGE_NAME;
        capabilityMap = new HashMap<>(2);
        capabilityMap.put(ClientStorageSource.COOKIE, 4096);
        capabilityMap.put(ClientStorageSource.HTML_LOCAL_STORAGE, 1024 * 1024);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setCleanupInterval(final long interval) {
        // Don't allow a cleanup task.
        super.setCleanupInterval(0);
    }
    
    /**
     * Set the map of storage sources to capability/size limits.
     * 
     * <p>The defaults include 4192 characters for cookies and 1024^2 characters
     * for local storage.</p>
     * 
     * @param map capability map
     */
    public void setCapabilityMap(@Nonnull @NonnullElements final Map<ClientStorageSource,Integer> map) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        Constraint.isNotNull(map, "Capability map cannot be null");
        
        for (final Map.Entry<ClientStorageSource,Integer> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                capabilityMap.put(entry.getKey(), entry.getValue());
            }
        }
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
     * Get the {@link CookieManager} to use.
     * 
     * @return the CookieManager to use
     */
    @NonnullAfterInit public CookieManager getCookieManager() {
        return cookieManager;
    }
    
    /**
     * Set the {@link CookieManager} to use.
     * 
     * @param manager the CookieManager to use.
     */
    public void setCookieManager(@Nonnull final CookieManager manager) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        cookieManager = Constraint.isNotNull(manager, "CookieManager cannot be null");
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
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    /** {@inheritDoc} */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        // This is just a no-op available to preserve compatibility with web.xml references to the
        // older storage plugin that saved modified data back to a cookie on every response.
        chain.doFilter(request, response);
    }
    
    /** {@inheritDoc} */
    @Override
    public int getContextSize() {
        return capabilityMap.get(getSource());
    }

    /** {@inheritDoc} */
    @Override
    public int getKeySize() {
        return capabilityMap.get(getSource());
    }

    /** {@inheritDoc} */
    @Override
    public long getValueSize() {
        return capabilityMap.get(getSource());
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (httpServletRequest == null) {
            throw new ComponentInitializationException("HttpServletRequest must be set");
        } else if (dataSealer == null || cookieManager == null) {
            throw new ComponentInitializationException("DataSealer and CookieManager must be set");
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
     * Get the backing source of the loaded data.
     * 
     * <p>This method should <strong>not</strong> be called while holding the session lock
     * returned by {@link #getLock()}.</p>
     *  
     * @return the source of the loaded data
     */
    @Nonnull ClientStorageSource getSource() {
       final Lock lock = getLock().readLock();
       try {
           lock.lock();
           
           final HttpSession session = Constraint.isNotNull(httpServletRequest.getSession(),
                   "HttpSession cannot be null");
           final Object object = session.getAttribute(STORAGE_ATTRIBUTE + '.' + storageName);
           if (object != null && object instanceof ClientStorageServiceStore) {
               return ((ClientStorageServiceStore) object).getSource();
           }
           return ClientStorageSource.COOKIE;
       } finally {
           lock.unlock();
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
     * @param raw encrypted data to load as storage contents, or null if none
     * @param source indicates source of the data for later use
     */
    void load(@Nullable @NotEmpty final String raw, @Nonnull final ClientStorageSource source) {

        ClientStorageServiceStore storageObject;
        
        if (raw != null) {
            log.trace("{} Loading storage state into session", getLogPrefix());
            try {
                final StringBuffer keyAliasUsed = new StringBuffer();
                final String decrypted = dataSealer.unwrap(raw, keyAliasUsed);
                
                log.trace("{} Data after decryption: {}", getLogPrefix(), decrypted);
                
                storageObject = new ClientStorageServiceStore(decrypted, source);
                
                if (keyStrategy != null) {
                    try {
                        if (!keyStrategy.getDefaultKey().getFirst().equals(keyAliasUsed.toString())) {
                            storageObject.setDirty(true);
                        }
                    } catch (final KeyException e) {
                        log.error("{} Exception while accessing default key during stale key detection",
                                getLogPrefix(), e);
                    }
                }
                
                log.debug("{} Successfully decrypted and loaded storage state from client", getLogPrefix());
            } catch (final DataExpiredException e) {
                log.debug("{} Secured data or key has expired", getLogPrefix());
                storageObject = new ClientStorageServiceStore(null, source);
                storageObject.setDirty(true);
            } catch (final DataSealerException e) {
                log.error("{} Exception unwrapping secured data", getLogPrefix(), e);
                storageObject = new ClientStorageServiceStore(null, source);
                storageObject.setDirty(true);
            }
        } else {
            log.trace("{} Initializing empty storage state into session", getLogPrefix());
            storageObject = new ClientStorageServiceStore(null, source);
        }
        
        // The object should be loaded, and marked "clean", or in the event of just about any failure
        // it should be empty and marked "dirty" to force an overwrite of the expired or corrupted data.
        
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
     * @return if dirty, the operation to perform, if not dirty, a null value  
     */
    @Nullable ClientStorageServiceOperation save() {
        
        log.trace("{} Preserving storage state from session", getLogPrefix());
        
        final Lock lock = getLock().writeLock();
        try {
            lock.lock();
            
            final HttpSession session = Constraint.isNotNull(httpServletRequest.getSession(),
                    "HttpSession cannot be null");
            
            final Object object = session.getAttribute(STORAGE_ATTRIBUTE + '.' + storageName);
            if (object == null || !(object instanceof ClientStorageServiceStore)) {
                log.error("{} No storage object found in session", getLogPrefix());
                return null;
            }

            try {
                return ((ClientStorageServiceStore) object).save();
            } catch (final IOException e) {
                log.error("{} Error while serializing storage data", getLogPrefix(), e);
                return null;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get a prefix for log messages.
     * 
     * @return  logging prefix
     */
    @Nonnull @NotEmpty private String getLogPrefix() {
        return "StorageService " + getId() + ":";
    }
    
    /**
     * Implements a session-bound backing store and locking mechanism for the {@link ClientStorageService}.
     */
    public class ClientStorageServiceStore {
        
        /** The underlying map of data records. */
        @Nonnull @NonnullElements private final Map<String, Map<String, MutableStorageRecord>> contextMap;
        
        /** Data source. */
        @Nonnull private final ClientStorageSource source; 
        
        /** Dirty bit. */
        private boolean dirty;
        
        /**
         * Reconstitute stored data.
         * 
         * <p>The dirty bit is set based on the result. If successful, the bit is cleared,
         * but if an error occurs, it will be set.</p>
         * 
         * @param raw serialized data to load
         * @param src data source
         */
        ClientStorageServiceStore(@Nullable @NotEmpty final String raw, @Nonnull final ClientStorageSource src) {
            contextMap = new HashMap<>();
            source = Constraint.isNotNull(src, "Data source cannot be null");
            
            if (raw == null) {
                return;
            }
            
            try {
                final JsonReader reader = Json.createReader(new StringReader(raw));
                final JsonStructure st = reader.read();
                if (!(st instanceof JsonObject)) {
                    throw new JsonException("Found invalid data structure while parsing context map");
                }
                final JsonObject obj = (JsonObject) st;
                
                for (final Map.Entry<String,JsonValue> context : obj.entrySet()) {
                    if (context.getValue().getValueType() != JsonValue.ValueType.OBJECT) {
                        throw new JsonException("Found invalid data structure while parsing context map");
                    }
                    
                    // Create new context if necessary.
                    Map<String,MutableStorageRecord> dataMap = contextMap.get(context);
                    if (dataMap == null) {
                        dataMap = new HashMap<>();
                        contextMap.put(context.getKey(), dataMap);
                    }
                    
                    final JsonObject contextRecords = (JsonObject) context.getValue();
                    for (Map.Entry<String,JsonValue> record : contextRecords.entrySet()) {
                    
                        final JsonObject fields = (JsonObject) record.getValue();
                        Long exp = null;
                        if (fields.containsKey("x")) {
                            exp = fields.getJsonNumber("x").longValueExact();
                        }
                        
                        dataMap.put(record.getKey(), new MutableStorageRecord(fields.getString("v"), exp));
                    }
                }
                setDirty(false);
            } catch (final NullPointerException | ClassCastException | ArithmeticException | JsonException e) {
                contextMap.clear();
                // Setting this should force corrupt data in the client to be overwritten.
                setDirty(true);
                log.error("{} Found invalid data structure while parsing context map", getLogPrefix(), e);
            }
        }

        /**
         * Get the map of contexts to manipulate during operations.
         * 
         * @return map of contexts to manipulate
         */
        @Nonnull @NonnullElements @Live Map<String, Map<String, MutableStorageRecord>> getContextMap() {
            return contextMap;
        }
        
        /**
         * Get the data source.
         * 
         * @return data source
         */
        @Nonnull public ClientStorageSource getSource() {
            return source;
        }

        /**
         * Get the dirty bit for the current data.
         * 
         * @return  status of dirty bit
         */
        boolean isDirty() {
            return dirty;
        }
        
        /**
         * Set the dirty bit for the current data.
         * 
         * @param flag  dirty bit to set
         */
        void setDirty(final boolean flag) {
            dirty = flag;
        }

// Checkstyle: CyclomaticComplexity OFF        
        /**
         * Serialize current state of stored data into a storage operation.
         * 
         * @return the operation, or a null if the data has not been modified since loading or saving
         * 
         * @throws IOException if an error occurs
         */
        @Nullable ClientStorageServiceOperation save() throws IOException {
            
            if (!isDirty()) {
                log.trace("{} Storage state has not been modified, save operation skipped", getLogPrefix());
                return null;
            }
            
            if (contextMap.isEmpty()) {
                log.trace("{} Data is empty", getLogPrefix());
                return new ClientStorageServiceOperation(getId(), getStorageName(), null, source);
            }

            long exp = 0L;
            final long now = System.currentTimeMillis();
            boolean empty = true;

            try {
                final StringWriter sink = new StringWriter(128);
                final JsonGenerator gen = Json.createGenerator(sink);
                
                gen.writeStartObject();
                for (final Map.Entry<String,Map<String, MutableStorageRecord>> context : contextMap.entrySet()) {
                    gen.writeStartObject(context.getKey());
                    for (Map.Entry<String,MutableStorageRecord> entry : context.getValue().entrySet()) {
                        final MutableStorageRecord record = entry.getValue();
                        final Long recexp = record.getExpiration();
                        if (recexp == null || recexp > now) {
                            empty = false;
                            gen.writeStartObject(entry.getKey())
                                .write("v", record.getValue());
                            if (recexp != null) {
                                gen.write("x", record.getExpiration());
                                exp = Math.max(exp, recexp);
                            }
                            gen.writeEnd();
                        }
                    }
                    gen.writeEnd();
                }
                gen.writeEnd().close();

                if (empty) {
                    log.trace("{} Data is empty", getLogPrefix());
                    return new ClientStorageServiceOperation(getId(), getStorageName(), null, source);
                }
                
                final String raw = sink.toString();
                
                log.trace("{} Size of data before encryption is {}", getLogPrefix(), raw.length());
                log.trace("{} Data before encryption is {}", getLogPrefix(), raw);
                try {
                    final String wrapped = dataSealer.wrap(raw,
                            exp > 0 ? exp : System.currentTimeMillis() + 24 * 60 * 60 * 1000);
                    log.trace("{} Size of data after encryption is {}", getLogPrefix(), wrapped.length());
                    setDirty(false);
                    return new ClientStorageServiceOperation(getId(), getStorageName(), wrapped, source);
                } catch (final DataSealerException e) {
                    throw new IOException(e);
                }
            } catch (final JsonException e) {
                throw new IOException(e);
            }
        }
    }
// Checkstyle: CyclomaticComplexity ON
    
}