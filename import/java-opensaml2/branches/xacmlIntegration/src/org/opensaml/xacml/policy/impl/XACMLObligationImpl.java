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
package org.opensaml.xacml.policy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.common.impl.AbstractSAMLObject;
import org.opensaml.xacml.policy.XACMLAttributeAssignment;
import org.opensaml.xacml.policy.XACMLObligation;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLObjectChildrenList;
/**
 *Implementation for {@link org.opensaml.xacml.policy.XACMLObligation} 
 *
 */
public class XACMLObligationImpl extends AbstractSAMLObject implements
		XACMLObligation {
	
	
	/**Lists of the atrributeAssignments in the obligation*/
	private XMLObjectChildrenList<XACMLAttributeAssignment> attributeAssignments;
	
	/**The attribute fulfillOn*/
	private String fulFillOn;
	
	/**Obligation Id */
	private String obligationId;

	/**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
	protected XACMLObligationImpl(String namespaceURI, String elementLocalName, String namespacePrefix){
		super(namespaceURI,elementLocalName,namespacePrefix);	
		attributeAssignments = new XMLObjectChildrenList<XACMLAttributeAssignment>(this);
	}

	 /** {@inheritDoc} */
	public XMLObjectChildrenList<XACMLAttributeAssignment> getAttributesAssignments() {
		return attributeAssignments;
	}

	 /** {@inheritDoc} */
	public String getFulfillOn() {
		return fulFillOn;
	}

	 /** {@inheritDoc} */
	public String getObligationId() {
		return obligationId;
	}

	 /** {@inheritDoc} */
	public void setFulFillOn(String newFulfillOn) {
		fulFillOn = prepareForAssignment(this.fulFillOn,newFulfillOn);
	}

	 /** {@inheritDoc} */
	public void setobligationId(String newObligationId) {
		obligationId = prepareForAssignment(this.obligationId,newObligationId);

	}

	 /** {@inheritDoc} */
	public List<XMLObject> getOrderedChildren() {
		ArrayList<XMLObject> children = new ArrayList<XMLObject>();
		
					
		children.addAll(attributeAssignments);		
				
		return Collections.unmodifiableList(children);
	}

}
