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

package org.opensaml.ws.wspolicy.impl;

import org.opensaml.ws.wspolicy.Policy;
import org.opensaml.xml.AbstractExtensibleXMLObjectMarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Marshaller for the &lt;wsp:Policy&gt; element.
 * 
 * @see Policy
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class PolicyMarshaller extends AbstractExtensibleXMLObjectMarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public PolicyMarshaller() {
        super();
    }

    /**
     * Marshalls the <code>wsu:Id</code> and the <code>Name</code> attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        Document document = domElement.getOwnerDocument();
        Policy policy = (Policy) xmlObject;
        String id = policy.getId();
        if (id != null) {
            Attr attribute = XMLHelper.constructAttribute(document, Policy.ID_ATTR_NAME);
            attribute.setValue(id);
            domElement.setAttributeNodeNS(attribute);
            domElement.setIdAttributeNode(attribute, true);
        }
        String name = policy.getName();
        if (name != null) {
            Attr attribute = XMLHelper.constructAttribute(document, Policy.NAME_ATTR_NAME);
            attribute.setValue(name);
            domElement.setAttributeNodeNS(attribute);
        }
        // xs:anyAttribute
        super.marshallAttributes(xmlObject, domElement);
    }

}
