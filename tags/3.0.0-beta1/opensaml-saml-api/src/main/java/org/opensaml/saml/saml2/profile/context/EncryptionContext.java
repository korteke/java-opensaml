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

package org.opensaml.saml.saml2.profile.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.xmlsec.EncryptionParameters;

/**
 * Context supplying encryption parameters for the different forms of SAML XML encryption.
 * 
 * <p>The presence or absence of a particular parameter set is generally an indicator as to
 * the requirement to encrypt that type of object.</p>
 */
public class EncryptionContext extends BaseContext {

    /** Parameters for assertion encrytion. */
    @Nullable private EncryptionParameters assertionEncParams;

    /** Parameters for identifier encrytion. */
    @Nullable private EncryptionParameters idEncParams;

    /** Parameters for attribute encrytion. */
    @Nullable private EncryptionParameters attributeEncParams;
    
    /**
     * Get the encryption parameters to use for encryption of assertions.
     * 
     * @return parameters to use
     */
    @Nullable public EncryptionParameters getAssertionEncryptionParameters() {
        return assertionEncParams;
    }
    
    /**
     * Set the encryption parameters to use for encryption of assertions.
     * 
     * @param params parameters to use
     * 
     * @return this context
     */
    @Nonnull public EncryptionContext setAssertionEncryptionParameters(@Nullable final EncryptionParameters params) {
        assertionEncParams = params;
        return this;
    }

    /**
     * Get the encryption parameters to use for encryption of identifiers.
     * 
     * @return parameters to use
     */
    @Nullable public EncryptionParameters getIdentifierEncryptionParameters() {
        return idEncParams;
    }
    
    /**
     * Set the encryption parameters to use for encryption of identifiers.
     * 
     * @param params parameters to use
     * 
     * @return this context
     */
    @Nonnull public EncryptionContext setIdentifierEncryptionParameters(@Nullable final EncryptionParameters params) {
        idEncParams = params;
        return this;
    }

    /**
     * Get the encryption parameters to use for encryption of attributes.
     * 
     * @return parameters to use
     */
    @Nullable public EncryptionParameters getAttributeEncryptionParameters() {
        return attributeEncParams;
    }
    
    /**
     * Set the encryption parameters to use for encryption of attributes.
     * 
     * @param params parameters to use
     * 
     * @return this context
     */
    @Nonnull public EncryptionContext setAttributeEncryptionParameters(@Nullable final EncryptionParameters params) {
        attributeEncParams = params;
        return this;
    }

}