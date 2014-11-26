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
import java.io.PrintWriter;
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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.CookieManager;
import net.shibboleth.utilities.java.support.net.URISupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.security.DataExpiredException;
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
public class ServletRequestScopedStorageService extends AbstractMapBackedStorageService
    implements RequestScopedStorageService, Filter {

    /** Name of request attribute for context map. */
    @Nonnull protected static final String CONTEXT_MAP_ATTRIBUTE = 
            "org.opensaml.storage.impl.ServletRequestScopedStorageService.contextMap";

    /** Name of request attribute used as a dirty bit. */
    @Nonnull protected static final String DIRTY_BIT_ATTRIBUTE =
            "org.opensaml.storage.impl.ServletRequestScopedStorageService.dirty";

    /** Default cookie name for storage tracking. */
    @Nonnull @NotEmpty private static final String DEFAULT_COOKIE_NAME = "shib_idp_req_ss";
    
    /** A dummy lock implementation. */
    @Nonnull private static final ReadWriteLock DUMMY_LOCK;
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ServletRequestScopedStorageService.class);

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
    public ServletRequestScopedStorageService() {
        cookieName = DEFAULT_COOKIE_NAME;
    }

    /** {@inheritDoc} */
    @Override
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
     * Get the cookie name to use for storage tracking.
     * 
     * @return cookie name to use
     */
    @Nonnull @NotEmpty public String getCookieName() {
        return cookieName;
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
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (httpServletRequest == null || httpServletResponse == null) {
            throw new ComponentInitializationException("HttpServletRequest and HttpServletResponse must be set");
        } else if (dataSealer == null || cookieManager == null) {
            throw new ComponentInitializationException("DataSealer and CookieManager must be set");
        }
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
    public void updateContextExpiration(@Nonnull @NotEmpty final String context, @Nullable final Long expiration)
            throws IOException {
        super.updateContextExpiration(context, expiration);
        
        setDirty(true);
    }
    
    /** {@inheritDoc} */
    @Override
    public void deleteContext(@Nonnull @NotEmpty final String context) throws IOException {
        super.deleteContext(context);
        
        setDirty(true);
    }

    /** {@inheritDoc} */
    @Override
    public void reap(@Nonnull @NotEmpty final String context) throws IOException {
        super.reap(context);
        
        setDirty(true);
    }

    /** {@inheritDoc} */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    /** {@inheritDoc} */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (!(response instanceof HttpServletResponse)) {
            throw new ServletException("Response was not an HttpServletResponse");
        }
        
        // Intercept output operations and inject a save() operation at all applicable points.
        chain.doFilter(request, new OutputInterceptingHttpServletResponseProxy((HttpServletResponse) response));
    }

// Checkstyle: CyclomaticComplexity|MethodLength OFF
    /**
     * Reconstitute stored data.
     * 
     * @throws IOException  if an error occurs reconstituting the data
     */
    protected void load() throws IOException {
        
        final Map<String,Map<String,MutableStorageRecord>> contextMap = getContextMap();
        
        // Check for recursion. If load() is called directly, the above getter will
        // call us, which means we need to short-circuit the "outer" load call by
        // detecting that data has been loaded already.
        if (!contextMap.isEmpty()) {
            return;
        }
        
        log.trace("Loading storage state from cookie in current request");
        
        setDirty(false);
        
        // Search for our cookie.
        final Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            return;
        }

        final Optional<Cookie> cookie = Iterables.tryFind(Arrays.asList(cookies), new Predicate<Cookie>() {
            public boolean apply(@Nullable final Cookie c) {
                return c != null && c.getName().equals(cookieName);
            }
        });
        
        if (!cookie.isPresent() || cookie.get().getValue() == null || cookie.get().getValue().isEmpty()) {
            return;
        }
        
        try {
            final String decrypted = dataSealer.unwrap(URISupport.doURLDecode(cookie.get().getValue()));
            
            log.trace("Data after decryption: {}", decrypted);
            
            final JsonReader reader = Json.createReader(new StringReader(decrypted));
            final JsonStructure st = reader.read();
            if (!(st instanceof JsonObject)) {
                throw new IOException("Found invalid data structure while parsing context map");
            }
            final JsonObject obj = (JsonObject) st;
            
            for (final Map.Entry<String,JsonValue> context : obj.entrySet()) {
                if (context.getValue().getValueType() != JsonValue.ValueType.OBJECT) {
                    contextMap.clear();
                    throw new IOException("Found invalid data structure while parsing context map");
                }
                
                final JsonObject contextRecords = (JsonObject) context.getValue();
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
        } catch (final NullPointerException | ClassCastException | ArithmeticException | JsonException e) {
            contextMap.clear();
            setDirty(true);
            log.error("Exception while parsing context map", e);
            throw new IOException("Found invalid data structure while parsing context map", e);
        } catch (final DataExpiredException e) {
            setDirty(true);
            log.debug("Secured data expired");
            return;
        } catch (final DataSealerException e) {
            setDirty(true);
            log.error("Exception unwrapping secured data", e);
            throw new IOException("Exception unwrapping secured data", e);
        }
    }
