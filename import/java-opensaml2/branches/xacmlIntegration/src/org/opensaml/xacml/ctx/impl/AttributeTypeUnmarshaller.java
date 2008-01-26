/*
Copyright 2008 Members of the EGEE Collaboration.
Copyright 2008 University Corporation for Advanced Internet Development,
Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package org.opensaml.xacml.ctx.impl;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/** Unmarshaller for {@link AttributeType} objects. */
public class AttributeTypeUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** Constructor. */
    public AttributeTypeUnmarshaller() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param targetNamespaceURI the namespace URI of either the schema type QName or element QName of the elements this
     *            unmarshaller operates on
     * @param targetLocalName the local name of either the schema type QName or element QName of the elements this
     *            unmarshaller operates on
     */
    protected AttributeTypeUnmarshaller(String targetNamespaceURI, String targetLocalName) {
        super(targetNamespaceURI, targetLocalName);
    }

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentObject, XMLObject childObject) throws UnmarshallingException {
        AttributeType attribute = (AttributeType) parentObject;
        if (childObject instanceof AttributeValueType) {
            attribute.getAttributeValues().add(childObject);
        }
    }

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {

        AttributeType attrib = (AttributeType) samlObject;

        if (attribute.getLocalName().equals(AttributeType.ATTRIBUTEID_ATTTRIB_NAME)) {
            attrib.setAttributeID(attribute.getValue());
        } else if (attribute.getLocalName().equals(AttributeType.DATATYPE_ATTRIB_NAME)) {
            attrib.setDataType(attribute.getValue());
        } else if (attribute.getLocalName().equals(AttributeType.ISSUER_ATTRIB_NAME)) {
            attrib.setIssuer(attribute.getValue());
        }
    }

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject xmlObject, String elementContent) {

    }
}