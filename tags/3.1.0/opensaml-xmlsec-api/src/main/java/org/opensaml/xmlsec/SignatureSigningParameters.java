/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xmlsec;

import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;


/**
 * The effective parameters to use when generating an XML signature.
 */
public class SignatureSigningParameters {
    
    /** The signing credential. */
    private Credential signingCredential;
    
    /** The signature algorithm URI. */
    private String signatureAlgorithmURI;
    
    /** The signature reference digest method URI. */
    private String signatureReferenceDigestMethod;
    
    /** The signature canonicalization algorithm URI. */
    private String signatureCanonicalizationAlgorithm;
    
    /** The signature HMAC output length value. */
    private Integer signatureHMACOutputLength;
    
    /** The KeyInfo generator. */
    private KeyInfoGenerator keyInfoGenerator;
    
    /**
     * Get the signing credential to use when signing.
     * 
     * @return the signing credential
     */
    @Nullable public Credential getSigningCredential() {
        return signingCredential;
    }
    
    /**
     * Set the signing credential to use when signing.
     * 
     * @param credential the signing credential
     */
    public void setSigningCredential(@Nullable final Credential credential) {
        signingCredential = credential;
    }
    
    /**
     * Get the signature algorithm URI to use when signing.
     * 
     * @return a signature algorithm URI mapping
     */
    @Nullable public String getSignatureAlgorithm() {
        return signatureAlgorithmURI;
    }
    
    /**
     * Set the signature algorithm URI to use when signing.
     * 
     * @param uri a signature algorithm URI mapping
     */
    public void setSignatureAlgorithm(@Nullable final String uri) {
        signatureAlgorithmURI = uri;
    }
    
    /**
     * Get a digest method algorithm URI suitable for use as a Signature Reference DigestMethod value.
     * 
     * @return a digest method algorithm URI
     */
    @Nullable public String getSignatureReferenceDigestMethod() {
        return signatureReferenceDigestMethod;
    }
    
    /**
     * Set a digest method algorithm URI suitable for use as a Signature Reference DigestMethod value.
     * 
     * @param uri a digest method algorithm URI
     */
    public void setSignatureReferenceDigestMethod(@Nullable final String uri) {
        signatureReferenceDigestMethod = uri;
    }
    
    /**
     * Get a canonicalization algorithm URI suitable for use as a Signature CanonicalizationMethod value.
     * 
     * @return a canonicalization algorithm URI
     */
    @Nullable public String getSignatureCanonicalizationAlgorithm() {
        return signatureCanonicalizationAlgorithm;
    }
    
    /**
     * Set a canonicalization algorithm URI suitable for use as a Signature CanonicalizationMethod value.
     * 
     * @param uri a canonicalization algorithm URI
     */
    public void setSignatureCanonicalizationAlgorithm(@Nullable final String uri) {
        signatureCanonicalizationAlgorithm = uri;
    }
    
    /**
     * Get the value to be used as the Signature SignatureMethod HMACOutputLength value, used
     * only when signing with an HMAC algorithm.  This value is optional when using HMAC.
     * 
     * @return the configured HMAC output length value
     */
    @Nullable public Integer getSignatureHMACOutputLength() {
        return signatureHMACOutputLength;
    }
    
    /**
     * Set the value to be used as the Signature SignatureMethod HMACOutputLength value, used
     * only when signing with an HMAC algorithm.  This value is optional when using HMAC.
     * 
     * @param length the configured HMAC output length value
     */
    public void setSignatureHMACOutputLength(@Nullable final Integer length) {
        signatureHMACOutputLength = length;
    }
    
    /**
     * Get the KeyInfoGenerator to use when generating the Signature/KeyInfo.
     * 
     * @return the KeyInfoGenerator instance
     */
    @Nullable public KeyInfoGenerator getKeyInfoGenerator() {
        return keyInfoGenerator;
    }

    /**
     * Set the KeyInfoGenerator to use when generating the Signature/KeyInfo.
     * 
     * @param generator the KeyInfoGenerator instance
     */
    public void  setKeyInfoGenerator(@Nullable final KeyInfoGenerator generator) {
        keyInfoGenerator = generator;
    }

}