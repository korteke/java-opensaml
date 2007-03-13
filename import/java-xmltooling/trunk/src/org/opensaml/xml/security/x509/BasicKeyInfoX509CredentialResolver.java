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

package org.opensaml.xml.security.x509;


import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.AbstractCredentialResolver;
import org.opensaml.xml.security.credential.CredentialCriteria;
import org.opensaml.xml.security.credential.KeyInfoCredentialCriteria;
import org.opensaml.xml.security.credential.KeyInfoCredentialResolver;

/**
 * A basic implementation of {@link KeyInfoCredentialResolver} which supports extraction of credentials
 * based on public keys and X.509 certificates held directly within a {@link KeyInfo} element.
 */
public class BasicKeyInfoX509CredentialResolver extends AbstractCredentialResolver<KeyInfoX509Credential> 
    implements KeyInfoCredentialResolver<KeyInfoX509Credential> {

    /** {@inheritDoc} */
    public Iterable<KeyInfoX509Credential> resolveCredentials(CredentialCriteria criteria) throws SecurityException {
        
        if ( ! (criteria instanceof KeyInfoCredentialCriteria)) {
            throw new SecurityException("CredentialCriteria was not an instance of KeyInfoCredentialCriteria");
        }
        KeyInfoCredentialCriteria kiCriteria = (KeyInfoCredentialCriteria) criteria;
        
        // TODO Auto-generated method stub
        
        // factor out generation of credentials and basic KeyInfo parsing so can be used by subclasses.
        
        return null;
    }

}
