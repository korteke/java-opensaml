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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.common.impl.AbstractSAMLObject;
import org.opensaml.xacml.ctx.XACMLRequest;
import org.opensaml.xacml.ctx.XACMLResponse;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionStatement;
import org.opensaml.xml.XMLObject;


/**
 * A concrete implementation of {@link org.opensaml.xacml.profile.saml.XACMLAuthzDecisionStatement}
 * 
 */
public class XACMLAuthzDecisionStatementImpl extends AbstractSAMLObject implements XACMLAuthzDecisionStatement {

	/** 
	 * The request of the authorization request
	 */
    private XACMLRequest request;
    
    
    /**
     * The response of the authorization request
     */
    private XACMLResponse response;
	
    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
    protected XACMLAuthzDecisionStatementImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
 
    /** {@inheritDoc} */
    public XACMLRequest getRequest(){
    	return request;
    }
    
    /** {@inheritDoc} */
    public XACMLResponse getResponse(){
    	return response;
    }
    
    /** {@inheritDoc} */
    public void setRequest(XACMLRequest request){
    	this.request = request;
    }
    
    /** {@inheritDoc} */
    public void setResponse(XACMLResponse response){
    	this.response = response;
    }
    
    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
    	ArrayList<XMLObject> children = new ArrayList<XMLObject>();
    	
    	if(request != null){
    		children.add(request);
    	}
    	if(response != null){
    		children.add(response);
    	}
    	
        return Collections.unmodifiableList(children);
    }
}
