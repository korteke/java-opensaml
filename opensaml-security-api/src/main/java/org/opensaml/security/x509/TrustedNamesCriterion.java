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

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.Criterion;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A criterion implementation for conveying a dynamically-generated set of trusted
 * names for PKIX validation purposes.  This criterion would typically be evaluated
 * by a {@link PKIXValidationInformationResolver} that supports trusted name resolution.
 */
public class TrustedNamesCriterion implements Criterion {
    
    /** The set of trusted names. */
    private Set<String> trustedNames;
    
    /**
     * Constructor.
     *
     * @param names the set of trusted names
     */
    public TrustedNamesCriterion(@Nonnull final Set<String> names)  {
        setTrustedNames(names);
    }
    
    /**
     * Get the set of trusted names.
     * 
     * @return the set of trusted names
     */
    public Set<String> getTrustedNames() {
        return ImmutableSet.copyOf(trustedNames);
    }
    
    /**
     * Set the set of trusted names.
     * 
     * @param names the new trusted names
     */
    public void setTrustedNames(@Nullable final Set<String> names) {
        if (names == null) {
            trustedNames = Collections.emptySet();
            return;
        }
        
        trustedNames = Sets.newHashSet(StringSupport.normalizeStringCollection(names));
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TrustedNamesCriterion [names=");
        builder.append(trustedNames);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        int result = 17;  
        result = 37*result + trustedNames.hashCode();
        return result;
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof TrustedNamesCriterion) {
            TrustedNamesCriterion other = (TrustedNamesCriterion) obj;
            return trustedNames.equals(other.trustedNames);
        }

        return false;
    }

}
