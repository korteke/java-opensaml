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
import org.opensaml.xacml.ctx.ActionType;
import org.opensaml.xacml.ctx.EnvironmentType;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResourceType;
import org.opensaml.xacml.ctx.SubjectType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * A unmarshaller for {@link org.opensaml.xacml.ctx.RequestType}
*/


public class XACMLRequestUnmarshaller extends AbstractSAMLObjectUnmarshaller {
    
	/** Constructor */
	public XACMLRequestUnmarshaller() {
		super();
    }

    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
    protected XACMLRequestUnmarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }
    
    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentSAMLObject, XMLObject childSAMLObject)
            throws UnmarshallingException {
    	RequestType request = (RequestType) parentSAMLObject;

        if (childSAMLObject instanceof ActionType)
            request.setAction((ActionType) childSAMLObject);
        else if (childSAMLObject instanceof EnvironmentType)
            request.getEnvironment().add((EnvironmentType)childSAMLObject);
        else if(childSAMLObject instanceof SubjectType)
        	request.getSubjects().add((SubjectType) childSAMLObject);
        else if(childSAMLObject instanceof ResourceType)
        	request.getResources().add((ResourceType) childSAMLObject);        
        else
            super.processChildElement(parentSAMLObject, childSAMLObject);
    }

    
}
