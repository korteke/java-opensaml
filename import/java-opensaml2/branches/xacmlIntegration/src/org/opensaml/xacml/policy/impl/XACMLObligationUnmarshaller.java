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

import org.opensaml.common.impl.AbstractSAMLObjectUnmarshaller;
import org.opensaml.xacml.policy.XACMLAttributeAssignment;
import org.opensaml.xacml.policy.XACMLObligation;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;
/**
 *UnMarshaller for {@link org.opensaml.xacml.policy.XACMLObligation} 
 *
 */
public class XACMLObligationUnmarshaller extends AbstractSAMLObjectUnmarshaller {

	/**
     * Constructor
     */
    public XACMLObligationUnmarshaller() {
        super();
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected XACMLObligationUnmarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }
    
    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentObject, XMLObject childObject) throws UnmarshallingException {
    	
    	XACMLObligation obligation = (XACMLObligation) parentObject;
    	
    	if(childObject instanceof XACMLAttributeAssignment){
    		obligation.getAttributesAssignments().add((XACMLAttributeAssignment)childObject);    		
    	}else{
    		super.processChildElement(parentObject, childObject);
    	}
    }

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {

    	XACMLObligation attrib = (XACMLObligation) samlObject;

        if (attribute.getLocalName().equals(XACMLObligation.ObligationId_ATTTRIB_NAME)) {
        	attrib.setobligationId(attribute.getValue());           
        }
        if(attribute.getLocalName().equals(XACMLObligation.FulfillOn_ATTTRIB_NAME)){
        	if(attribute.getValue().equals(XACMLObligation.DENY)){
        		attrib.setFulFillOn(XACMLObligation.DENY);
        	}else{
        		attrib.setFulFillOn(XACMLObligation.PERMIT);
        	}        			
        }
    }	
}
