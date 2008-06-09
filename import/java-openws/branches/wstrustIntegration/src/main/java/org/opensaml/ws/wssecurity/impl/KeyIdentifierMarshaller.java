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
import org.opensaml.ws.wssecurity.AttributedValueType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSBase64Binary;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * KeyIdentifierMarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class KeyIdentifierMarshaller extends AbstractAttributedIdMarshaller {

    /**
     * Default constructor.
     */
    public KeyIdentifierMarshaller() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractIdMarshaller#marshallAttributes(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement)
            throws MarshallingException {
        Document document= domElement.getOwnerDocument();
        AttributedValueType typed= (AttributedValueType) xmlObject;
        String valueType= typed.getValueType();
        if (valueType != null) {
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         AttributedValueType.VALUE_TYPE_ATTR_NAME);
            attribute.setValue(valueType);
            domElement.setAttributeNode(attribute);
        }
        AttributedEncodingType encodingTyped= (AttributedEncodingType) xmlObject;
        String encodingType= encodingTyped.getEncodingType();
        if (encodingType != null) {
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         AttributedEncodingType.ENCODING_TYPE_ATTR_NAME);
            attribute.setValue(encodingType);
            domElement.setAttributeNode(attribute);
        }
        super.marshallAttributes(xmlObject, domElement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractWSSecurityObjectMarshaller#marshallElementContent(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void marshallElementContent(XMLObject xmlObject,
            Element domElement) throws MarshallingException {
        XSBase64Binary base64binary= (XSBase64Binary) xmlObject;
        XMLHelper.appendTextContent(domElement, base64binary.getValue());
    }

}
