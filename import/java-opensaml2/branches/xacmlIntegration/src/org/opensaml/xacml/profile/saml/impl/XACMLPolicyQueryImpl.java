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
import org.opensaml.xacml.policy.XACMLPolicyIdReference;
import org.opensaml.xacml.policy.XACMLPolicySetIdReference;
import org.opensaml.xacml.profile.saml.XACMLPolicyQuery;
import org.opensaml.xml.XMLObject;

/**
 * Implementation for {@link org.opensaml.xacml.profile.saml.XACMLPolicyQuery}.
 *
 */

public class XACMLPolicyQueryImpl extends RequestAbstractTypeImpl implements
		XACMLPolicyQuery {

	/** The xacml-context:Request */
	private XACMLRequest request = null;
	
	/** The xacml policyIdReferance	 */
	private XACMLPolicyIdReference policyIdReferance = null;
	
	/** The xacml policySetIdReferance */
	private XACMLPolicySetIdReference policyIdSetReferance = null;
	
	/**
	 * Constructor
	 */		
	public XACMLPolicyQueryImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        setElementNamespacePrefix(namespacePrefix);
	}
	
	/** {@inheritDoc} */
	public XACMLRequest getRequest() {
		return request;
	}

	/** {@inheritDoc} */
	public XACMLPolicyIdReference getXACMLPolicyIdReference() {
		return policyIdReferance;
	}

	/** {@inheritDoc} */
	public XACMLPolicySetIdReference getXACMLPolicySetIdReference() {
		return policyIdSetReferance;
	}

	/** {@inheritDoc} */
	public void setRequest(XACMLRequest request) {
		this.request = request;
	}

	/** {@inheritDoc} */
	public void setXACMLPolicyIdReference(XACMLPolicyIdReference policyIdRef) {
		policyIdReferance = policyIdRef;
	}

	/** {@inheritDoc} */
	public void setXACMLPolicySetIdReference(
			XACMLPolicySetIdReference policySetIdRef) {
		policyIdSetReferance = policySetIdRef;
	}
	
	/** {@inheritDoc} */
	public List<XMLObject> getOrderedChildren() {
		ArrayList<XMLObject> children = new ArrayList<XMLObject>();
		
		if(super.getOrderedChildren() != null){
			children.addAll(super.getOrderedChildren());
		}
		if(request != null){
			children.add(request);
		}if(policyIdReferance != null){
			children.add(policyIdReferance);
		}if(policyIdSetReferance != null){
			children.add(policyIdSetReferance);
		}
						
		return Collections.unmodifiableList(children);
	}

}
