/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.security;

/**
 * Evaluates the trustworthiness and validity of an entity's credential against implementation-specific requirements.
 */
public interface EntityCredentialTrustEngine<CredentialType extends EntityCredential> {
    
    /**
     * Validates the given entity credential against keying information expressed in the given key info source.
     * 
     * @param credential credential to validate
     * @param keyInfo keying information source
     * @param keyResolver key resolver used to extracts keys from keying information
     * 
     * @return true if the given credential is valid, false if not
     */
    public boolean validate(EntityCredential credential, KeyInfoSource keyInfo, KeyResolver keyResolver);
}