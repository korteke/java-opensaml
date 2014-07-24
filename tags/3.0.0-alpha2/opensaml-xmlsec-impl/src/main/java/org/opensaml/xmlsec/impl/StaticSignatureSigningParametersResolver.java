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

import java.util.Collections;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureSigningParametersResolver;

/**
 * Resolve an instance of {@link SignatureSigningParameters} statically.
 */
public class StaticSignatureSigningParametersResolver implements SignatureSigningParametersResolver {
    
    /** Static parameters. */
    private SignatureSigningParameters params;
    
    /**
     * Constructor.
     *
     * @param parameters the static parameters instance to return
     */
    public StaticSignatureSigningParametersResolver(SignatureSigningParameters parameters) {
        params = Constraint.isNotNull(parameters, "Parameters instance may not be null");
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<SignatureSigningParameters> resolve(CriteriaSet criteria) throws ResolverException {
        return Collections.singleton(params);
    }

    /** {@inheritDoc} */
    @Nonnull public SignatureSigningParameters resolveSingle(CriteriaSet criteria) throws ResolverException {
        return params;
    }

}
