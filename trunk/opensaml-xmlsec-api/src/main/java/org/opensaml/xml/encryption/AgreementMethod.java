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

package org.opensaml.xml.encryption;

import javax.xml.namespace.QName;

import org.opensaml.xml.ElementExtensibleXMLObject;
import org.opensaml.xml.util.XMLConstants;
import org.opensaml.xml.validation.ValidatingXMLObject;

/**
 * XMLObject representing XML Encryption, version 20021210, AgreementMethod element.
 */
public interface AgreementMethod extends ValidatingXMLObject, ElementExtensibleXMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "AgreementMethod";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(XMLConstants.XMLENC_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            XMLConstants.XMLENC_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "AgreementMethodType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(XMLConstants.XMLENC_NS, TYPE_LOCAL_NAME, 
            XMLConstants.XMLENC_PREFIX);

    /** Algorithm attribute name. */
    public static final String ALGORITHM_ATTRIBUTE_NAME = "Algorithm";

    /**
     * Gets the algorithm URI attribute value for this agreement method.
     * 
     * @return the algorithm URI attribute value
     */
    public String getAlgorithm();

    /**
     * Sets the algorithm URI attribute value for this agreement method.
     * 
     * @param newAlgorithm the new algorithm URI attribute value
     */
    public void setAlgorithm(String newAlgorithm);

    /**
     * Get the nonce child element used to introduce variability into the generation of keying material.
     * 
     * @return the KA-Nonce child element
     */
    public KANonce getKANonce();

    /**
     * Set the nonce child element used to introduce variability into the generation of keying material.
     * 
     * @param newKANonce the new KA-Nonce child element
     */
    public void setKANonce(KANonce newKANonce);

    /**
     * Get the child element containing the key generation material for the originator.
     * 
     * @return the OriginatorKeyInfo child element
     */
    public OriginatorKeyInfo getOriginatorKeyInfo();

    /**
     * Set the child element containing the key generation material for the originator.
     * 
     * @param newOriginatorKeyInfo the new OriginatorKeyInfo child element
     */
    public void setOriginatorKeyInfo(OriginatorKeyInfo newOriginatorKeyInfo);

    /**
     * Get the child element containing the key generation material for the recipient.
     * 
     * @return the RecipientKeyInfo child element
     */
    public RecipientKeyInfo getRecipientKeyInfo();

    /**
     * Set the child element containing the key generation material for the recipient.
     * 
     * @param newRecipientKeyInfo the new RecipientKeyInfo child element
     */
    public void setRecipientKeyInfo(RecipientKeyInfo newRecipientKeyInfo);

}
