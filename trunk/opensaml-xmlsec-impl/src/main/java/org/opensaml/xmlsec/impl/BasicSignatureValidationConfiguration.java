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

package org.opensaml.xmlsec.impl;

import javax.annotation.Nullable;

import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;

/**
 * Basic implementation of {@link SignatureValidationConfiguration}.
 */
public class BasicSignatureValidationConfiguration extends BasicWhitelistBlacklistConfiguration
        implements SignatureValidationConfiguration {
    
    /** The signature trust engine to use. */
    @Nullable private SignatureTrustEngine signatureTrustEngine;
    
    //TODO chaining to parent config instance on getters? or use a wrapping proxy, etc?
    
    /**
     * Get the signature trust engine to use.
     * 
     * @return the signature trust engine
     */
    @Nullable public SignatureTrustEngine getSignatureTrustEngine() {
        return signatureTrustEngine;
    }

    /**
     * Set the signature trust engine to use.
     * 
     * @param engine the signature trust engine
     */
    public void setSignatureTrustEngine(@Nullable final SignatureTrustEngine engine) {
        signatureTrustEngine = engine;
    }

}