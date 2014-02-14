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

package org.opensaml.profile.action.impl;

import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.messaging.context.MessageChannelSecurityContext;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;

/**
 * Profile action which populates a {@link MessageChannelSecurityContext} based on an {@link HttpServletRequest}.
 */
public class HttpServletRequestMessageChannelSecurity extends AbstractMessageChannelSecurity {
    
    @NonnullAfterInit private HttpServletRequest httpServletRequest;

    /**
     * Get the {@link HttpServletRequest}
     * 
     * @return Returns the httpServletRequest.
     */
    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Set the {@link HttpServletRequest}
     * 
     * @param httpServletRequest The httpServletRequest to set.
     */
    public void setHttpServletRequest(HttpServletRequest request) {
        httpServletRequest = request;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (httpServletRequest == null) {
            throw new ComponentInitializationException("HttpServletRequest is required");
        }
    }

    /** {@inheritDoc} */
    protected void doExecute(ProfileRequestContext profileRequestContext) throws ProfileException {
        MessageChannelSecurityContext channelContext = getParentContext().getSubcontext(MessageChannelSecurityContext.class, true);
        channelContext.setConfidentialityActive(getHttpServletRequest().isSecure());
        channelContext.setIntegrityActive(getHttpServletRequest().isSecure());
    }

}
