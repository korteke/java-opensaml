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

package org.opensaml.saml.common.messaging.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.BaseContext;

import com.google.common.base.Strings;

/**
 * Context for holding information related to the SAML binding in use.
 */
public class SamlBindingContext extends BaseContext {
    
    /** The relay state associated with the message. */
    private String relayState;
    
    /** The binding URI. */
    private String bindingUri;
    
    /** Flag indicating whether the message is signed at the binding level. */
    private boolean hasBindingSignature;
    
    /** Flag indicating whether the binding in use requires the presence within the message 
     * of information indicating the intended message destination endpoint URI. */
    private boolean isIntendedDestinationEndpointUriRequired;
    
    /**
     * Gets the relay state.
     * 
     * @return relay state associated with this protocol exchange, may be null
     */
    @Nullable @NotEmpty public String getRelayState() {
        return relayState;
    }

    /**
     * Sets the relay state.
     * 
     * @param state relay state associated with this protocol exchange
     */
    public void setRelayState(@Nullable String state) {
        relayState = StringSupport.trimOrNull(state);
    }

    /**
     * Get the SAML binding URI.
     * 
     * @return Returns the bindingUri.
     */
    @Nonnull @NotEmpty public String getBindingUri() {
        return bindingUri;
    }

    /**
     * Set the SAML binding URI.
     * 
     * @param newBindingUri the new binding URI
     */
    public void setBindingUri(@Nonnull @NotEmpty final String newBindingUri) {
        Constraint.isFalse(Strings.isNullOrEmpty(newBindingUri), "Binding URI was null or empty");
        bindingUri = newBindingUri;
    }

    /**
     * Get the flag indicating whether the message is signed at the binding level.
     * 
     * @return true if message was signed at the binding level, otherwise false
     */
    public boolean isHasBindingSignature() {
        return hasBindingSignature;
    }

    /**
     * Set the flag indicating whether the message is signed at the binding level.
     * 
     * @param flag true if message was signed at the binding level, otherwise false
     */
    public void setHasBindingSignature(boolean flag) {
        this.hasBindingSignature = flag;
    }

    /**
     * Get the flag indicating whether the binding in use requires the presence within the message 
     * of information indicating the intended message destination endpoint URI.
     * 
     * @return true if required, false otherwise
     */
    public boolean isIntendedDestinationEndpointUriRequired() {
        return isIntendedDestinationEndpointUriRequired;
    }

    /**
     * Set the flag indicating whether the binding in use requires the presence within the message 
     * of information indicating the intended message destination endpoint URI.
     * 
     * @param flag true if required, false otherwise
     */
    public void setIntendedDestinationEndpointUriRequired(boolean flag) {
        isIntendedDestinationEndpointUriRequired = flag;
    }


}
