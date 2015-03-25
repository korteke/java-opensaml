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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import com.google.common.collect.ImmutableSet;

/**
 * The whitelist and blacklist algorithm parameters.
 */
public class WhitelistBlacklistParameters {
    
    /** Whitelisted algorithm URIs. */
    @Nonnull @NonnullElements private Collection<String> whiteListedAlgorithmURIs;
    
    /** Blacklisted algorithm URIs. */
    @Nonnull @NonnullElements private Collection<String> blackListedAlgorithmURIs;
        
    /** Constructor. */
    public WhitelistBlacklistParameters() {
        whiteListedAlgorithmURIs = Collections.emptySet();
        blackListedAlgorithmURIs = Collections.emptySet();
    }
    
    /**
     * Get the list of whitelisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getWhitelistedAlgorithms() {
        return ImmutableSet.copyOf(whiteListedAlgorithmURIs);
    }
    
    /**
     * Set the list of whitelisted algorithm URI's.
     * 
     * @param uris the list of algorithms
     */
    public void setWhitelistedAlgorithms(@Nullable final Collection<String> uris) {
        if (uris == null) {
            whiteListedAlgorithmURIs = Collections.emptySet();
            return;
        }
        whiteListedAlgorithmURIs = new HashSet<>(StringSupport.normalizeStringCollection(uris));
    }
    
    /**
     * Get the list of blacklisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getBlacklistedAlgorithms() {
        return ImmutableSet.copyOf(blackListedAlgorithmURIs);
    }
    
    /**
     * Set the list of blacklisted algorithm URI's.
     * 
     * @param uris the list of algorithms
     */
    public void setBlacklistedAlgorithms(@Nonnull @NonnullElements final Collection<String> uris) {
        if (uris == null) {
            blackListedAlgorithmURIs = Collections.emptySet();
            return;
        }
        blackListedAlgorithmURIs = new HashSet<>(StringSupport.normalizeStringCollection(uris));
    }
    
}