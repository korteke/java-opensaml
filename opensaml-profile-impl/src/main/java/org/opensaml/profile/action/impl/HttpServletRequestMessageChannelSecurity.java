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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.opensaml.messaging.context.MessageChannelSecurityContext;
import org.opensaml.profile.context.ProfileRequestContext;

/**
 * Profile action which populates a {@link MessageChannelSecurityContext} based on a
 * {@link javax.servlet.http.HttpServletRequest}.
 */
public class HttpServletRequestMessageChannelSecurity extends AbstractMessageChannelSecurity {

    /** Flag controlling whether traffic on the default TLS port is "secure". */
    private boolean defaultPortInsecure;
    
    /** Constructor. */
    public HttpServletRequestMessageChannelSecurity() {
        defaultPortInsecure = true;
    }
    
    /**
     * Set whether traffic on the default TLS port is "secure" for the purposes of this action.
     * 
     * <p>Defaults to "true"</p>
     *
     * <p>Ordinarily TLS is considered a "secure" channel, but traffic to a default port meant
     * for browser access tends to rely on server certificates that are unsuited to secure messaging
     * use cases. This flag allows software layers to recognize traffic on this port as "insecure" and
     * needing additional security measures.</p>
     * 
     * @param flag flag to set
     */
    public void setDefaultPortInsecure(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        defaultPortInsecure = flag;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HttpServletRequest is required");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(ProfileRequestContext profileRequestContext) {
        final MessageChannelSecurityContext channelContext =
                getParentContext().getSubcontext(MessageChannelSecurityContext.class, true);
        
        final HttpServletRequest request = getHttpServletRequest();
        if (request.isSecure() && (!defaultPortInsecure || request.getLocalPort() != 443)) {
            channelContext.setConfidentialityActive(true);
            channelContext.setIntegrityActive(true);
        } else {
            channelContext.setConfidentialityActive(false);
            channelContext.setIntegrityActive(false);
        }
    }

}