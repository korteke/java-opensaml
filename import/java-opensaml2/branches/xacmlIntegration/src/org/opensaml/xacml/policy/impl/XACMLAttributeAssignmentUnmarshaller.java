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

import javax.xml.namespace.QName;

import org.opensaml.common.impl.AbstractSAMLObjectUnmarshaller;
import org.opensaml.xacml.policy.XACMLAttributeAssignment;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
/**
 *UnMarshaller for {@link org.opensaml.xacml.policy.XACMLAttributeAssignment} 
 *
 */
public class XACMLAttributeAssignmentUnmarshaller extends
		AbstractSAMLObjectUnmarshaller {
	
	 /**
     * Constructor
     */
    public XACMLAttributeAssignmentUnmarshaller() {
        super();
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected XACMLAttributeAssignmentUnmarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject samlObject, String elementContent) {
    	XACMLAttributeAssignment attributeAssignment = (XACMLAttributeAssignment) samlObject;
    	attributeAssignment.setValue(elementContent);
    }
    
    /** {@inheritDoc} */
    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {

    	XACMLAttributeAssignment attrib = (XACMLAttributeAssignment) samlObject;

        if (attribute.getLocalName().equals(XACMLAttributeAssignment.ATTRIBUTEID_ATTTRIB_NAME)) {
        	attrib.setAttributeId(attribute.getValue());
        }else {
            QName attribQName = XMLHelper.getNodeQName(attribute);
            if (attribute.isId()) {
            	attrib.getUnknownAttributes().registerID(attribQName);
            }
            attrib.getUnknownAttributes().put(attribQName, attribute.getValue());
        }
        super.processAttribute(samlObject, attribute);
    }

}
