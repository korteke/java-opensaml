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
package org.opensaml.ws.wsaddressing.impl;

import java.util.Map.Entry;

import javax.xml.namespace.QName;


import org.opensaml.ws.wsaddressing.Address;
import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Marshaller for the &lt;wsa:Address&gt; element.
 * 
 * @see Address
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class AddressMarshaller extends AbstractWSAddressingObjectMarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public AddressMarshaller() {
        super(Address.ELEMENT_NAME.getNamespaceURI(),
              Address.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Marshalls the &lt;wsa:Address&gt; element URI content.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallElementContent(XMLObject xmlObject,
            Element domElement) throws MarshallingException {
        Address intf= (Address) xmlObject;
        XMLHelper.appendTextContent(domElement, intf.getValue());
    }

    /**
     * Marshalls the xs:anyAttributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement)
            throws MarshallingException {
        AttributeExtensibleXMLObject anyAttribute= (AttributeExtensibleXMLObject) xmlObject;
        Document document= domElement.getOwnerDocument();
        Attr attribute;
        for (Entry<QName, String> entry : anyAttribute.getUnknownAttributes().entrySet()) {
            attribute= XMLHelper.constructAttribute(document, entry.getKey());
            attribute.setValue(entry.getValue());
            domElement.setAttributeNodeNS(attribute);
        }
    }
}
