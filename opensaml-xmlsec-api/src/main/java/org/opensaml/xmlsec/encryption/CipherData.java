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
 * XMLObject representing XML Encryption, version 20021210, CipherData element.
 */
public interface CipherData extends XMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "CipherData";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(EncryptionConstants.XMLENC_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, EncryptionConstants.XMLENC_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "CipherDataType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(EncryptionConstants.XMLENC_NS, TYPE_LOCAL_NAME,
            EncryptionConstants.XMLENC_PREFIX);

    /**
     * Get the base64-encoded data representing the the encrypted form of the plaintext data.
     * 
     * @return base64-encoded encrypted value
     */
    @Nullable public CipherValue getCipherValue();

    /**
     * Set the base64-encoded data representing the the encrypted form of the plaintext data.
     * 
     * @param newCipherValue the new base64-encoded encrypted data
     */
    public void setCipherValue(@Nullable final CipherValue newCipherValue);

    /**
     * Get the CipherReference which points to the location encrypted data.
     * 
     * @return CipherReference child element representing the encrypted data
     */
    @Nullable public CipherReference getCipherReference();

    /**
     * Get the CipherReference which points to the location encrypted data.
     * 
     * @param newCipherReference the new CipherReference child element
     */
    public void setCipherReference(@Nullable final CipherReference newCipherReference);
}