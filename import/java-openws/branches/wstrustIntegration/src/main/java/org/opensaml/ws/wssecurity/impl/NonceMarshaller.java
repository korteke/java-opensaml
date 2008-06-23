/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.wssecurity.impl;

import org.opensaml.ws.wssecurity.AttributedEncodingType;
import org.opensaml.ws.wssecurity.Nonce;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * NonceMarshaller
 * 
 */
public class NonceMarshaller extends AbstractWSSecurityObjectMarshaller {

    /**
     * Default constructor.
     */
    public NonceMarshaller() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.io.AbstractXMLObjectMarshaller#marshallElementContent(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {
        Nonce nonce = (Nonce) xmlObject;
        XMLHelper.appendTextContent(domElement, nonce.getValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractWSSecurityObjectMarshaller#marshallAttributes(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        Nonce nonce = (Nonce) xmlObject;
        String encodingType = nonce.getEncodingType();
        if (encodingType != null) {
            Document document = domElement.getOwnerDocument();
            Attr attribute = XMLHelper.constructAttribute(document, AttributedEncodingType.ENCODING_TYPE_ATTR_NAME);
            attribute.setValue(encodingType);
            domElement.setAttributeNode(attribute);
        }
    }

}
