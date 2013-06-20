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

package org.opensaml.xmlsec;

import java.util.List;

import javax.annotation.Nullable;

import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;

/**
 * The effective parameters to use when validating an XML signature.
 */
public class SignatureValidationParameters {
    
    /** Whitelisted algorithm URIs. */
    private List<String> whiteListedAlgorithmURIs;
    
    /** Blacklisted algorithm URIs. */
    private List<String> blackListedAlgorithmURIs;
    
    /** The signature trust engine to use. */
    private SignatureTrustEngine signatureTrustEngine;
    
    /**
     * Get the list of whitelisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nullable public List<String> getWhitelistedAlgorithmURIs() {
        return whiteListedAlgorithmURIs;
    }
    
    /**
     * Set the list of whitelisted algorithm URI's.
     * 
     * @param uris the list of algorithms
     */
    public void setWhitelistedAlgorithmURIs(@Nullable final List<String> uris) {
        whiteListedAlgorithmURIs = uris;
    }
    
    /**
     * Get the list of blacklisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nullable public List<String> getBlacklistedAlgorithmsURIs() {
        return blackListedAlgorithmURIs;
    }
    
    /**
     * Set the list of blacklisted algorithm URI's.
     * 
     * @param uris the list of algorithms
     */
    public void setBlacklistedAlgorithmURIs(@Nullable final List<String> uris) {
        blackListedAlgorithmURIs = uris;
    }
    
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