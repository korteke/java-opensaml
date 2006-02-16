/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;

/**
 * A data construct containing the information needed to digitally sign an XML Node.  Default algorithms are:
 * <ul>
 *   <li>Exlusive, omit comment, canonicalization</li>
 *   <li>HMAC-SHA1 message digest</li>
 *   <li>RSA-SHA1 signature algorithm</li>
 * </ul>
 */
public class SigningContext {

    /** Method used to canonicalize the XML */
    private String canonicalizationMethod;
    
    /** The signature algorithim */
    private String signatureAlgorithim;

    /** The digest algorithim */
    private String digestAlgorithim;

    /** The signing key */
    private PrivateKey signingKey;

    /** Public key to validate signature with */
    private PublicKey publicKey;

    /** The certificates to be included with the signature */
    private Set<X509Certificate> certs;
    
    /** Transforms applied to content to be signed */
    private List<String> transforms;

    /**
     * Constructor
     */
    public SigningContext() throws NullPointerException{
        canonicalizationMethod = Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS;
        signatureAlgorithim = XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1;
        digestAlgorithim = MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA1;
        transforms = new ArrayList<String>();
    }

    /**
     * Gets the certificates to be included with the signature.
     * 
     * @return the certificates to be included with the signature
     */
    public Set<X509Certificate> getCerts() {
        return certs;
    }

    /**
     * Sets the certificates to be included with the signature.
     * 
     * @param certs the certificates to be included with the signature
     */
    public void setCerts(Set<X509Certificate> certs) {
        this.certs = certs;
    }
    
    /**
     * Gets the canonicalization algorithim used to construct the signature.
     * 
     * @return the canonicalization algorithim used to construct the signature
     */
    public String getCanonicalizationAlgorithm() {
        return canonicalizationMethod;
    }
    
    /**
     * Sets the canonicalization algorithim used to construct the signature.
     * 
     * @param newAlgorithm the canonicalization algorithim used to construct the signature
     */
    public void setCanonicalizationAlgortihm(String newAlgorithm) {
        canonicalizationMethod = newAlgorithm;
    }

    /**
     * Gets the digest algorithim used in creating the signature.
     * 
     * @return the digest algorithim used in creating the signature
     */
    public String getDigestAlgorithm() {
        return digestAlgorithim;
    }

    /**
     * Sets the digest algorithim used in creating the signature.
     * 
     * @param digestAlgorithim the digest algorithim used in creating the signature
     */
    public void setDigestAlgorithm(String digestAlgorithim) {
        this.digestAlgorithim = digestAlgorithim;
    }

    /**
     * Gets the signature algorithim used in creating the signature.
     * 
     * @return the signature algorithim used in creating the signature
     */
    public String getSignatureAlgorithm() {
        return signatureAlgorithim;
    }

    /**
     * Sets the signature algorithim used in creating the signature.
     * 
     * @param signatureAlgorithim the signature algorithim used in creating the signature
     */
    public void setSignatureAlgorithm(String signatureAlgorithim) {
        this.signatureAlgorithim = signatureAlgorithim;
    }
    
    /**
     * Gets the transforms applied to the content before a digest is computed.  See 
     * {@link org.apache.xml.security.transforms.Transforms} for a list of available 
     * transforms.
     * 
     * @return the transforms applied to the content before a digest is computed
     */
    public List<String> getTransforms(){
        return transforms;
    }

    /**
     * Gets the signing key used to create the signature.
     * 
     * @return the signing key used to create the signature
     */
    public PrivateKey getSigningKey() {
        return signingKey;
    }

    /**
     * Sets the signing key used to create the signature.
     * 
     * @param key the signing key used to create the signature
     */
    public void setSigningKey(PrivateKey key) {
        signingKey = key;
    }

    /**
     * Gets the public key used to validate the signature.
     * 
     * @return the public key used to validate the signature
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Sets the public key used to validate the signature.
     * 
     * @param key the public key used to validate the signature
     */
    public void setPublicKey(PublicKey key) {
        publicKey = key;
    }
}