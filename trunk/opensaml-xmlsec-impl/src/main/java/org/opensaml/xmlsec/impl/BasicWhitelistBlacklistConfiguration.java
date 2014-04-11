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

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.xmlsec.WhitelistBlacklistConfiguration;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Basic implementation of {@link WhitelistBlacklistConfiguration}.
 * 
 * <p>
 * The value returned by {@link #getWhitelistBlacklistPrecedence()} defaults to {@link Precedence#WHITELIST}.
 * </p>
 */
public class BasicWhitelistBlacklistConfiguration implements WhitelistBlacklistConfiguration {
    
    /** Default precedence. */
    public static final Precedence DEFAULT_PRECEDENCE = Precedence.WHITELIST;
    
    /** Whitelisted algorithm URIs. */
    private Collection<String> whitelist;
    
    /** Whitelist merge flag. */
    private boolean whitelistMerge;
    
    /** Blacklisted algorithm URIs. */
    private Collection<String> blacklist;
    
    /** Blacklist merge flag. */
    private boolean blacklistMerge;
    
    /** Precedence flag. */
    private Precedence precedence;
    
    /** Constructor. */
    public BasicWhitelistBlacklistConfiguration() {
        whitelist = Collections.emptyList();
        blacklist = Collections.emptyList();
        precedence = DEFAULT_PRECEDENCE;
    }
    
    /**
     * Get the list of whitelisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getWhitelistedAlgorithmURIs() {
        return ImmutableList.copyOf(whitelist);
    }
    
    /**
     * Set the list of whitelisted algorithm URI's.
     * 
     * @param uris the list of algorithms
     */
    public void setWhitelistedAlgorithmURIs(@Nonnull @NonnullElements final Collection<String> uris) {
        Constraint.isNotNull(uris, "Whitelist may not be null");
        whitelist = Lists.newArrayList(Collections2.filter(uris, Predicates.notNull()));
    }

    /** {@inheritDoc} */
    public boolean isWhitelistMerge() {
        return whitelistMerge;
    }
    
    /**
     * Set the flag indicating whether to merge this configuration's whitelist with one of a lower order of precedence,
     * or to treat this whitelist as authoritative.
     * 
     * @param flag true if should merge, false otherwise
     */
    public void setWhitelistMerge(boolean flag) {
        whitelistMerge = flag;
    }

    /**
     * Get the list of blacklisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getBlacklistedAlgorithmsURIs() {
        return ImmutableList.copyOf(blacklist);
    }
    
    /**
     * Set the list of blacklisted algorithm URI's.
     * 
     * @param uris the list of algorithms
     */
    public void setBlacklistedAlgorithmURIs(@Nonnull @NonnullElements final Collection<String> uris) {
        Constraint.isNotNull(uris, "Blacklist may not be null");
        blacklist = Lists.newArrayList(Collections2.filter(uris, Predicates.notNull()));
    }

    /** {@inheritDoc} */
    public boolean isBlacklistMerge() {
        return blacklistMerge;
    }

    /**
     * Set the flag indicating whether to merge this configuration's blacklist with one of a lower order of precedence,
     * or to treat this blacklist as authoritative.
     * 
     * @param flag true if should merge, false otherwise
     */
    public void setBlacklistMerge(boolean flag) {
        blacklistMerge = flag;
    }

    /** {@inheritDoc} */
    @Nonnull public Precedence getWhitelistBlacklistPrecedence() {
        return precedence;
    }
    
    /**
     * Set preference value indicating which should take precedence when both whitelist and blacklist are non-empty.
     * 
     * @param value the precedence value
     */
    public void setWhitelistBlacklistPrecedence(@Nonnull Precedence value) {
        precedence = Constraint.isNotNull(value, "Precedence may not be null");
    }

}
