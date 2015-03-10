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

package org.opensaml.xmlsec.encryption;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

/**
 * XMLObject representing XML Encryption, version 20021210, CipherReference element.
 */
public interface CipherReference extends XMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "CipherReference";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(EncryptionConstants.XMLENC_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, EncryptionConstants.XMLENC_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "CipherReferenceType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(EncryptionConstants.XMLENC_NS, TYPE_LOCAL_NAME,
            EncryptionConstants.XMLENC_PREFIX);

    /** URI attribute name. */
    public static final String URI_ATTRIB_NAME = "URI";

    /**
     * Get the URI attribute that describes from where to deference the encrypted data.
     * 
     * @return the URI attribute string
     */
    @Nullable public String getURI();

    /**
     * Set the URI attribute that describes from where to deference the encrypted data.
     * 
     * @param newURI the new URI attribute string value
     */
    public void setURI(@Nullable final String newURI);

    /**
     * Get the Transforms child element, which describes which transformations to apply when dereferencing the data.
     * 
     * @return the Transforms child element
     */
    @Nullable public Transforms getTransforms();

    /**
     * Set the Transforms child element, which describes which transformations to apply when dereferencing the data.
     * 
     * @param newTransforms the new Transforms child element
     */
    public void setTransforms(@Nullable final Transforms newTransforms);

}