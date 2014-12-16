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

package org.opensaml.security.x509;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Resolver;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * A resolver which uses {@link net.shibboleth.utilities.java.support.resolver.Criterion} to resolve
 * {@link PKIXValidationInformation}, which will typically be used by PKIX-based trust engines.
 * 
 * Implementations must also implement {@link #resolveTrustedNames(CriteriaSet)}, which will 
 * return a set of trusted names associated with the entity implied by the criteria.  These trusted names
 * may be used to validate (in an application-specific manner) that an entity is trusted to wield a particular
 * certificate.
 */
public interface PKIXValidationInformationResolver extends Resolver<PKIXValidationInformation, CriteriaSet> {
    
    /**
     * Resolve a set of trusted names associated with the entity indicated by the criteria.  This method
     * is optional to implement.
     * 
     * @param criteriaSet set of criteria used to determine or resolve the trusted names
     * @return the set of certificate names trusted for an entity
     * @throws ResolverException thrown if there is an error resolving the trusted names
     * @throws UnsupportedOperationException thrown if this optional method is not supported by the implementation
     */
    @Nonnull public Set<String> resolveTrustedNames(@Nullable final CriteriaSet criteriaSet)
        throws ResolverException;
    
    /**
     * Check whether resolution of trusted names is supported.
     * 
     * @return true if the implementation supports resolution of trusted names, otherwise false
     */
    public boolean supportsTrustedNameResolution();

}