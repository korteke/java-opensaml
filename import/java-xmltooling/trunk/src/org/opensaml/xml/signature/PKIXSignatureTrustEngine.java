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

import org.apache.log4j.Logger;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.xml.security.BasePKIXTrustEngine;
import org.opensaml.xml.security.KeyInfoSource;
import org.opensaml.xml.security.PKIXValidationInformation;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.SimpleX509EntityCredential;
import org.opensaml.xml.security.X509EntityCredential;
import org.opensaml.xml.security.X509KeyInfoResolver;
import org.opensaml.xml.signature.impl.SignatureImpl;

/**
 * A signature validation and trust engine that checks the signature against certificates embedded within KeyInfo
 * elements and then performs a full PKIX validation of the certificates within the signature.
 */
public class PKIXSignatureTrustEngine extends BasePKIXTrustEngine<Signature, X509KeyInfoResolver> implements
        SignatureTrustEngine<X509KeyInfoResolver> {

    /** Class logger. */
    private Logger log = Logger.getLogger(PKIXSignatureTrustEngine.class);

    /** {@inheritDoc} */
    public boolean validate(byte[] signature, byte[] content, String sigAlg, KeyInfoSource keyInfoSource,
            X509KeyInfoResolver keyResolver) throws SecurityException {

            X509EntityCredential entityCredential = validateSignature(signature, content, sigAlg, keyInfoSource, keyResolver);
            for (PKIXValidationInformation validationInfo : getPKIXValidationInformation(keyInfoSource.getName())) {
                if (pkixValidate(entityCredential, validationInfo)) {
                    return true;
                }
            }

            return false;
    }

    /** {@inheritDoc} */
    public boolean validate(Signature token, KeyInfoSource keyInfoSource, X509KeyInfoResolver keyResolver)
            throws SecurityException {

        X509EntityCredential credential = validateSignature(token, keyInfoSource, keyResolver);
        for (PKIXValidationInformation validationInfo : getPKIXValidationInformation(keyInfoSource.getName())) {
            if (pkixValidate(credential, validationInfo)) {
                return true;
            }
        }

        return false;
    }

    protected X509EntityCredential validateSignature(byte[] signature, byte[] content, String sigAlg,
            KeyInfoSource keyInfoSource, X509KeyInfoResolver keyResolver) throws SecurityException {
        try {
            List<PublicKey> publicKeys = null;
            List<X509Certificate> certificates = null;
            java.security.Signature signatureVerifier;
            boolean validatedSignature = false;
            KEYSOURCES: for (KeyInfo keyInfo : keyInfoSource) {
                publicKeys = keyResolver.resolveKeys(keyInfo);
                if (publicKeys != null) {
                    for (PublicKey key : publicKeys) {
                        signatureVerifier = java.security.Signature.getInstance(sigAlg);
                        signatureVerifier.update(content);
                        signatureVerifier.initVerify(key);
                        if (signatureVerifier.verify(signature)) {
                            validatedSignature = true;
                            break KEYSOURCES;
                        }
                    }
                }

                certificates = keyResolver.resolveCertificates(keyInfo);
                if (certificates != null) {
                    for (X509Certificate cert : certificates) {
                        signatureVerifier = java.security.Signature.getInstance(sigAlg);
                        signatureVerifier.update(content);
                        signatureVerifier.initVerify(cert);
                        if (signatureVerifier.verify(signature)) {
                            validatedSignature = true;
                            break KEYSOURCES;
                        }
                    }
                }

            }
            if (!validatedSignature) {
                return null;
            }

            X509Certificate entityCertificate = getEntityCertificate(keyInfoSource.getName(), certificates);
            certificates.remove(entityCertificate);
            certificates.add(0, entityCertificate);
            return new SimpleX509EntityCredential(certificates);
        } catch (KeyException e) {
            throw new SecurityException("Error to extracting public key information", e);
        } catch (CertificateException e) {
            throw new SecurityException("Error extracting certificate information", e);
        } catch (SignatureException e) {
            throw new SecurityException("Error evaluating signature", e);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("Signature algorithm, " + sigAlg + ", is not supported", e);
        }
    }

    protected X509EntityCredential validateSignature(Signature signature, KeyInfoSource keyInfoSource,
            X509KeyInfoResolver keyResolver) throws SecurityException {
        XMLSignature xmlSignature = ((SignatureImpl) signature).getXMLSignature();

        try {
            List<PublicKey> publicKeys = null;
            List<X509Certificate> certificates = null;
            boolean validatedSignature = false;
            for (KeyInfo keyInfo : keyInfoSource) {
                publicKeys = keyResolver.resolveKeys(keyInfo);
                certificates = keyResolver.resolveCertificates(keyInfo);
                if (validateSignature(xmlSignature, publicKeys, certificates)) {
                    validatedSignature = true;
                    break;
                }
            }
            if (!validatedSignature) {
                return null;
            }

            X509Certificate entityCertificate = getEntityCertificate(keyInfoSource.getName(), certificates);
            certificates.remove(entityCertificate);
            certificates.add(0, entityCertificate);
            return new SimpleX509EntityCredential(certificates);
        } catch (KeyException e) {
            throw new SecurityException("Error to extracting public key information", e);
        } catch (CertificateException e) {
            throw new SecurityException("Error extracting certificate information", e);
        }
    }

    protected boolean validateSignature(XMLSignature signature, List<PublicKey> keys, List<X509Certificate> certs)
            throws SecurityException {
        try {
            if (keys != null) {
                for (PublicKey key : keys) {
                    if (signature.checkSignatureValue(key)) {
                        return true;
                    }
                }
            }

            if (certs != null) {
                for (X509Certificate cert : certs) {
                    if (signature.checkSignatureValue(cert)) {
                        return true;
                    }
                }
            }
        } catch (XMLSignatureException e) {
            throw new SecurityException("Error while attempting to verify digital signature", e);
        }

        return false;
    }

    protected X509Certificate getEntityCertificate(String entityID, List<X509Certificate> certificates) {
        for (X509Certificate cert : certificates) {
            if (matchId(entityID, cert)) {
                return cert;
            }
        }

        return null;
    }
}