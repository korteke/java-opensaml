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
import org.opensaml.xml.schema.XSBooleanValue;
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
        super();
    }

    /**
     * Marshalls the &lt;S11:mustUnderstand&gt;, the &lt;S12:role&gt; and the &lt;S11:actor&gt; attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        Document document = domElement.getOwnerDocument();
        Security security = (Security) xmlObject;
        XSBooleanValue mustUnderstand = security.getMustUnderstand();
        if (mustUnderstand != null) {
            // FIXME: SOAP 1.1 or SOAP 1.2 mustUnderstand ?
            Attr attribute = XMLHelper.constructAttribute(document, Security.MUST_UNDERSTAND_ATTR_NAME);
            String value = mustUnderstand.toString();
            attribute.setValue(value);
            domElement.setAttributeNodeNS(attribute);
        }
        String actor = security.getActor();
        if (actor != null) {
            Attr attribute = XMLHelper.constructAttribute(document, Security.ACTOR_ATTR_NAME);
            attribute.setValue(actor);
            domElement.setAttributeNodeNS(attribute);
        }
        String role = security.getRole();
        if (role != null) {
            Attr attribute = XMLHelper.constructAttribute(document, Security.ROLE_ATTR_NAME);
            attribute.setValue(role);
            domElement.setAttributeNodeNS(attribute);
        }

        super.marshallAttributes(xmlObject, domElement);
    }

}
