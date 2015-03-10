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

/**
 * 
 */

package org.opensaml.xmlsec.mock;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for {@link org.opensaml.core.xml.mock.SimpleXMLObject}.
 */
public class SignableSimpleXMLObjectUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {

        SignableSimpleXMLObject simpleXMLObject = (SignableSimpleXMLObject) parentXMLObject;

        if (childXMLObject instanceof SignableSimpleXMLObject) {
            simpleXMLObject.getSimpleXMLObjects().add((SignableSimpleXMLObject) childXMLObject);
        } else if (childXMLObject instanceof EncryptedData) {
            simpleXMLObject.setEncryptedData((EncryptedData) childXMLObject);
        } else if (childXMLObject instanceof Signature) {
            simpleXMLObject.setSignature((Signature) childXMLObject);
        } else {
            simpleXMLObject.getUnknownXMLObjects().add(childXMLObject);
        }
    }

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        SignableSimpleXMLObject simpleXMLObject = (SignableSimpleXMLObject) xmlObject;

        if (attribute.getLocalName().equals(SimpleXMLObject.ID_ATTRIB_NAME)) {
            simpleXMLObject.setId(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(simpleXMLObject.getUnknownAttributes(), attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject xmlObject, String elementContent) {
        SignableSimpleXMLObject simpleXMLObject = (SignableSimpleXMLObject) xmlObject;

        simpleXMLObject.setValue(elementContent);
    }
}