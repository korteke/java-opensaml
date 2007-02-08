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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.opensaml.xml.signature.KeyInfo;

/**
 * Trust engine that evaluates X509 credentials aginst key expressed within a given{@link KeyInfoSource}. A credential
 * is valid if its public key or the public key of its entity certificate matches any of the public keys produced by the
 * given key resolver or the public keys of any of the certificates produced by the key resolver.
 */
public class BasicX509EntityCredentialTrustEngine extends BaseTrustEngine<X509EntityCredential, X509KeyInfoResolver>
        implements EntityCredentialTrustEngine<X509EntityCredential, X509KeyInfoResolver> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(BasicX509EntityCredentialTrustEngine.class);

    /** {@inheritDoc} */
    public boolean validate(X509EntityCredential token, KeyInfoSource keyInfoSource, X509KeyInfoResolver keyResolver)
            throws SecurityException {

        if (log.isDebugEnabled()) {
            log.debug("Validating X509 credential for entity " + token.getEntityID());
        }

        List<PublicKey> credentialKeys = getKeys(token);

        List<PublicKey> trustedKeys = getKeys(keyInfoSource, keyResolver);
        if (trustedKeys == null) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to validate X509 entity credential, no trust keys available");
            }
            return false;
        }

        for (PublicKey trustedKey : trustedKeys) {
            for (PublicKey credentialKey : credentialKeys) {
                if (trustedKey.equals(credentialKey)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Validated X509 credential for entity " + token.getEntityID()
                                + " against trusted public keys");
                    }
                    return true;
                }
            }            
        }

        if (log.isDebugEnabled()) {
            log.debug("X509 credential for entity " + token.getEntityID()
                    + " did not validated against any trusted keys");
        }

        return false;
    }

    /**
     * Gets the list public keys contained within an X509EntityCredential.
     * 
     * @param token an X509EntityCredential
     * @return a list of public keys contained within the credential
     */
    protected List<PublicKey> getKeys(X509EntityCredential token) {
        List<PublicKey> credentialKeys = new FastList<PublicKey>();
        credentialKeys.add(token.getPublicKey());
        credentialKeys.add(token.getEntityCertificate().getPublicKey());
        return credentialKeys;
    }

    /**
     * Gets the list of public keys from the given key info. The complete list of keys include any raw keys expresed at
     * KeyValue elements and the public key from any certificate who DN or subjectAltName matches the given peer name.
     * 
     * @param keyInfoSource the key info to extract the keys from, may be null
     * @param keyResolver the resolver used to extract keys and certificates from the key info, must not be null
     * 
     * @return list of public keys given in the key info
     * 
     * @throws SecurityException thrown if the keys or certificate within the key info can not be resolved
     */
    protected List<PublicKey> getKeys(KeyInfoSource keyInfoSource, X509KeyInfoResolver keyResolver)
            throws SecurityException {

        try {
            if (keyInfoSource == null) {
                if (log.isDebugEnabled()) {
                    log.debug("KeyInfo source was null, attempting to resolve raw keys from resolver");
                }
                return keyResolver.resolveKeys(null);
            }

            if (log.isDebugEnabled()) {
                log.debug("Extracting keying information from KeyInfoSource for peer " + keyInfoSource.getName());
            }
            FastList<PublicKey> keys = new FastList<PublicKey>();
            for(KeyInfo keyInfo : keyInfoSource){
                if (log.isDebugEnabled()) {
                    log.debug("Extracting raw keys from key info");
                }
                keys.addAll(keyResolver.resolveKeys(keyInfo));

                List<X509Certificate> certs = keyResolver.resolveCertificates(keyInfo);
                keys.addAll(getKeys(certs));
            }
            return keys;
        } catch (KeyException e) {
            throw new SecurityException("Unable to parse raw key information", e);
        } catch (CertificateException e) {
            throw new SecurityException("Unable to parse ceritificate information", e);
        }
    }

    /**
     * Gets the public keys from a list of certificates. If the given peer name is not null only those certificates
     * whose CN component of their DN, DNS subjectAltName, or URI subjectAltName will be used.
     * 
     * @param certs certificate list to extract keys from
     * 
     * @return extracted public keys
     */
    protected List<PublicKey> getKeys(List<X509Certificate> certs) {
        FastList<PublicKey> keys = new FastList<PublicKey>();

        if (certs != null) {
            if (log.isDebugEnabled()) {
                log.debug("Extracting public keys from certificates from key info");
            }
            for (X509Certificate cert : certs) {
                keys.add(cert.getPublicKey());
            }
        }

        return keys;
    }
}