// Checkstyle: CyclomaticComplexity|MethodLength ON
    
// Checkstyle: CyclomaticComplexity OFF    
    /**
     * Write/preserve stored data for subsequent requests.
     * 
     * @throws IOException  if an error occurs preserving the data
     */
    @Nullable protected void save() throws IOException {
        if (!isDirty()) {
            log.trace("Storage state has not been modified during request, save operation skipped");
            return;
        }

        log.trace("Saving updated storage data to cookie");
        
        final Map<String, Map<String, MutableStorageRecord>> contextMap = getContextMap();
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
                log.trace("Context map was empty, unsetting storage cookie");
                cookieManager.unsetCookie(cookieName);
                setDirty(false);
                return;
            }
            
            final String toEncrypt = sink.toString();
            log.trace("Size of data before encryption is {}", toEncrypt.length());
            log.trace("Data before encryption is {}", toEncrypt);
            
            try {
                final String wrapped = dataSealer.wrap(toEncrypt, exp > 0 ? exp : now + 24 * 60 * 60 * 1000);
                log.trace("Size of data after encryption is {}", wrapped.length());
                cookieManager.addCookie(cookieName, URISupport.doURLEncode(wrapped));
                setDirty(false);
            } catch (final DataSealerException e) {
                throw new IOException(e);
            }
        } catch (final JsonException e) {
            log.error("JsonException while serializing context map", e);
            throw new IOException(e);
        }
    }
// Checkstyle: CyclomaticComplexity ON

    /** {@inheritDoc} */
    @Override
    @Nullable protected Long updateImpl(@Nullable final Long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nullable final String value, @Nullable final Long expiration)
                    throws IOException, VersionMismatchException {
        final Long i = super.updateImpl(version, context, key, value, expiration);
        if (i != null) {
            setDirty(true);
        }
        return i;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean deleteImpl(@Nullable @Positive final Long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException {
        if (super.deleteImpl(version, context, key)) {
            setDirty(true);
            return true;
        } else {
            return false;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable protected TimerTask getCleanupTask() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @Live protected Map<String, Map<String, MutableStorageRecord>> getContextMap() {
        
        final Object contextMap = httpServletRequest.getAttribute(CONTEXT_MAP_ATTRIBUTE + '.' + cookieName);
        if (contextMap != null) {
            return (Map<String, Map<String, MutableStorageRecord>>) contextMap;
        }

        final Map<String, Map<String, MutableStorageRecord>> newMap = Maps.newHashMap();
        httpServletRequest.setAttribute(CONTEXT_MAP_ATTRIBUTE + '.' + cookieName, newMap);
        
        // The first time through, do a load from the cookie.
        // Any subsequent calls to get the context map will return the previously set map.
        try {
            load();
        } catch (final IOException e) {
            setDirty(true);
            log.error("Error loading data from cookie, starting fresh", e);
        }
        
        return newMap;
    }
    
    /** {@inheritDoc} */
    @Override
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
            httpServletRequest.setAttribute(DIRTY_BIT_ATTRIBUTE  + '.' + cookieName, Boolean.TRUE);
        } else {
            httpServletRequest.removeAttribute(DIRTY_BIT_ATTRIBUTE + '.' + cookieName);
        }
    }

    /**
     * Get the dirty bit for the current request.
     * 
     * @return  status of dirty bit
     */
    private boolean isDirty() {
        final Object dirty = httpServletRequest.getAttribute(DIRTY_BIT_ATTRIBUTE + '.' + cookieName);
        if (dirty != null && dirty instanceof Boolean) {
            return (Boolean) dirty;
        } else {
            return false;
        }
    }

    /**
     * An implementation of {@link HttpServletResponse} which detects a response going out
     * from a servlet and executes a save operation.
     */
    private class OutputInterceptingHttpServletResponseProxy extends HttpServletResponseWrapper {

        /**
         * Constructor.
         *
         * @param response the response to delegate to
         */
        public OutputInterceptingHttpServletResponseProxy(@Nonnull final HttpServletResponse response) {
            super(response);
        }
    
        /** {@inheritDoc} */
        public ServletOutputStream getOutputStream() throws IOException {
            save();
            return super.getOutputStream();
        }

        /** {@inheritDoc} */
        public PrintWriter getWriter() throws IOException {
            save();
            return super.getWriter();
        }

        /** {@inheritDoc} */
        public void sendError(int sc, String msg) throws IOException {
            save();
            super.sendError(sc, msg);
        }

        /** {@inheritDoc} */
        public void sendError(int sc) throws IOException {
            save();
            super.sendError(sc);
        }

        /** {@inheritDoc} */
        public void sendRedirect(String location) throws IOException {
            save();
            super.sendRedirect(location);
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