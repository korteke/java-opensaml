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

package org.opensaml.xml.signature;

import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.xml.security.BaseTrustEngine;
import org.opensaml.xml.security.InlineX509KeyInfoResolver;
import org.opensaml.xml.security.KeyInfoSource;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.X509KeyInfoResolver;
import org.opensaml.xml.signature.impl.SignatureImpl;

/**
 * A signature validation and trust engine that checks the signature against keys embedded within KeyInfo elements but
 * does not perform full PKIX validation.
 */
public class BasicX509SignatureTrustEngine extends BaseTrustEngine<Signature, X509KeyInfoResolver> implements
        SignatureTrustEngine<X509KeyInfoResolver> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(BasicX509SignatureTrustEngine.class);

    /** Constructor. */
    public BasicX509SignatureTrustEngine() {
       setDefaultKeyResolver(new InlineX509KeyInfoResolver());
    }

    /** {@inheritDoc} */
    public boolean validate(Signature token, KeyInfoSource keyInfoSource, X509KeyInfoResolver keyResolver)
            throws SecurityException {
        if (log.isDebugEnabled()) {
            log.debug("Verify digital signature with against keying information");
        }

        XMLSignature signature = buildSignature(token);

        List<PublicKey> keys = getKeys(keyInfoSource, keyResolver);
        if (validate(signature, keys)) {
            return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    public boolean validate(byte[] signature, byte[] content, String sigAlg, KeyInfoSource keyInfo,
            X509KeyInfoResolver keyResolver) throws SecurityException {

        List<PublicKey> keys = getKeys(keyInfo, keyResolver);
        try {
            if (keys != null) {
                for (PublicKey key : keys) {
                    java.security.Signature signatureVerifier = java.security.Signature.getInstance(sigAlg);
                    signatureVerifier.update(content);
                    signatureVerifier.initVerify(key);
                    if (signatureVerifier.verify(signature)) {
                        return true;
                    }
                }
            }
        } catch (SignatureException e) {
            throw new SecurityException("Error evaluating signature", e);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("Signature algorithm, " + sigAlg + ", is not supported", e);
        } catch (InvalidKeyException e) {
            throw new SecurityException("Unsupported public key", e);
        }

        return false;
    }

    /**
     * Constructs an {@link XMLSignature} from the given signature object.
     * 
     * @param signature the signature
     * 
     * @return the constructed XMLSignature
     * 
     * @throws SecurityException thrown if the given signature can not be marshalled or converted into an XMLSignature
     */
    protected XMLSignature buildSignature(Signature signature) throws SecurityException {
        if (log.isDebugEnabled()) {
            log.debug("Creating XMLSignature object");
        }
        return ((SignatureImpl) signature).getXMLSignature();
    }

    /**
     * Gets the list of public keys from the given key info. The complete list of keys include any raw keys expresed at
     * KeyValue elements and the public key from any certificate who DN or subjectAltName matches the given peer name.
     * 
     * @param keyInfoSrc the key info to extract the keys from, may be null
     * @param keyResolver the resolver used to extract keys and certificates from the key info, must not be null
     * 
     * @return list of public keys given in the key info
     * 
     * @throws SecurityException thrown if the keys or certificate within the key info can not be resolved
     */
    protected List<PublicKey> getKeys(KeyInfoSource keyInfoSrc, X509KeyInfoResolver keyResolver)
            throws SecurityException {

        try {
            if (keyInfoSrc == null) {
                if (log.isDebugEnabled()) {
                    log.debug("KeyInfo source was null, attempting to resolve raw keys from resolver");
                }
                return keyResolver.resolveKeys(null);
            }

            if (log.isDebugEnabled()) {
                log.debug("Extracting keying information from KeyInfoSource for peer " + keyInfoSrc.getName());
            }
            FastList<PublicKey> keys = new FastList<PublicKey>();
            for(KeyInfo keyInfo : keyInfoSrc){

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

    /**
     * Validates the given signature against a set of keys.
     * 
     * @param signature the signature to validate
     * @param keys the keys to use during validation
     * 
     * @return true if the signature validates with any of the given keys
     * 
     * @throws SecurityException thrown if there is a problem checking the signature against a key
     */
    protected boolean validate(XMLSignature signature, List<PublicKey> keys) throws SecurityException {
        try {
            for (PublicKey key : keys) {
                if (signature.checkSignatureValue(key)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Signature validated with public key");
                    }
                    return true;
                }
            }
        } catch (XMLSignatureException e) {
            throw new SecurityException("Unable to evaluate key against signature", e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Signature did not validate against any public key");
        }
        return false;
    }
}