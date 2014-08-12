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

package org.opensaml.profile.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.InOutOperationContext;

/**
 * Context that holds the ongoing state of a profile request.
 * 
 * @param <InboundMessageType> type of in-bound message
 * @param <OutboundMessageType> type of out-bound message
 */
@ThreadSafe
public final class ProfileRequestContext<InboundMessageType, OutboundMessageType> extends
        InOutOperationContext<InboundMessageType, OutboundMessageType> {

    /** ID under which this context is stored, for example, within maps or sessions. */
    public static final String BINDING_KEY = "opensamlProfileRequestContext";

    /** Profile ID if not overridden. */
    public static final String ANONYMOUS_PROFILE_ID = "anonymous";

    /** Unique identifier for the profile/operation/function of the current request. */
    private String profileId;

    /** Logging label for the profile/operation/function . */
    private String loggingId;
    
    /** Whether the current profile request is browser-based. */
    private boolean browserProfile;

    /** Constructor. */
    public ProfileRequestContext() {
        profileId = ANONYMOUS_PROFILE_ID;
        loggingId = ANONYMOUS_PROFILE_ID;
    }

    /**
     * Get the ID of the profile used by the current request.
     * 
     * @return ID of the profile used by the current request
     */
    @Nonnull @NotEmpty public String getProfileId() {
        return profileId;
    }

    /**
     * Set the ID of the profile used by the current request.
     * 
     * @param id ID of the profile used by the current request
     */
    public void setProfileId(@Nullable final String id) {
        final String trimmedId = StringSupport.trimOrNull(id);
        if (trimmedId == null) {
            profileId = ANONYMOUS_PROFILE_ID;
        } else {
            profileId = trimmedId;
        }
    }

    /**
     * Get the logging ID of the profile used by the current request.
     * 
     * <p>The logging ID is used for audit logging and may be used for other
     * logging-related functions such as in diagnostic contexts.</p>
     * 
     * @return ID of the profile used for logging
     */
    @Nonnull @NotEmpty public String getLoggingId() {
        return loggingId;
    }

    /**
     * Set the logging ID of the profile used by the current request.
     * 
     * <p>The logging ID is used for audit logging and may be used for other
     * logging-related functions such as in diagnostic contexts.</p>
     * 
     * @param id ID of the profile used for logging
     */
    public void setLoggingId(@Nullable final String id) {
        final String trimmedId = StringSupport.trimOrNull(id);
        if (trimmedId == null) {
            loggingId = ANONYMOUS_PROFILE_ID;
        } else {
            loggingId = trimmedId;
        }
    }
    
    /**
     * Get whether the current profile request is browser-based (defaults to false).
     * 
     * @return whether the current profile request is browser-based
     */
    public boolean isBrowserProfile() {
        return browserProfile;
    }

    /**
     * Set whether the current profile request is browser-based.
     * 
     * @param browser whether the current profile request is browser-based
     */
    public void setBrowserProfile(final boolean browser) {
        browserProfile = browser;
    }

}