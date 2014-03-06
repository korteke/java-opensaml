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

package org.opensaml.xmlsec.signature.support;


import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.Criterion;

import org.opensaml.xmlsec.SignatureValidationParameters;

/**
 * Criterion which holds an instance of {@link SignatureValidationParameters}.
 * 
 * <p>This criterion is often used with implementations of the {@link SignatureTrustEngine}.</p>
 */
public class SignatureValidationParametersCriterion implements Criterion {
    
    /** The SignatureValidationParameters instance. */
    private SignatureValidationParameters params;
    
    /**
     * Constructor.
     *
     * @param validationParams the signature validation parameters instance to wrap
     */
    public SignatureValidationParametersCriterion(@Nonnull final SignatureValidationParameters validationParams) {
       params = Constraint.isNotNull(validationParams, "SignatureValidationParameters instance was null"); 
    }
    
    /**
     * Get the signature validation parameters instance.
     * 
     * @return the parameters instance
     */
    @Nonnull public SignatureValidationParameters getSignatureValidationParameters() {
        return params;
    }
    
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SignatureValidationParametersCriterion [params=");
        builder.append(params);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return params.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof SignatureValidationParametersCriterion) {
            return params.equals(((SignatureValidationParametersCriterion) obj).getSignatureValidationParameters());
        }

        return false;
    }

}
