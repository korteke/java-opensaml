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
import org.opensaml.xacml.policy.XACMLPolicy;
import org.opensaml.xacml.policy.XACMLPolicySet;
import org.opensaml.xacml.profile.saml.XACMLPolicyStatement;
import org.opensaml.xml.XMLObject;

/**
 * A conctrete implementation of {@link org.opensaml.xacml.profile.saml.XACMLPolicyStatement}
 */

public class XACMLPolicyStatementImpl extends AbstractSAMLObject implements XACMLPolicyStatement{
	
	/** The policy in the policy statement*/
	private XACMLPolicy policy;
	
	/**A set of policies in the statement*/
	private XACMLPolicySet policyset;
    
	/**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
    protected XACMLPolicyStatementImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
    
    /**{@inheritDoc}*/
    public XACMLPolicy getPolicy(){
    	return policy;
    }
    
    /**{@inheritDoc}*/
    public XACMLPolicySet getPolicySet(){
    	return policyset;
    }
   
    /**{@inheritDoc}*/
    public void setPolicy(XACMLPolicy policy){
    	this.policy = prepareForAssignment(this.policy,policy);
    }
    
    /**{@inheritDoc}*/
    public void setPolicySet(XACMLPolicySet policyset){
    	this.policyset = prepareForAssignment(this.policyset,policyset);
    }
         
    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
           	
       	if(policy != null){
       		children.add(policy);
       	}
       	if(policyset != null){
	   	   children.add(policyset);
       	}	
	  
        return Collections.unmodifiableList(children);
    }
}
