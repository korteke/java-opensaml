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
 * Abstract base class for {@link CredentialResolver} implementations.
 * 
 * @param <CredentialType> the type of credential produced by this resolver
 */
public abstract class AbstractCredentialResolver<CredentialType extends Credential> 
    implements CredentialResolver<CredentialType> {

    /** {@inheritDoc} */
    public CredentialType resolveCredential(CredentialCriteria criteria) throws SecurityException {
        Iterable<CredentialType> creds = resolveCredentials(criteria);
        if (creds.iterator().hasNext()) {
            return creds.iterator().next();
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    public abstract Iterable<CredentialType> resolveCredentials(CredentialCriteria criteria) throws SecurityException;

}
