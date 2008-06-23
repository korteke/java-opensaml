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
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSBase64Binary;
import org.w3c.dom.Attr;

/**
 * KeyIdentifierUnmarshaller
 * 
 */
public class KeyIdentifierUnmarshaller extends AbstractAttributedIdUnmarshaller {

    /**
     * Default constructor.
     */
    public KeyIdentifierUnmarshaller() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractIdUnmarshaller#processAttribute(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Attr)
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute)
            throws UnmarshallingException {
        String attrName= attribute.getLocalName();
        if (AttributedEncodingType.ENCODING_TYPE_ATTR_LOCAL_NAME.equals(attrName)) {
            AttributedEncodingType encodingType= (AttributedEncodingType) xmlObject;
            String attrValue= attribute.getValue();
            encodingType.setEncodingType(attrValue);
        }
        else if (AttributedValueType.VALUE_TYPE_ATTR_LOCAL_NAME.equals(attrName)) {
            AttributedValueType valueType= (AttributedValueType) xmlObject;
            String attrValue= attribute.getValue();
            valueType.setValueType(attrValue);
        }
        else {
            // marshall wsu:Id attribute
            super.processAttribute(xmlObject, attribute);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractWSSecurityObjectUnmarshaller#processElementContent(org.opensaml.xml.XMLObject,
     *      java.lang.String)
     */
    @Override
    protected void processElementContent(XMLObject xmlObject,
            String elementContent) {
        if (elementContent != null) {
            XSBase64Binary base64Binary= (XSBase64Binary) xmlObject;
            base64Binary.setValue(elementContent.trim());
        }
    }

}
