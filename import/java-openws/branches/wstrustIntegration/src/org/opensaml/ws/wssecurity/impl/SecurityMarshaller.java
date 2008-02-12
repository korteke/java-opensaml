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


import org.opensaml.ws.wssecurity.Security;
import org.opensaml.xml.AbstractExtensibleXMLObjectMarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * SecurityMarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class SecurityMarshaller extends AbstractExtensibleXMLObjectMarshaller {

    /**
     * Default constructor
     */
    public SecurityMarshaller() {
        super(Security.ELEMENT_NAME.getNamespaceURI(),
              Security.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Marshalls the &lt;S11:mustUnderstand&gt;, the &lt;S12:role&gt; and the
     * &lt;S11:actor&gt; attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement)
            throws MarshallingException {
        Document document= domElement.getOwnerDocument();
        Security security= (Security) xmlObject;
        boolean mustUnderstand= security.getMustUnderstand();
        if (mustUnderstand) {
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         Security.MUST_UNDERSTAND_ATTR_NAME);
            // FIXME: SOAP 1.1 uses 1 or 0 and SOAP 1.2 uses true, 1, false or 0
            // ???
            attribute.setValue("1");
            domElement.setAttributeNode(attribute);
        }
        String actor= security.getActor();
        if (actor != null) {
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         Security.ACTOR_ATTR_NAME);
            attribute.setValue(actor);
            domElement.setAttributeNode(attribute);
        }
        // FIXME: role attribute is only SOAP 1.2 !!!
        /*
         * String role= security.getRole(); if (role != null) { Attr attribute=
         * XMLHelper.constructAttribute(document, Security.ROLE_ATTR_NAME);
         * attribute.setValue(role); domElement.setAttributeNode(attribute); }
         */
        super.marshallAttributes(xmlObject, domElement);
    }

}
