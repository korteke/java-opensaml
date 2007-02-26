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

package org.opensaml.xml.security.trust;

import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;

/**
 * Evaluates the trustworthiness and validity of a token against implementation-specific requirements.
 * 
 * @param <TokenType> the token type this trust engine evaluates
 * @param <TrustedCredentialType> trusted credential information the given token will be checked against
 */
public interface TrustEngine<TokenType, TrustedCredentialType extends Credential> {

    /**
     * Validates the token against the default key info using the default key resolver.
     * 
     * @param token security token to validate
     * @param trustedCredential information the given token will be checked against
     * 
     * @return true if the token is trusted and valid, false if not
     * 
     * @throws SecurityException thrown if there is a problem validating the security token
     */
    public boolean validate(TokenType token, TrustedCredentialType trustedCredential) throws SecurityException;
}