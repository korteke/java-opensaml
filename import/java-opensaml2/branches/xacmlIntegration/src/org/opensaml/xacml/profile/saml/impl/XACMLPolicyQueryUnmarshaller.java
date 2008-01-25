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
import org.opensaml.xacml.policy.XACMLPolicyIdReference;
import org.opensaml.xacml.policy.XACMLPolicySetIdReference;
import org.opensaml.xacml.profile.saml.XACMLPolicyQuery;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * Unmarshaller for {@link org.opensaml.xacml.profile.saml.XACMLPolicyQuery}.
 *
 */
public class XACMLPolicyQueryUnmarshaller extends RequestAbstractTypeUnmarshaller {
	
	 /**
     * Constructor     
     */
	
	public XACMLPolicyQueryUnmarshaller() {
        super();		
	}
	
	 /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     */
	
	protected XACMLPolicyQueryUnmarshaller(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }

	/** {@inheritDoc} */
    protected void processChildElement(XMLObject parentObject, XMLObject childObject) throws UnmarshallingException {
    	XACMLPolicyQuery xacmlpolicyquery = (XACMLPolicyQuery) parentObject;

    	if(childObject instanceof XACMLRequest){
    		xacmlpolicyquery.setRequest((XACMLRequest) childObject);
        }else if(childObject instanceof XACMLPolicyIdReference) {
        	xacmlpolicyquery.setXACMLPolicyIdReference((XACMLPolicyIdReference)childObject);        		
        }else if(childObject instanceof XACMLPolicySetIdReference){
        	xacmlpolicyquery.setXACMLPolicySetIdReference((XACMLPolicySetIdReference)childObject);
        }else{
        	super.processChildElement(parentObject, childObject);
        }
    }

}
