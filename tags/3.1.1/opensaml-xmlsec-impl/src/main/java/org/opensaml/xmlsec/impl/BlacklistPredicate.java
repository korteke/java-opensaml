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
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * Predicate which implements an algorithm URI blacklist policy.
 */
public class BlacklistPredicate implements Predicate<String> {
    
    /** Blacklisted algorithms. */
    private Collection<String> blacklist;
    
    /**
     * Constructor.
     *
     * @param algorithms collection of blacklisted algorithms
     */
    public BlacklistPredicate(@Nonnull Collection<String> algorithms) {
        Constraint.isNotNull(algorithms, "Blacklist may not be null");
        blacklist = new HashSet<>(Collections2.filter(algorithms, Predicates.notNull()));
    }

    /** {@inheritDoc} */
    public boolean apply(@Nullable String input) {
        if (input == null) {
            throw new IllegalArgumentException("Algorithm URI to evaluate may not be null");
        }
        return ! blacklist.contains(input);
    }
    
}