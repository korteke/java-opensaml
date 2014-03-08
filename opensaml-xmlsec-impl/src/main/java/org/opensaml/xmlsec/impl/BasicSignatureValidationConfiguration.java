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

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Basic implementation of {@link SignatureValidationConfiguration}.
 */
public class BasicSignatureValidationConfiguration implements SignatureValidationConfiguration {
    
    /** Whitelisted algorithm URIs. */
    @Nonnull @NonnullElements private Collection<String> whiteListedAlgorithmURIs;
    
    /** Blacklisted algorithm URIs. */
    @Nonnull @NonnullElements private Collection<String> blackListedAlgorithmURIs;
    
    /** The signature trust engine to use. */
    @Nullable private SignatureTrustEngine signatureTrustEngine;
    
    //TODO chaining to parent config instance on getters? or use a wrapping proxy, etc?
    
    /** Constructor. */
    public BasicSignatureValidationConfiguration() {
        whiteListedAlgorithmURIs = Collections.emptyList();
        blackListedAlgorithmURIs = Collections.emptyList();
    }
    
    /**
     * Get the list of whitelisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getWhitelistedAlgorithmURIs() {
        return ImmutableList.copyOf(whiteListedAlgorithmURIs);
    }
    
    /**
     * Set the list of whitelisted algorithm URI's.
     * 
     * @param uris the list of algorithms
     */
    public void setWhitelistedAlgorithmURIs(@Nonnull @NonnullElements final Collection<String> uris) {
        Constraint.isNotNull(uris, "Whitelist cannot be null");
        whiteListedAlgorithmURIs = Lists.newArrayList(Collections2.filter(uris, Predicates.notNull()));
    }
    
    /**
     * Get the list of blacklisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getBlacklistedAlgorithmsURIs() {
        return ImmutableList.copyOf(blackListedAlgorithmURIs);
    }
    
    /**
     * Set the list of blacklisted algorithm URI's.
     * 
     * @param uris the list of algorithms
     */
    public void setBlacklistedAlgorithmURIs(@Nonnull @NonnullElements final Collection<String> uris) {
        Constraint.isNotNull(uris, "Blacklist cannot be null");
        blackListedAlgorithmURIs = Lists.newArrayList(Collections2.filter(uris, Predicates.notNull()));
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