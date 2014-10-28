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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * XMLObject representing XML Digital Signature, version 20020212, Transform element.
 */
public interface Transform extends XMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Transform";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "TransformType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(SignatureConstants.XMLSIG_NS, TYPE_LOCAL_NAME,
            SignatureConstants.XMLSIG_PREFIX);

    /** Algorithm attribute name. */
    public static final String ALGORITHM_ATTRIB_NAME = "Algorithm";

    /**
     * Get the Algorithm URI attribute value.
     * 
     * @return the Algorithm URI attribute value
     */
    @Nullable public String getAlgorithm();

    /**
     * 
     * Set the Algorithm URI attribute value.
     * 
     * @param newAlgorithm the new Algorithm URI attribute value
     */
    public void setAlgorithm(@Nullable final String newAlgorithm);

    /**
     * Get the list of XMLObject children that match a particular QName.
     * 
     * @param typeOrName the QName of the statements to return
     * 
     * @return the list of XMLObject children that match the given QName
     */
    @Nonnull public List<XMLObject> getXMLObjects(@Nonnull final QName typeOrName);

    /**
     * Get the list of XPath child elements.
     * 
     * @return list of XPath child elements
     */
    @Nonnull public List<XPath> getXPaths();

    /**
     * Gets the ordered list of all XMLObject child elements of this element.
     * 
     * @return list of XMLObject children
     */
    @Nonnull public List<XMLObject> getAllChildren();

}