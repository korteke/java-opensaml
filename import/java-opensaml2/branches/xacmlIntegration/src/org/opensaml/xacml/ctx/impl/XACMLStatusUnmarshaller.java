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

import org.opensaml.common.impl.AbstractSAMLObjectUnmarshaller;
import org.opensaml.xacml.ctx.XACMLStatus;
import org.opensaml.xacml.ctx.XACMLStatusCode;
import org.opensaml.xacml.ctx.XACMLStatusDetail;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSString;
/**
 * Unmarshaller for {@link org.opensaml.xacml.ctx.XACMLStatus} objects.
 *
 */
public class XACMLStatusUnmarshaller extends AbstractSAMLObjectUnmarshaller {
	
	/**
     * Constructor
     */
    public XACMLStatusUnmarshaller() {
        super();
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected XACMLStatusUnmarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }

	
	  /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentObject, XMLObject childObject) throws UnmarshallingException {
    	
    	XACMLStatus status = (XACMLStatus) parentObject;
    	
    	if(childObject instanceof XACMLStatusCode){
    		status.setStatusCode((XACMLStatusCode)childObject);    		
    	}if(childObject instanceof XSString){
    		status.setStatusMessage((XSString)childObject);    		
    	}if(childObject instanceof XACMLStatusDetail){
    		status.setStatusDetail((XACMLStatusDetail)childObject);    		
    	}else{
    		super.processChildElement(parentObject, childObject);
    	}
    }

}
