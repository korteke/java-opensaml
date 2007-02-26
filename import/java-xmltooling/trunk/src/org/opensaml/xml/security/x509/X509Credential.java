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

package org.opensaml.xml.security.x509;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.opensaml.xml.security.credential.Credential;

/**
 * An entity credential based on PKI.
 */
public interface X509Credential extends Credential {

    /** DSA key algorithim identifier. */
    public static final String DSA_KEY_ALGORITHM = "DSA";

    /** RSA key algorithim identifier. */
    public static final String RSA_KEY_ALGORITHM = "RSA";

    /**
     * Gets the public certificate for the entity.
     * 
     * @return the public certificate for the entity
     */
    public X509Certificate getEntityCertificate();

    /**
     * Gets an immutable collection of certificates in the entity's trust chain. The entity certificate is contained
     * within this list. No specific ordering of the certificates is guaranteed.
     * 
     * @return entities certificate chain
     */
    public Collection<X509Certificate> getEntityCertificateChain();

    /**
     * Gets a collection of CRLs associated with the credential.
     * 
     * @return CRLs associated with the credential
     */
    public Collection<X509CRL> getCRLs();
}