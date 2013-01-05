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
 * XMLObject representing XML Encryption, version 20021210, DHKeyValue element.
 */
public interface DHKeyValue extends XMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "DHKeyValue";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(EncryptionConstants.XMLENC_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, EncryptionConstants.XMLENC_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "DHKeyValueType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(EncryptionConstants.XMLENC_NS, TYPE_LOCAL_NAME,
            EncryptionConstants.XMLENC_PREFIX);

    /**
     * Get the P child element.
     * 
     * @return the P child element
     */
    @Nullable public P getP();

    /**
     * Set the P child element.
     * 
     * @param newP the new P child element
     */
    public void setP(@Nullable final P newP);

    /**
     * Get the Q child element.
     * 
     * @return the Q child element
     */
    @Nullable public Q getQ();

    /**
     * Set the Q child element.
     * 
     * @param newQ the new Q child element
     */
    public void setQ(@Nullable final Q newQ);

    /**
     * Get the Generator child element.
     * 
     * @return the Generator child element
     */
    @Nullable public Generator getGenerator();

    /**
     * Set the G child element.
     * 
     * @param newGenerator the new G child element
     */
    public void setGenerator(@Nullable final Generator newGenerator);

    /**
     * Get the Public element.
     * 
     * @return the Public element
     */
    @Nullable public Public getPublic();

    /**
     * Set the Public element.
     * 
     * @param newPublic the new Public child element
     */
    public void setPublic(@Nullable final Public newPublic);

    /**
     * Get the seed element.
     * 
     * @return the seed element
     */
    @Nullable public Seed getSeed();

    /**
     * Set the seed element.
     * 
     * @param newSeed new seed element
     */
    public void setSeed(@Nullable final Seed newSeed);

    /**
     * Get the pgenCounter element.
     * 
     * @return the pgenCounter element
     */
    @Nullable public PgenCounter getPgenCounter();

    /**
     * Set the pgenCounter element.
     * 
     * @param newPgenCounter new pgenCounter element
     */
    public void setPgenCounter(@Nullable final PgenCounter newPgenCounter);

}