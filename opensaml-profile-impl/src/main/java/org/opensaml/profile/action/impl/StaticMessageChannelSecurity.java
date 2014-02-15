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

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageChannelSecurityContext;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;

/**
 * Profile action which populates a {@link MessageChannelSecurityContext} based on static configuration flags.
 */
public class StaticMessageChannelSecurity extends AbstractMessageChannelSecurity {
    
    /** Message channel confidentiality flag. */
    private boolean confidentialityActive;
    
    /** Message channel integrity flag. */
    private boolean integrityActive;
    
    /** Constructor. */
    public StaticMessageChannelSecurity() {
        super();
    }

    /**
     * Get whether message channel confidentiality is active.
     * 
     * @return Returns the confidentialityActive.
     */
    public boolean isConfidentialityActive() {
        return confidentialityActive;
    }

    /**
     * Set whether message channel confidentiality is active.
     * 
     * @param flag The confidentialityActive to set.
     */
    public void setConfidentialityActive(boolean flag) {
        confidentialityActive = flag;
    }

    /**
     * Get whether message channel integrity is active.
     * 
     * @return Returns the integrityActive.
     */
    public boolean isIntegrityActive() {
        return integrityActive;
    }

    /**
     * Set whether message channel integrity is active.
     * 
     * @param flag The integrityActive to set.
     */
    public void setIntegrityActive(boolean flag) {
        integrityActive = flag;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        final MessageChannelSecurityContext channelContext =
                getParentContext().getSubcontext(MessageChannelSecurityContext.class, true);
        channelContext.setConfidentialityActive(isConfidentialityActive());
        channelContext.setIntegrityActive(isIntegrityActive());
    }

}
