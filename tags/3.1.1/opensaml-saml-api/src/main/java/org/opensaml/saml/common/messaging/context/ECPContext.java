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

import org.opensaml.messaging.context.BaseContext;

/**
 * Context, usually attached to a {@link org.opensaml.messaging.context.MessageContext}
 * that carries state associated with an ECP request that is needed during response generation.
 */
public class ECPContext extends BaseContext {

    /** Whether the request was authenticated. */
    private boolean requestAuthenticated;
    
    /** Generated session key. */
    @Nullable private byte[] sessionKey;
    
    /**
     * Get whether the request from the SP was authenticated.
     * 
     * @return  true iff the SP request was authenticated
     */
    public boolean isRequestAuthenticated() {
        return requestAuthenticated;
    }
    
    /**
     * Set whether the request from the SP was authenticated.
     * 
     * @param flag flag to set
     * 
     * @return this context
     */
    @Nonnull public ECPContext setRequestAuthenticated(final boolean flag) {
        requestAuthenticated = flag;
        return this;
    }
    
    /**
     * Get the session key generated for use by the client and IdP.
     * 
     * @return the generated session key bytes
     */
    @Nullable public byte[] getSessionKey() {
        return sessionKey;
    }
    
    /**
     * Set the session key generated for use by the client and IdP.
     * 
     * @param key key to set
     * 
     * @return this context
     */
    @Nonnull public ECPContext setSessionKey(final byte[] key) {
        sessionKey = key;
        return this;
    }
    
}