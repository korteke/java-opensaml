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
import java.util.Arrays;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.CookieManager;
import net.shibboleth.utilities.java.support.net.UriSupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.security.DataSealer;
import net.shibboleth.utilities.java.support.security.DataSealerException;

import org.opensaml.storage.AbstractMapBackedStorageService;
import org.opensaml.storage.MutableStorageRecord;
import org.opensaml.storage.RequestScopedStorageService;
import org.opensaml.storage.VersionMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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

    /** Default cookie name for storage tracking. */
    @Nonnull @NotEmpty private static final String DEFAULT_COOKIE_NAME = "shib_idp_json_ss";
    
    /** A dummy lock implementation. */
    @Nonnull private static final ReadWriteLock DUMMY_LOCK;
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(JSONRequestScopedStorageService.class);

    /** Servlet request. */
    @NonnullAfterInit private HttpServletRequest httpServletRequest;

    /** Servlet response. */
    @NonnullAfterInit private HttpServletResponse httpServletResponse;
    
    /** Manages creation of cookies. */
    @NonnullAfterInit private CookieManager cookieManager;
    
    /** Name of cookie used to track storage. */
    @Nonnull @NotEmpty private String cookieName;
    
    /** DataSealer instance to secure data. */
    @NonnullAfterInit private DataSealer dataSealer;

    /** Constructor. */
    public JSONRequestScopedStorageService() {
        super();
        
        cookieName = DEFAULT_COOKIE_NAME;
    }

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
    public void setHttpServletRequest(@Nonnull final HttpServletRequest request) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest cannot be null");
    }

    /**
     * Set the servlet response in which to manage per-request data.
     * 
     * @param response servlet response in which to manage data
     */
    public void setHttpServletResponse(@Nonnull final HttpServletResponse response) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        httpServletResponse = Constraint.isNotNull(response, "HttpServletResponse cannot be null");
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
     * Set the cookie name to use for storage tracking.
     * 
     * @param name cookie name to use
     */
    public void setCookieName(@Nonnull @NotEmpty final String name) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        cookieName = Constraint.isNotNull(StringSupport.trimOrNull(name), "Cookie name cannot be null or empty");
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
        
        if (httpServletRequest == null || httpServletResponse == null) {
            throw new ComponentInitializationException("HttpServletRequest and HttpServletResponse must be set");
        } else if (dataSealer == null || cookieManager == null) {
            throw new ComponentInitializationException("DataSealer and CookieManager must be set");
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
    public void load() throws IOException {
        log.debug("Loading storage state from cookie in current request");
        
        getContextMap().clear();
        setDirty(false);
        
        // Search for our cookie.
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            return;
        }

        Optional<Cookie> cookie = Iterables.tryFind(Arrays.asList(cookies), new Predicate<Cookie>() {
            public boolean apply(@Nullable final Cookie c) {
                return c != null && c.getName().equals(cookieName);
            }
        });
        
        if (!cookie.isPresent() || cookie.get().getValue() == null || cookie.get().getValue().isEmpty()) {
            return;
        }
        
        try {
            String decrypted = dataSealer.unwrap(UriSupport.urlDecode(cookie.get().getValue()));
            
            log.trace("Data after decryption: {}", decrypted);
            
            final JsonReader reader = Json.createReader(new StringReader(decrypted));
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
    @Nullable public void save() throws IOException {
        if (!isDirty()) {
            log.debug("Storage state has not been modified during request, save operation skipped");
            return;
        }

        log.debug("Saving updated storage data to cookie");
        
        Map<String, Map<String, MutableStorageRecord>> contextMap = getContextMap();
        if (contextMap.isEmpty()) {
            log.trace("Context map was empty, unsetting storage cookie");
            cookieManager.unsetCookie(cookieName);
            setDirty(false);
            return;
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
                log.trace("Context map was empty, unsetting storage cookie");
                cookieManager.unsetCookie(cookieName);
                setDirty(false);
                return;
            }
            
            String toEncrypt = sink.toString();
            log.trace("Size of data before encryption is {}", toEncrypt.length());
            log.trace("Data before encryption is {}", toEncrypt);
            
            try {
                String wrapped = dataSealer.wrap(toEncrypt, exp > 0 ? exp : now + 24 * 60 * 60 * 1000);
                log.trace("Size of data after encryption is {}", wrapped.length());
                cookieManager.addCookie(cookieName, UriSupport.urlEncode(wrapped));
                setDirty(false);
            } catch (DataSealerException e) {
                throw new IOException(e);
            }
        } catch (JsonException e) {
            log.error("JsonException while serializing context map", e);
            throw new IOException(e);
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
        Object contextMap = httpServletRequest.getAttribute(CONTEXT_MAP_ATTRIBUTE);
        if (contextMap != null) {
            return (Map<String, Map<String, MutableStorageRecord>>) contextMap;
        }
        
        Map<String, Map<String, MutableStorageRecord>> newMap = Maps.newHashMap();
        httpServletRequest.setAttribute(CONTEXT_MAP_ATTRIBUTE, newMap);
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
            httpServletRequest.setAttribute(DIRTY_BIT_ATTRIBUTE, Boolean.TRUE);
        } else {
            httpServletRequest.removeAttribute(DIRTY_BIT_ATTRIBUTE);
        }
    }
    
    /** {@inheritDoc} */
    private boolean isDirty() {
        Object dirty = httpServletRequest.getAttribute(DIRTY_BIT_ATTRIBUTE);
        if (dirty != null && dirty instanceof Boolean) {
            return (Boolean) dirty;
        } else {
            return false;
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