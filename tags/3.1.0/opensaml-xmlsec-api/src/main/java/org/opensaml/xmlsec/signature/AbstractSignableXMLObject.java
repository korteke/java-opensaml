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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.xml.ElementSupport;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.w3c.dom.Element;


/**
 * Base for signable XMLObjects.
 */
public abstract class AbstractSignableXMLObject extends AbstractXMLObject implements SignableXMLObject {

    /** Signature child. */
    private Signature signature;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AbstractSignableXMLObject(@Nullable final String namespaceURI,
            @Nonnull final String elementLocalName, @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public boolean isSigned() {
        
        Element child = ElementSupport.getFirstChildElement(getDOM());
        while (child != null && !ElementSupport.isElementNamed(child, SignatureConstants.XMLSIG_NS,
                Signature.DEFAULT_ELEMENT_LOCAL_NAME)) {
            child = ElementSupport.getNextSiblingElement(child);
        }
        
        return child != null;
    }

    /** {@inheritDoc} */
    @Nullable public Signature getSignature() {
        return signature;
    }

    /** {@inheritDoc} */
    public void setSignature(@Nullable final Signature newSignature) {
        signature = prepareForAssignment(signature, newSignature);
    }
    
}