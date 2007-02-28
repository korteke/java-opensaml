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
 * 
 * @param <CredentialType> the type of credential produced by this resolver
 */
public interface CredentialResolver<CredentialType extends Credential> {

    /**
     * Gets the credential for the given entity.
     * 
     * @param entity ID of the entity
     * @param usage usage type of the credential
     * 
     * @return entity's credential or null
     * 
     * @throws SecurityException thrown if the credential can not be resolved
     */
    public CredentialType resolveCredential(String entity, UsageType usage) throws SecurityException;
}