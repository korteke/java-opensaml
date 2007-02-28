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
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.impl.SignatureImpl;

/**
 * A signature validation and trust engine that checks the signature against keys in a trusted credential's public keys
 * or entity certificate.
 */
public class BasicX509SignatureTrustEngine implements SignatureTrustEngine<X509Credential> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(BasicX509SignatureTrustEngine.class);

    /** Constructor. */
    public BasicX509SignatureTrustEngine() {

    }

    /** {@inheritDoc} */
    public boolean validate(Signature token, X509Credential trustedCredential) throws SecurityException {
        if (log.isDebugEnabled()) {
            log.debug("Verify digital signature with against keying information");
        }

        XMLSignature signature = buildSignature(token);

        Collection<PublicKey> trustedKeys = getKeys(trustedCredential);
        if(trustedKeys == null || trustedKeys.size() < 1){
            return false;
        }
        
        try {
            for (PublicKey trustedKey : trustedKeys) {
                if (signature.checkSignatureValue(trustedKey)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Signature validated with public key from credential for entity "
                                + trustedCredential.getEntityId());
                    }
                    return true;
                }
            }
        } catch (XMLSignatureException e) {
            throw new SecurityException("Unable to evaluate key against signature", e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Signature did not validate against any public keyfor entity " + trustedCredential.getEntityId());
        }
        return false;
    }

    /** {@inheritDoc} */
    public boolean validate(byte[] signature, byte[] content, String sigAlg, X509Credential trustedCredential)
            throws SecurityException {

        Collection<PublicKey> trustedKeys = getKeys(trustedCredential);
        if(trustedKeys == null || trustedKeys.size() < 1){
            return false;
        }
        
        try {
            for (PublicKey trustedKey : trustedKeys) {
                java.security.Signature signatureVerifier = java.security.Signature.getInstance(sigAlg);
                signatureVerifier.update(content);
                signatureVerifier.initVerify(trustedKey);
                if (signatureVerifier.verify(signature)) {
                    return true;
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
     * @param credential the credential to extract keys from
     * 
     * @return list of public keys given in the key info
     * 
     * @throws SecurityException thrown if the keys or certificate within the key info can not be resolved
     */
    protected Collection<PublicKey> getKeys(X509Credential credential) throws SecurityException {
        ArrayList<PublicKey> publicKeys = new ArrayList<PublicKey>(credential.getPublicKeys());
        if (credential.getEntityCertificate() != null) {
            publicKeys.add(credential.getEntityCertificate().getPublicKey());
        }

        return publicKeys;
    }
}