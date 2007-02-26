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

import java.util.Set;

import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialResolver;

/**
 * A {@link CredentialResolver} that pulls credential information from the classpath.
 * 
 * TODO
 */
public class ClasspathCredentialResolver implements CredentialResolver {

    /**
     * Constructor.
     * 
     * @param entityId ID of the entity this credential is for
     * @param keyPath path to private key
     * @param keyPass private key password
     * @param certPath path to the certificate file or directory
     * @param crlPath path to the CRL file or directory
     */
    public ClasspathCredentialResolver(String entityId, String keyPath, String keyPass, String certPath, String crlPath) {

    }
    
    /** {@inheritDoc} */
    public Set getEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public Credential resolveCredential(String entity) {
        // TODO Auto-generated method stub
        return null;
    }

}
