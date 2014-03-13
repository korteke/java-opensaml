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

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;

/**
 * Algorithm URI whitelist and blacklist configuration.
 */
public interface WhitelistBlacklistConfiguration {
    
    /** Whitelist/blacklist precedence values. */
    public enum Precedence {
        /** Whitelist takes precedence over blacklist. */
        WHITELIST,
        
        /** Blacklist takes precedence over whitelist. */
        BLACKLIST
    }
    
    /**
     * Get the list of whitelisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public Collection<String> getWhitelistedAlgorithmURIs();
    
    /**
     * Flag indicating whether to merge this configuration's whitelist with one of a lower order of precedence,
     * or to treat this whitelist as authoritative.
     * 
     * @return true if should merge, false otherwise
     */
    public boolean isWhitelistMerge();
    
    /**
     * Get the list of blacklisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public Collection<String> getBlacklistedAlgorithmsURIs();
    
    /**
     * Flag indicating whether to merge this configuration's blacklist with one of a lower order of precedence,
     * or to treat this blacklist as authoritative.
     * 
     * @return true if should merge, false otherwise
     */
    public boolean isBlacklistMerge();
    
    /**
     * Get preference value indicating which should take precedence when both whitelist and blacklist are non-empty.
     * 
     * @return the configured precedence value.
     */
    @Nonnull public Precedence getWhitelistBlacklistPrecedence();

}