/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

package org.opensaml.saml2.metadata.impl;

import org.opensaml.common.impl.AbstractSAMLObjectMarshaller;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * A thread safe Marshaller for {@link org.opensaml.saml2.metadata.OrganizationDisplayName} objects.
 */
public class OrganizationDisplayNameMarshaller extends AbstractSAMLObjectMarshaller {

    /**
     * Constructor
     */
    public OrganizationDisplayNameMarshaller() {
        super(SAMLConstants.SAML20MD_NS, OrganizationDisplayName.LOCAL_NAME);
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected OrganizationDisplayNameMarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }

    /**
     * {@inheritDoc}
     */
    protected void marshallAttributes(XMLObject samlObject, Element domElement) throws MarshallingException {
        OrganizationDisplayName name = (OrganizationDisplayName) samlObject;

        Attr attribute = XMLHelper.constructAttribute(domElement.getOwnerDocument(), SAMLConstants.XML_NS,
                OrganizationDisplayName.LANG_ATTRIB_NAME, SAMLConstants.XML_PREFIX);
        attribute.setValue(name.getName().getLanguage());
        domElement.setAttributeNode(attribute);
    }

    /*
     * @see org.opensaml.xml.io.AbstractXMLObjectMarshaller#marshallElementContent(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Element)
     */
    protected void marshallElementContent(XMLObject samlObject, Element domElement) throws MarshallingException {
        OrganizationDisplayName name = (OrganizationDisplayName) samlObject;

        if (name.getName() != null) {
            XMLHelper.appendTextContent(domElement, name.getName().getLocalString());
        }
    }
}