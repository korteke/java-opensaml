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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.URISupport;

/**
 * An action that loads any number of {@link ClientStorageService} instances from a POST submission
 * or cookies as applicable.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 * 
 * @param <InboundMessageType>
 * @param <OutboundMessageType>
 */
public class LoadClientStorageServices<InboundMessageType, OutboundMessageType>
        extends AbstractProfileAction<InboundMessageType, OutboundMessageType> {
    
    /** Name of local storage form field containing local storage support indicator. */
    @Nonnull @NotEmpty public static final String SUPPORT_FORM_FIELD = "shib_idp_ls_supported";

    /** Name of local storage form field signaling success/failure of a read operation. */
    @Nonnull @NotEmpty public static final String SUCCESS_FORM_FIELD = "shib_idp_ls_success";

    /** Name of local storage form field containing value read. */
    @Nonnull @NotEmpty public static final String VALUE_FORM_FIELD = "shib_idp_ls_value";

    /** Name of local storage form field containing value read. */
    @Nonnull @NotEmpty public static final String EXCEPTION_FORM_FIELD = "shib_idp_ls_exception";

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(LoadClientStorageServices.class);
    
    /** Whether to allow for data loaded from local storage and submitted via POST. */
    private boolean useLocalStorage;
    
    /** The storage service instances to load. */
    @Nonnull @NonnullElements private Map<String,ClientStorageService> storageServices;
    
    /** Context to drive storage load. */
    @Nullable private ClientStorageLoadContext clientStorageLoadCtx;
    
    /** Constructor. */
    public LoadClientStorageServices() {
        useLocalStorage = false;
        storageServices = Collections.emptyMap();
    }

    /**
     * Set whether to allow for data loaded from local storage and submitted via POST.
     * 
     * @param flag flag to set
     */
    public void setUseLocalStorage(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        useLocalStorage = flag;
    }
    
    /**
     * Set the {@link ClientStorageService} instances to check for loading.
     * 
     * @param services instances to check for loading
     */
    public void setStorageServices(@Nonnull @NonnullElements final Collection<ClientStorageService> services) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        Constraint.isNotNull(services, "StorageService collection cannot be null");
        storageServices = new HashMap<>(services.size());
        for (final ClientStorageService ss : Collections2.filter(services, Predicates.notNull())) {
            storageServices.put(ss.getStorageName(), ss);
        }
    }
    
    /** {@inheritDoc} */
    @Override protected boolean doPreExecute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        if (storageServices.isEmpty()) {
            log.debug("{} No ClientStorageServices supplied, nothing to do", getLogPrefix());
            return false;
        }
        
        clientStorageLoadCtx = profileRequestContext.getSubcontext(ClientStorageLoadContext.class);
        if (clientStorageLoadCtx == null) {
            log.debug("{} No ClientStorageLoadContext found", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return false;
        }
        
        if (getHttpServletRequest() == null) {
            log.error("{} HttpServletRequest not available", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return false;
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Override protected void doExecute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext) {
        
        boolean useLS = useLocalStorage;
        if (useLS) {
            final String param = getHttpServletRequest().getParameter(SUPPORT_FORM_FIELD);
            if (param == null || !Boolean.valueOf(param)) {
                log.debug("{} Local storage not available, backing off to cookies", getLogPrefix());
                useLS = false;
            }
        }
        
        for (final String storageKey : clientStorageLoadCtx.getStorageKeys()) {
            
            final ClientStorageService storageService = storageServices.get(storageKey);
            if (storageService == null) {
                log.error("{} ClientStorageService with storage name '{}' missing from configuration", getLogPrefix(),
                        storageKey);
                continue;
            }
            
            if (useLS) {
                loadFromLocalStorage(storageService);
            } else {
                loadFromCookie(storageService);
            }
        }
        
        profileRequestContext.removeSubcontext(clientStorageLoadCtx);
    }

    /**
     * Load the specified storage service from a cookie.
     * 
     * @param storageService service to load
     */
    private void loadFromCookie(@Nonnull final ClientStorageService storageService) {
        
        Optional<Cookie> cookie = Optional.absent();
        
        // Search for our cookie.
        final Cookie[] cookies = getHttpServletRequest().getCookies();
        if (cookies != null) {
            cookie = Iterables.tryFind(Arrays.asList(cookies), new Predicate<Cookie>() {
                public boolean apply(@Nullable final Cookie c) {
                    return c != null && c.getName().equals(storageService.getStorageName());
                }
            });
        }

        if (!cookie.isPresent() || cookie.get().getValue() == null || cookie.get().getValue().isEmpty()) {
            log.debug("{} No cookie data present, initializing StorageService '{}' to empty state", getLogPrefix(),
                    storageService.getId());
            storageService.load(null, ClientStorageSource.COOKIE);
        } else {
            log.debug("{} Initializing StorageService '{}' from cookie", getLogPrefix(), storageService.getId());
            storageService.load(URISupport.doURLDecode(cookie.get().getValue()), ClientStorageSource.COOKIE);
        }
    }
 
    /**
     * Load the specified storage service from local storage data supplied in the POST.
     * 
     * @param storageService service to load
     */
    private void loadFromLocalStorage(@Nonnull final ClientStorageService storageService) {
        
        final HttpServletRequest request = getHttpServletRequest();
        
        String param = request.getParameter(SUCCESS_FORM_FIELD + '.' + storageService.getStorageName());
        if (param == null || !Boolean.valueOf(param)) {
            param = request.getParameter(EXCEPTION_FORM_FIELD + '.' + storageService.getStorageName());
            log.debug("{} Load from local storage failed ({}), initializing StorageService '{}' to empty state",
                    getLogPrefix(), param, storageService.getId());
            storageService.load(null, ClientStorageSource.HTML_LOCAL_STORAGE);
            return;
        }
        
        param = request.getParameter(VALUE_FORM_FIELD + '.' + storageService.getStorageName());
        if (param == null || param.isEmpty()) {
            log.debug("{} No local storage data present, initializing StorageService '{}' to empty state",
                    getLogPrefix(), storageService.getId());
            storageService.load(null, ClientStorageSource.HTML_LOCAL_STORAGE);
        } else {
            log.debug("{} Initializing StorageService '{}' from local storage data", getLogPrefix(),
                    storageService.getId());
            storageService.load(param, ClientStorageSource.HTML_LOCAL_STORAGE);
        }
    }

}