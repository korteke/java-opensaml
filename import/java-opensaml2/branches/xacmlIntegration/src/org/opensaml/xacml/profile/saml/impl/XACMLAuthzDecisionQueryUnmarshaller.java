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
package org.opensaml.xacml.profile.saml.impl;


import org.opensaml.saml2.core.impl.RequestAbstractTypeUnmarshaller;
import org.opensaml.xacml.ctx.XACMLRequest;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQuery;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQuery} objects.
 */

public class XACMLAuthzDecisionQueryUnmarshaller extends RequestAbstractTypeUnmarshaller {
	
	 /**
     * Constructor     
     */
	
	public XACMLAuthzDecisionQueryUnmarshaller() {
        super();	
	}
	
	 /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
	
	protected XACMLAuthzDecisionQueryUnmarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }

	/** {@inheritDoc} */
    protected void processChildElement(XMLObject parentObject, XMLObject childObject) throws UnmarshallingException {
    	XACMLAuthzDecisionQuery xacmlauthzdecisionquery = (XACMLAuthzDecisionQuery) parentObject;

    	if(childObject instanceof XACMLRequest){
    		xacmlauthzdecisionquery.setRequest((XACMLRequest) childObject);
        }else{
            super.processChildElement(parentObject, childObject);
        }
    }
    
    /** {@inheritDoc} */
    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {
    	XACMLAuthzDecisionQuery authzDS = (XACMLAuthzDecisionQuery) samlObject;
        
    	if(attribute.getLocalName().equals(XACMLAuthzDecisionQuery.INPUTCONTEXTONLY_ATTRIB_NAME) 
    			&& authzDS.isInputContextOnly()){
    		authzDS.setInputContextOnly(true);    		
    	}
    	if(attribute.getLocalName().equals(XACMLAuthzDecisionQuery.INPUTCONTEXTONLY_ATTRIB_NAME) 
    			&& !(authzDS.isInputContextOnly())){
    		authzDS.setInputContextOnly(false);
    	}
    	if(attribute.getLocalName().equals(XACMLAuthzDecisionQuery.RETURNCONTEXT_ATTRIB_NAME) 
    			&& authzDS.isReturnContext()){
    		authzDS.setReturnContext(true);
    	}
    	if(attribute.getLocalName().equals(XACMLAuthzDecisionQuery.RETURNCONTEXT_ATTRIB_NAME) 
    			&& !(authzDS.isReturnContext())){
    		authzDS.setReturnContext(false);
    	}
    	if(attribute.getLocalName().equals(XACMLAuthzDecisionQuery.COMBINEPOLICIES_ATTRIB_NAME) 
    			&& !(authzDS.isCombinePolicies())){
    		authzDS.setReturnContext(false);
    	}
    	if(attribute.getLocalName().equals(XACMLAuthzDecisionQuery.COMBINEPOLICIES_ATTRIB_NAME) 
    			&& authzDS.isCombinePolicies()){
    		authzDS.setReturnContext(true);
    	}    	
    	super.processAttribute(samlObject, attribute);
    }
   
}
