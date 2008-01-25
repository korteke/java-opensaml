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

import org.opensaml.saml2.core.impl.RequestAbstractTypeImpl;
import org.opensaml.xacml.ctx.XACMLRequest;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQuery;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSBooleanValue;


/**
 * A concrete implementation of 
 * {@link org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQuery}
 *
 */
public class XACMLAuthzDecisionQueryImpl extends RequestAbstractTypeImpl implements XACMLAuthzDecisionQuery  {
	
	/** The xacml-context:Request*/
	private XACMLRequest request = null;
	
	/** InputContextOnly attribute value */	
	private boolean InputContextOnly = false;
	
	/** InputContextOnly attribute value */	
	private XSBooleanValue InputContextOnlyXSBooleanValue;
	
	/** ReturnContext attribute value */
	private boolean ReturnContext = false;
	
	/** ReturnContext attribute value */
	private XSBooleanValue ReturnContextXSBooleanValue;
	
	private boolean CombinePolicies = true;
	
	private XSBooleanValue CombinePoliciesXSBooleanValue;
	
	
	
	 /**
     * Constructor    
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
	protected XACMLAuthzDecisionQueryImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        setElementNamespacePrefix(namespacePrefix);
	}

	/** {@inheritDoc} */
	public XACMLRequest getRequest() {
		return request;
	}

	/** {@inheritDoc} */	  
	public void setRequest(XACMLRequest request) {
		this.request = request;
	}

	/** {@inheritDoc} */
	public List<XMLObject> getOrderedChildren() {
		ArrayList<XMLObject> children = new ArrayList<XMLObject>();

		if(super.getOrderedChildren() != null){
			children.addAll(super.getOrderedChildren());
		}
		if(request != null){
			children.add(request);
		}		
		return Collections.unmodifiableList(children);
	}
	
	/** {@inheritDoc} */
	public boolean isInputContextOnly() {
		return InputContextOnly;
	}
	
	/** {@inheritDoc} */
	public boolean isReturnContext() {
		return ReturnContext;
	}
	
	/** {@inheritDoc} */
	public void setInputContextOnly(boolean inputContextOnly) {
		InputContextOnly = inputContextOnly;
	}
	 
	/** {@inheritDoc} */
	public void setReturnContext(boolean returnContext) {
		ReturnContext = returnContext;
	}

	/** {@inheritDoc} */
	public XSBooleanValue getInputContextOnlyXSBooleanValue() {
		return InputContextOnlyXSBooleanValue;
	}

	/** {@inheritDoc} */
	public void setInputContextOnlyXSBooleanValue(
			XSBooleanValue inputContextOnlyXSBooleanValue) {
		InputContextOnlyXSBooleanValue = inputContextOnlyXSBooleanValue;
	}

	/** {@inheritDoc} */
	public XSBooleanValue getReturnContextXSBooleanValue() {
		return ReturnContextXSBooleanValue;
	}

	/** {@inheritDoc} */
	public void setReturnContextXSBooleanValue(
			XSBooleanValue returnContextXSBooleanValue) {
		ReturnContextXSBooleanValue = returnContextXSBooleanValue;
	}
	
	/** {@inheritDoc} */
	public void setCombinePoliciesXSBooleanValue(XSBooleanValue combinePolicies){
		CombinePoliciesXSBooleanValue = combinePolicies;
	}
	
	/** {@inheritDoc} */
	public void setCombinePolicies(boolean combinePolicies){
		CombinePolicies = combinePolicies;
	}
	
	/** {@inheritDoc} */
	public boolean isCombinePolicies(){
		return CombinePolicies;
	}
	
	/** {@inheritDoc} */
	public XSBooleanValue getCombinePoliciesXSBooleanValue(){
		return CombinePoliciesXSBooleanValue;
	}

}
