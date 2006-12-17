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

import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.log4j.Logger;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.KeyInfoHelper;

/**
 * A simple key resolver that extracts either public DSA or RSA key from the KeyInfo's KeyValue element. Key value
 * elements that can not be resolved are ignored.
 * 
 * If more than one key is present the "primary" key is simply the first one listed in the KeyInfo.
 * 
 * TODO implement RetrivalMethod following once XMLObjects may be found by ID attribute
 */
public class InlineX509KeyInfoResolver implements X509KeyInfoResolver {

    /** Class logger. */
    private static Logger log = Logger.getLogger(InlineX509KeyInfoResolver.class);

    /** {@inheritDoc} */
    public PublicKey resolveKey(KeyInfo keyInfo) throws KeyException {
        List<PublicKey> keys = resolveKeys(keyInfo);

        if (keys == null || keys.size() < 1) {
            return null;
        }

        return keys.get(0);
    }
    
    /** {@inheritDoc} */
    public List<String> resolveKeyNames(KeyInfo keyInfo) {
        return KeyInfoHelper.getKeyNames(keyInfo);
    }

    /** {@inheritDoc} */
    public List<PublicKey> resolveKeys(KeyInfo keyInfo) throws KeyException {
        return KeyInfoHelper.getPublicKeys(keyInfo);
    }
    
    /** {@inheritDoc} */
    public List<X509Certificate> resolveCertificates(KeyInfo keyInfo) throws CertificateException{
        return KeyInfoHelper.getCertificates(keyInfo);
    }
    
    /** {@inheritDoc} */
    public List<X509CRL> resolveCRLS(KeyInfo keyInfo) throws CRLException{
        return KeyInfoHelper.getCRLs(keyInfo);
    }
}