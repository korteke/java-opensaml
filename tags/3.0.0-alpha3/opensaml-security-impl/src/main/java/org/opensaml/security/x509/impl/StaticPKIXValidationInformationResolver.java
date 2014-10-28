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

package org.opensaml.security.x509.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.PKIXValidationInformationResolver;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * An implementation of {@link PKIXValidationInformationResolver} which always returns a static, fixed set of
 * information.
 */
public class StaticPKIXValidationInformationResolver implements PKIXValidationInformationResolver {

    /** The PKIX validation information to return. */
    private final List<PKIXValidationInformation> pkixInfo;

    /** The set of trusted names to return. */
    private final Set<String> trustedNames;

    /**
     * Constructor.
     * 
     * @param info list of PKIX validation information to return
     * @param names set of trusted names to return
     */
    public StaticPKIXValidationInformationResolver(@Nullable final List<PKIXValidationInformation> info,
            @Nullable final Set<String> names) {
        if (info != null) {
            pkixInfo = new ArrayList<PKIXValidationInformation>(info);
        } else {
            pkixInfo = Collections.EMPTY_LIST;
        }

        if (names != null) {
            trustedNames = new HashSet<String>(names);
        } else {
            trustedNames = Collections.EMPTY_SET;
        }
    }

    /** {@inheritDoc} */
    @Nullable public Set<String> resolveTrustedNames(CriteriaSet criteriaSet) throws ResolverException {
        return trustedNames;
    }

    /** {@inheritDoc} */
    public boolean supportsTrustedNameResolution() {
        return true;
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<PKIXValidationInformation> resolve(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        return pkixInfo;
    }

    /** {@inheritDoc} */
    @Nullable public PKIXValidationInformation resolveSingle(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        if (!pkixInfo.isEmpty()) {
            return pkixInfo.get(0);
        }
        return null;
    }

}