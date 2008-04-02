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


import org.opensaml.ws.wssecurity.Reference;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * ReferenceMarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class ReferenceMarshaller extends AbstractWSSecurityObjectMarshaller {

    /**
     * Default constructor.
     */
    public ReferenceMarshaller() {
        super(Reference.ELEMENT_NAME.getNamespaceURI(),
              Reference.ELEMENT_NAME.getLocalPart());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractWSSecurityObjectMarshaller#marshallAttributes(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement)
            throws MarshallingException {
        Reference reference= (Reference) xmlObject;
        Document document= domElement.getOwnerDocument();
        String uri= reference.getURI();
        if (uri != null) {
            Attr attr= XMLHelper.constructAttribute(document,
                                                    Reference.URI_ATTR_NAME);
            attr.setValue(uri);
            domElement.setAttributeNode(attr);
        }
        String valueType= reference.getValueType();
        if (valueType != null) {
            Attr attr= XMLHelper.constructAttribute(document,
                                                    Reference.VALUE_TYPE_ATTR_NAME);
            attr.setValue(valueType);
            domElement.setAttributeNode(attr);
        }

        super.marshallAttributes(xmlObject, domElement);
    }

}
