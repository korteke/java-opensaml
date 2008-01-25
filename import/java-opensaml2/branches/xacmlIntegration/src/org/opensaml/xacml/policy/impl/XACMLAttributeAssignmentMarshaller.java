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

import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.opensaml.Configuration;
import org.opensaml.common.impl.AbstractSAMLObjectMarshaller;
import org.opensaml.xacml.policy.XACMLAttributeAssignment;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 *Marshaller for {@link org.opensaml.xacml.policy.XACMLAttributeAssignment} 
 *
 */
public class XACMLAttributeAssignmentMarshaller extends
		AbstractSAMLObjectMarshaller {
	
	/**
     * Constructor
     */
    public XACMLAttributeAssignmentMarshaller() {
        super();
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected XACMLAttributeAssignmentMarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }
    
    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject samlObject, Element domElement) throws MarshallingException {
        XACMLAttributeAssignment attributeAsignment = (XACMLAttributeAssignment) samlObject;
        XMLHelper.appendTextContent(domElement, attributeAsignment.getValue());
    }
    
    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject samlElement, Element domElement) throws MarshallingException {
    	XACMLAttributeAssignment attributeAssignment = (XACMLAttributeAssignment) samlElement;

        if (attributeAssignment.getAttributeId() != null) {
            domElement.setAttributeNS(null, XACMLAttributeAssignment.ATTRIBUTEID_ATTTRIB_NAME, attributeAssignment.getAttributeId());
        }
        Attr attr;
        for (Entry<QName, String> entry : attributeAssignment.getUnknownAttributes().entrySet()) {
        	attr = XMLHelper.constructAttribute(domElement.getOwnerDocument(), entry.getKey());
        	attr.setValue(entry.getValue());
        	domElement.setAttributeNodeNS(attr);
        	if (Configuration.isIDAttribute(entry.getKey())
        			|| attributeAssignment.getUnknownAttributes().isIDAttribute(entry.getKey())) {
        		attr.getOwnerElement().setIdAttributeNode(attr, true);
        	}
        }
    }
    
}
