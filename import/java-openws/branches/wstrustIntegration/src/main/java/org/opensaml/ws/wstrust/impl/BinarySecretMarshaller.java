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
package org.opensaml.ws.wstrust.impl;

import java.util.Map.Entry;

import javax.xml.namespace.QName;


import org.opensaml.ws.wstrust.BinarySecret;
import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSBase64Binary;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Marshaller for the BinarySecret element.
 * 
 * @see BinarySecret
 * 
 */
public class BinarySecretMarshaller extends AbstractWSTrustObjectMarshaller {

    /**
     * Default constructor.
     * 
     */
    public BinarySecretMarshaller() {
        super();
    }

    /**
     * Marshalls the Base64 binary content.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallElementContent(XMLObject xmlObject,
            Element domElement) throws MarshallingException {
        XSBase64Binary base64binary= (XSBase64Binary) xmlObject;
        String value= base64binary.getValue();
        XMLHelper.appendTextContent(domElement, value);
    }

    /**
     * Marshalls the &lt;@Type&gt; and the <code>xs:anyAttribute</code>
     * attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement)
            throws MarshallingException {
        Document document= domElement.getOwnerDocument();
        BinarySecret binarySecret= (BinarySecret) xmlObject;
        String type= binarySecret.getType();
        if (type != null) {
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         BinarySecret.TYPE_ATTR_NAME);
            attribute.setValue(type);
            domElement.setAttributeNodeNS(attribute);
        }
        // xs:anyAttribute
        AttributeExtensibleXMLObject anyAttribute= (AttributeExtensibleXMLObject) xmlObject;
        Attr attribute;
        for (Entry<QName, String> entry : anyAttribute.getUnknownAttributes().entrySet()) {
            attribute= XMLHelper.constructAttribute(document, entry.getKey());
            attribute.setValue(entry.getValue());
            domElement.setAttributeNodeNS(attribute);
            if (Configuration.isIDAttribute(entry.getKey())
                    || anyAttribute.getUnknownAttributes().isIDAttribute(entry.getKey())) {
                attribute.getOwnerElement().setIdAttributeNode(attribute, true);
            }
        }
        super.marshallAttributes(xmlObject, domElement);
    }

}
