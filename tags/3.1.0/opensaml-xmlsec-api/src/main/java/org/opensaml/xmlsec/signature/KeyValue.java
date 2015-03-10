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

package org.opensaml.xmlsec.signature;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * XMLObject representing XML Digital Signature, version 20020212, KeyValue element.
 */
public interface KeyValue extends XMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "KeyValue";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "KeyValueType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(SignatureConstants.XMLSIG_NS, TYPE_LOCAL_NAME,
            SignatureConstants.XMLSIG_PREFIX);

    /**
     * Get the DSAKeyValue child element.
     * 
     * @return DSAKeyValue child element
     */
    @Nullable public DSAKeyValue getDSAKeyValue();

    /**
     * Set the DSAKeyValue child element.
     * 
     * @param newDSAKeyValue the new DSAKeyValue child element
     */
    public void setDSAKeyValue(@Nullable final DSAKeyValue newDSAKeyValue);

    /**
     * Get the RSAKeyValue child element.
     * 
     * @return the RSAKeyValue child element
     */
    @Nullable public RSAKeyValue getRSAKeyValue();

    /**
     * Set the RSAKeyValue child element.
     * 
     * @param newRSAKeyValue the new RSAKeyValue child element
     */
    public void setRSAKeyValue(@Nullable final RSAKeyValue newRSAKeyValue);

    /**
     * Get the ECKeyValue child element.
     * 
     * @return the ECKeyValue child element
     */
    @Nullable public ECKeyValue getECKeyValue();

    /**
     * Set the ECKeyValue child element.
     * 
     * @param newECKeyValue the new ECKeyValue child element
     */
    public void setECKeyValue(@Nullable final ECKeyValue newECKeyValue);
    
    /**
     * Get the wildcard &lt;any&gt; XMLObject child element.
     * 
     * @return the wildcard XMLObject child element
     */
    @Nullable public XMLObject getUnknownXMLObject();

    /**
     * Set the wildcard &lt;any&gt; XMLObject child element.
     * 
     * @param newXMLObject the wildcard XMLObject child element
     */
    public void setUnknownXMLObject(@Nullable final XMLObject newXMLObject);
}
