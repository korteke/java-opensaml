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


/** XMLObject representing XML Digital Signature, version 20020212, ECKeyValue element. */
public interface ECKeyValue extends XMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "ECKeyValue";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME =
            new QName(SignatureConstants.XMLSIG11_NS, DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG11_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "ECKeyValueType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME =
            new QName(SignatureConstants.XMLSIG11_NS, TYPE_LOCAL_NAME, SignatureConstants.XMLSIG11_PREFIX);

    /** Id attribute name. */
    public static final String ID_ATTRIB_NAME = "Id";

    /**
     * Get the Id attribute value.
     * 
     * @return the Id attribute value
     */
    @Nullable public String getID();

    /**
     * Set the Id attribute value.
     * 
     * @param newID the new Id attribute value
     */
    public void setID(@Nullable final String newID);
    
    /**
     * Get the ECParameters child element.
     * 
     * @return the ECParameters child element
     */
    @Nullable public XMLObject getECParameters();

    /**
     * Set the ECParameters child element.
     * 
     * @param newParams the new ECParameters child element
     */
    public void setECParameters(@Nullable final XMLObject newParams);

    /**
     * Get the NamedCurve child element.
     * 
     * @return the NamedCurve child element
     */
    @Nullable public NamedCurve getNamedCurve();

    /**
     * Set the NamedCurve child element.
     * 
     * @param newCurve the new NamedCurve child element
     */
    public void setNamedCurve(@Nullable final NamedCurve newCurve);

    /**
     * Get the PublicKey child element.
     * 
     * @return the PublicKey child element
     */
    @Nullable public PublicKey getPublicKey();

    /**
     * Set the PublicKey child element.
     * 
     * @param newKey the new PublicKey child element
     */
    public void setPublicKey(@Nullable final PublicKey newKey);
    
}