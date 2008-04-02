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
import org.opensaml.xml.AbstractExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSBooleanValue;
import org.w3c.dom.Attr;

/**
 * SecurityUnmarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class SecurityUnmarshaller extends AbstractExtensibleXMLObjectUnmarshaller {

    /**
     * Default constructor.
     */
    public SecurityUnmarshaller() {
        super(Security.ELEMENT_NAME.getNamespaceURI(), Security.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Unmarshalls the &lt;S11:mustUnderstand&gt;, the &lt;S12:role&gt; and the &lt;S11:actor&gt; attributes.
     * <p>
     * {@inheritDoc}
     */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        Security security = (Security) xmlObject;
        String attrName = attribute.getLocalName();
        if (Security.MUST_UNDERSTAND_ATTR_LOCAL_NAME.equals(attrName)) {
            String value = attribute.getValue();
            // SOAP 1.1 or SOAP 1.2 ???
            XSBooleanValue mustUnderstand = XSBooleanValue.valueOf(value);
            security.setMustUnderstand(mustUnderstand);
        } else if (Security.ACTOR_ATTR_LOCAL_NAME.equals(attrName)) {
            String actor = attribute.getValue();
            security.setActor(actor);
        } else if (Security.ROLE_ATTR_LOCAL_NAME.equals(attrName)) {
            String role = attribute.getValue();
            security.setRole(role);
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}
