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

package org.opensaml.security.trust.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.security.SecurityException;
import org.opensaml.security.trust.TrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Evaluate a token in sequence using a chain of subordinate trust engines. If the token may be established as trusted
 * by any of the subordinate engines, the token is considered trusted. Otherwise it is considered untrusted.
 * 
 * @param <TokenType> the token type this trust engine evaluates
 */
public class ChainingTrustEngine<TokenType> implements TrustEngine<TokenType> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ChainingTrustEngine.class);

    /** The chain of subordinate trust engines. */
    private List<TrustEngine<TokenType>> engines;

    /** 
     * Constructor.
     * 
     * @param chain the list of trust engines in the chain
     */
    public ChainingTrustEngine(@Nonnull final List<TrustEngine<TokenType>> chain) {
        Constraint.isNotNull(chain, "TrustEngine list was null");
        engines = Lists.newArrayList(Collections2.filter(chain, Predicates.notNull()));
    }

    /**
     * Get the list of configured trust engines which constitute the trust evaluation chain.
     * 
     * @return the modifiable list of trust engines in the chain
     */
    @Nonnull  @NonnullElements @Unmodifiable @NotLive public List<TrustEngine<TokenType>> getChain() {
        return ImmutableList.copyOf(engines);
    }

    /** {@inheritDoc} */
    public boolean validate(@Nonnull final TokenType token, @Nullable final CriteriaSet trustBasisCriteria)
            throws SecurityException {
        for (TrustEngine<TokenType> engine : engines) {
            if (engine.validate(token, trustBasisCriteria)) {
                log.debug("Token was trusted by chain member: {}", engine.getClass().getName());
                return true;
            }
        }
        return false;
    }

}