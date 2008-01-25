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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.common.impl.AbstractSAMLObject;
import org.opensaml.xacml.ctx.XACMLDecision;
import org.opensaml.xacml.ctx.XACMLResult;
import org.opensaml.xacml.ctx.XACMLStatus;
import org.opensaml.xacml.policy.XACMLObligations;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLObjectChildrenList;


/**
 * Implementation of {@link org.opensaml.xacml.ctx.XACMLResult} objects.
 *
 */

public class XACMLResultImpl extends AbstractSAMLObject implements XACMLResult {

	/** Attribute resource id*/
	private String resourceId;
	
	/** The decision of the result*/
	private XACMLDecision decision;
	
	/** List of the status of this result*/
	private XMLObjectChildrenList<XACMLStatus> status;
	
	/**The obligations in this Result*/
	private XMLObjectChildrenList<XACMLObligations> obligations;
	

	/**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
	protected XACMLResultImpl(String namespaceURI, String elementLocalName, String namespacePrefix){
		super(namespaceURI,elementLocalName,namespacePrefix);
		status = new XMLObjectChildrenList<XACMLStatus>(this);
		obligations = new XMLObjectChildrenList<XACMLObligations>(this);
	}
	
	 /** {@inheritDoc} */
	public XACMLDecision getDecision() {
		return decision;
	}

	 /** {@inheritDoc} */
	public XMLObjectChildrenList<XACMLObligations> getObligations() {
		return obligations;
	}

	 /** {@inheritDoc} */
	public String getResourceId() {
		return resourceId;
	}

	 /** {@inheritDoc} */
	public XMLObjectChildrenList<XACMLStatus> getStatus() {
		return status;
	}
	
	 /** {@inheritDoc} */
	public void setDecision(XACMLDecision decision) {
		this.decision = decision;
	}

	 /** {@inheritDoc} */
	public void setResourceId(String newResourceId) {
		resourceId = newResourceId;
	}

	 /** {@inheritDoc} */
	public List<XMLObject> getOrderedChildren() {
		ArrayList<XMLObject> children = new ArrayList<XMLObject>();
		
		if(decision != null)
			children.add(decision);
		
		children.addAll(status);
		children.addAll(obligations);
		
		return Collections.unmodifiableList(children);		
	}
}
