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

import java.security.Key;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLConstants;

/**
 * XMLObject representing an enveloped or detached XML Digital Signature, version 20020212, Signature element.
 */
public interface Signature extends XMLObject {

    /** Element local name */
    public final static String DEFAULT_ELEMENT_LOCAL_NAME = "Signature";
    
    /** Default element name */
    public final static QName DEFAULT_ELEMENT_NAME = new QName(XMLConstants.XMLSIG_NS, DEFAULT_ELEMENT_LOCAL_NAME, XMLConstants.XMLSIG_PREFIX);
    
    /** Local name of the XSI type */
    public final static String TYPE_LOCAL_NAME = "SignatureType"; 
        
    /** QName of the XSI type */
    public final static QName TYPE_NAME = new QName(XMLConstants.XMLSIG_NS, TYPE_LOCAL_NAME, XMLConstants.XMLSIG_PREFIX);
    
    /**
     * Gets the canonicalization algorithim used to construct the signature.
     * 
     * @return the canonicalization algorithim used to construct the signature
     */
    public String getCanonicalizationAlgorithm();

    /**
     * Sets the canonicalization algorithim used to construct the signature.
     * 
     * @param newAlgorithm the canonicalization algorithim used to construct the signature
     */
    public void setCanonicalizationAlgorithm(String newAlgorithm);

    /**
     * Gets the signature algorithim used in creating the signature.
     * 
     * @return the signature algorithim used in creating the signature
     */
    public String getSignatureAlgorithm();

    /**
     * Sets the signature algorithim used in creating the signature.
     * 
     * @param signatureAlgorithm the signature algorithim used in creating the signature
     */
    public void setSignatureAlgorithm(String signatureAlgorithm);

    /**
     * Gets the signing key used to create the signature.
     * 
     * @return the signing key used to create the signature
     */
    public Key getSigningKey();

    /**
     * Sets the signing key used to create the signature.
     * 
     * @param newKey the signing key used to create the signature
     */
    public void setSigningKey(Key newKey);

    /**
     * Gets the key information to be added to the signature.
     * 
     * @return the key information to be added to the signature
     */
    public KeyInfo getKeyInfo();

    /**
     * Sets the key information to be added to the signature.
     * 
     * @param newKeyInfo the key information to be added to the signature
     */
    public void setKeyInfo(KeyInfo newKeyInfo);
    
    /**
     * Gets the references to the list of content that will be signed.
     * 
     * @return the references to the list of content that will be signed
     */
    public List<ContentReference> getContentReferences();
}