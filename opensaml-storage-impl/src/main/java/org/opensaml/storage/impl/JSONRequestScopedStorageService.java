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

package org.opensaml.storage.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletRequest;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.security.DataSealer;
import net.shibboleth.utilities.java.support.security.DataSealerException;

import org.opensaml.storage.AbstractMapBackedStorageService;
import org.opensaml.storage.MutableStorageRecord;
import org.opensaml.storage.RequestScopedStorageService;
import org.opensaml.storage.VersionMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;


/**
 * Implementation of {@link RequestScopedStorageService} that stores data in-memory in a servlet request attribute,
 * and reads and writes the data with a secured string form using JSON as the underlying format.
 */
public class JSONRequestScopedStorageService extends AbstractMapBackedStorageService
    implements RequestScopedStorageService {

    /** Name of request attribute for context map. */
    @Nonnull private static final String CONTEXT_MAP_ATTRIBUTE = 
            "org.opensaml.storage.impl.JSONRequestScopedStorageService.contextMap";

    /** Name of request attribute used as a dirty bit. */
    @Nonnull private static final String DIRTY_BIT_ATTRIBUTE =
            "org.opensaml.storage.impl.JSONRequestScopedStorageService.dirty";
    
    /** A dummy lock implementation. */
    @Nonnull private static final ReadWriteLock DUMMY_LOCK;
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(JSONRequestScopedStorageService.class);

    /** Servlet request. */
    @NonnullAfterInit private ServletRequest servletRequest;
    
    /** DataSealer instance to secure data. */
    @NonnullAfterInit private DataSealer dataSealer;
    
    /** {@inheritDoc} */
    public synchronized void setCleanupInterval(long interval) {
        // Don't allow a cleanup task.
        super.setCleanupInterval(0);
    }
    
    /**
     * Set the servlet request in which to manage per-request data.
     * 
     * @param request servlet request in which to manage data
     */
    public void setServletRequest(@Nonnull final ServletRequest request) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        servletRequest = Constraint.isNotNull(request, "ServletRequest cannot be null");
    }
    
    /**
     * Set the {@link DataSealer} to use for data security.
     * 
     * @param sealer    {@link DataSealer} to use for data security
     */
    public void setDataSealer(@Nonnull final DataSealer sealer) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        dataSealer = Constraint.isNotNull(sealer, "DataSealer cannot be null");
    }
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (servletRequest == null || dataSealer == null) {
            throw new ComponentInitializationException("ServletRequest or DataSealer is not set");
        }
    }

    /** {@inheritDoc} */
    public boolean create(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable final Long expiration) throws IOException {
        
        if (super.create(context, key, value, expiration)) {
            setDirty(true);
            return true;
        } else {
            return false;
        }
    }

    /** {@inheritDoc} */
    public void updateContextExpiration(@Nonnull @NotEmpty final String context, @Nullable final Long expiration)
            throws IOException {
        super.updateContextExpiration(context, expiration);
        
        setDirty(true);
    }
    
    /** {@inheritDoc} */
    public void deleteContext(@Nonnull @NotEmpty final String context) throws IOException {
        super.deleteContext(context);
        
        setDirty(true);
    }

    /** {@inheritDoc} */
    public void reap(@Nonnull @NotEmpty final String context) throws IOException {
        super.reap(context);
        
        setDirty(true);
    }

    /** {@inheritDoc} */
    public void load(@Nullable final String source) throws IOException {
        getContextMap().clear();
        setDirty(false);
        
        if (source == null || source.isEmpty()) {
            return;
        }
        
        try {
            final JsonReader reader = Json.createReader(new StringReader(dataSealer.unwrap(source)));
            final JsonStructure st = reader.read();
            if (!(st instanceof JsonObject)) {
                throw new IOException("Found invalid data structure while parsing context map");
            }
            final JsonObject obj = (JsonObject) st;
            
            for (Map.Entry<String,JsonValue> context : obj.entrySet()) {
                if (context.getValue().getValueType() != JsonValue.ValueType.OBJECT) {
                    getContextMap().clear();
                    throw new IOException("Found invalid data structure while parsing context map");
                }
                
                JsonObject contextRecords = (JsonObject) context.getValue();
                for (Map.Entry<String,JsonValue> record : contextRecords.entrySet()) {
                
                    final JsonObject fields = (JsonObject) record.getValue();
                    Long exp = null;
                    if (fields.containsKey("x")) {
                        exp = fields.getJsonNumber("x").longValueExact();
                    }
                    
                    create(context.getKey(), record.getKey(), fields.getString("v"), exp);
                }
            }
            setDirty(false);
        } catch (NullPointerException | ClassCastException | ArithmeticException | JsonException e) {
            getContextMap().clear();
            setDirty(false);
            log.error("Exception while parsing context map", e);
            throw new IOException("Found invalid data structure while parsing context map", e);
        } catch (DataSealerException e) {
            log.error("Exception unwrapping secured data", e);
            throw new IOException("Exception unwrapping secured data", e);
        }
    }

    /** {@inheritDoc} */
    @Nullable public String save() throws IOException {
        
        Map<String, Map<String, MutableStorageRecord>> contextMap = getContextMap();
        if (contextMap.isEmpty()) {
            log.trace("context map was empty, no data to save");
            return null;
        }

        long exp = 0;
        final long now = System.currentTimeMillis();
        boolean empty = true;

        try {
            final StringWriter sink = new StringWriter(128);
            final JsonGenerator gen = Json.createGenerator(sink);
            
            gen.writeStartObject();
            for (Map.Entry<String,Map<String, MutableStorageRecord>> context : contextMap.entrySet()) {
                gen.writeStartObject(context.getKey());
                for (Map.Entry<String,MutableStorageRecord> entry : context.getValue().entrySet()) {
                    final MutableStorageRecord record = entry.getValue();
                    final Long recexp = record.getExpiration();
                    if (recexp == null || recexp > now) {
                        empty = false;
                        gen.writeStartObject(entry.getKey());
                        gen.write("v", record.getValue());
                        if (recexp != null) {
                            gen.write("x", record.getExpiration());
                            exp = Math.max(exp, recexp);
                        }
                        gen.writeEnd();
                    }
                }
                gen.writeEnd();
            }
            gen.writeEnd();
            gen.close();

            if (empty) {
                log.trace("context map was empty, no data to save");
                return null;
            }
            
            log.trace("size of data before encrypting is {}", sink.toString().length());
            
            try {
                String wrapped = dataSealer.wrap(sink.toString(), exp > 0 ? exp : now + 24 * 60 * 60 * 1000);
                log.trace("size of data after encrypting is {}", wrapped.length());
                return wrapped;
            } catch (DataSealerException e) {
                throw new IOException(e);
            }
        } catch (JsonException e) {
            log.error("JsonException while serializing context map", e);
            throw new IOException(e);
        }
    }
    
    /** {@inheritDoc} */
    public boolean isDirty() {
        Object dirty = servletRequest.getAttribute(DIRTY_BIT_ATTRIBUTE);
        if (dirty != null && dirty instanceof Boolean) {
            return (Boolean) dirty;
        } else {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Nullable protected Integer updateImpl(@Nullable final Integer version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nullable final String value, @Nullable final Long expiration)
                    throws IOException, VersionMismatchException {
        Integer i = super.updateImpl(version, context, key, value, expiration);
        if (i != null) {
            setDirty(true);
        }
        return i;
    }

    /** {@inheritDoc} */
    protected boolean deleteImpl(@Nullable @Positive final Integer version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException {
        if (super.deleteImpl(version, context, key)) {
            setDirty(true);
            return true;
        } else {
            return false;
        }
    }
    
    /** {@inheritDoc} */
    @Nullable protected TimerTask getCleanupTask() {
        return null;
    }

    /** {@inheritDoc} */
    @Nonnull @NonnullElements @Live protected Map<String, Map<String, MutableStorageRecord>> getContextMap() {
        Object contextMap = servletRequest.getAttribute(CONTEXT_MAP_ATTRIBUTE);
        if (contextMap != null) {
            return (Map<String, Map<String, MutableStorageRecord>>) contextMap;
        }
        
        Map<String, Map<String, MutableStorageRecord>> newMap = Maps.newHashMap();
        servletRequest.setAttribute(CONTEXT_MAP_ATTRIBUTE, newMap);
        return newMap;
    }
    
    /** {@inheritDoc} */
    @Nonnull protected ReadWriteLock getLock() {
        return DUMMY_LOCK;
    }
    
    /**
     * Set the dirty bit for the current request.
     * 
     * @param flag  dirty bit to set
     */
    private void setDirty(final boolean flag) {
        if (flag) {
            servletRequest.setAttribute(DIRTY_BIT_ATTRIBUTE, Boolean.TRUE);
        } else {
            servletRequest.removeAttribute(DIRTY_BIT_ATTRIBUTE);
        }
    }
    
    /** Dummy shared lock that no-ops. */
    private static class DummyReadWriteLock implements ReadWriteLock {

        /** Dummy lock to return. */
        private static DummyLock lock;
        
        /** Constructor. */
        public DummyReadWriteLock() {
            lock = new DummyLock();
        }
        
        /** {@inheritDoc} */
        public Lock readLock() {
            return lock;
        }

        /** {@inheritDoc} */
        public Lock writeLock() {
            return lock;
        }
        
        /** Dummy lock that no-ops. */
        private static class DummyLock implements Lock {

            /** {@inheritDoc} */
            public void lock() {
                
            }

            /** {@inheritDoc} */
            public void lockInterruptibly() throws InterruptedException {
                
            }

            /** {@inheritDoc} */
            public boolean tryLock() {
                return true;
            }

            /** {@inheritDoc} */
            public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
                return true;
            }

            /** {@inheritDoc} */
            public void unlock() {
                
            }

            /** {@inheritDoc} */
            public Condition newCondition() {
                throw new UnsupportedOperationException("Conditions not supported");
            }
         
        }
    }
    
    static {
        DUMMY_LOCK = new DummyReadWriteLock();
    }
}