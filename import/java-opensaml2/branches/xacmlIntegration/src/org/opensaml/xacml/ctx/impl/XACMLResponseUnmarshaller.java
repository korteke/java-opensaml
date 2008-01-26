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
import org.opensaml.xacml.ctx.ResponseType;
import org.opensaml.xacml.ctx.ResultType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * An unmarshaller for {@link org.opensaml.xacml.ctx.ResponseType}
 * 
 *
 */
public class XACMLResponseUnmarshaller extends AbstractSAMLObjectUnmarshaller {
  
	/** Constructor */
	
    public XACMLResponseUnmarshaller() {
        super();
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected XACMLResponseUnmarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }
    
    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentSAMLObject, XMLObject childSAMLObject)
            throws UnmarshallingException {
    	ResponseType response = (ResponseType) parentSAMLObject;

        if (childSAMLObject instanceof ResultType)
        	response.setResult((ResultType) childSAMLObject);            
        else
            super.processChildElement(parentSAMLObject, childSAMLObject);
    }

}
