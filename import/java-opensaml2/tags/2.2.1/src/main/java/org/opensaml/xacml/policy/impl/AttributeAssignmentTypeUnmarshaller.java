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

package org.opensaml.xacml.policy.impl;

import org.opensaml.xacml.policy.AttributeAssignmentType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.DatatypeHelper;
import org.w3c.dom.Attr;

/** UnMarshaller for {@link AttributeAssignmentType}. */
public class AttributeAssignmentTypeUnmarshaller extends AttributeValueTypeUnmarshaller {

    /** Constructor. */
    public AttributeAssignmentTypeUnmarshaller() {
        super();
    }

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
        super.processElementContent(samlObject, elementContent);
    }

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {

        AttributeAssignmentType attrib = (AttributeAssignmentType) samlObject;

        if (attribute.getLocalName().equals(AttributeAssignmentType.ATTR_ID_ATTRIB_NAME)) {
            attrib.setAttributeId(DatatypeHelper.safeTrimOrNullString(attribute.getValue()));
        }
        else{        	
        	super.processAttribute(samlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        super.processChildElement(parentXMLObject, childXMLObject);
    }
}
