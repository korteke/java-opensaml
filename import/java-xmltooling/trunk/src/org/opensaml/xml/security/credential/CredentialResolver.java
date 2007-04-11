/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.security.credential;


import org.opensaml.xml.security.SecurityException;

/**
 * A manager for looking up credentials for a given entity.
 */
public interface CredentialResolver {

    /**
     * Gets a set of credentials that satisfy the specified credential criteria.
     * 
     * @param criteriaSet  the criteria set used for resolving the credential
     * 
     * @return an {@link Iterable} of credentials, possibly empty if no credentials were found that
     *          satisfy the given criteria
     * 
     * @throws SecurityException thrown if no credentials can be resolved
     */
    public Iterable<Credential> resolveCredentials(CredentialCriteriaSet criteriaSet) throws SecurityException;
    
    /**
     * Gets a single credential which satisfies the specified credential criteria.
     * 
     * If multiple credentials are found which satisfy the criteria, the resolver
     * may choose any mechanism to select the one to return.  Implementations should
     * override this method to meet their specific needs.
     * 
     * @param criteriaSet  the criteria set used for resolving the credential
     * 
     * @return a credential, or null if no credential was found that satisfies the given criteria
     * 
     * @throws SecurityException thrown if the credential can not be resolved
     */
    public Credential resolveCredential(CredentialCriteriaSet criteriaSet) throws SecurityException;
    
}