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

import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.common.impl.AbstractSAMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.AttributeMap;
import org.opensaml.xml.util.IndexedXMLObjectChildrenList;
/**
 *Implementation for {@link org.opensaml.xacml.policy.XACMLAttributeAssignment} 
 *
 */
public class XACMLAttributeAssignmentImpl extends AbstractSAMLObject implements
		org.opensaml.xacml.policy.XACMLAttributeAssignment {
	
	/** Value for the attribute AttributeId	 */
	private String attributeId;
	
	/**The value of the element*/
	private String value;
	
	 /** "anyAttribute" attributes. */
    private final AttributeMap unknownAttributes;
    
    /** "any" children. */
    private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;

	/**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
	protected XACMLAttributeAssignmentImpl(String namespaceURI, String elementLocalName, String namespacePrefix){
		super(namespaceURI,elementLocalName,namespacePrefix);
		unknownAttributes = new AttributeMap(this);
        unknownChildren = new IndexedXMLObjectChildrenList<XMLObject>(this);
		
	}
	
	 /** {@inheritDoc} */
	public String getAttributeId() {
		return attributeId;
	}

	 /** {@inheritDoc} */
	public void setAttributeId(String newAttributeID) {
		attributeId = prepareForAssignment(this.attributeId,newAttributeID);

	}

	 /** {@inheritDoc} */
	public List<XMLObject> getOrderedChildren() {
		return null;			
	}

	 /** {@inheritDoc} */
	public String getValue() {
		return value;
	}

	 /** {@inheritDoc} */
	public void setValue(String newValue) {
		this.value = prepareForAssignment(this.value, newValue);
	}
	
	 
    /** {@inheritDoc} */
	public List<XMLObject> getUnknownXMLObjects() {
		return unknownChildren;
	}

	/** {@inheritDoc} */
	public List<XMLObject> getUnknownXMLObjects(QName typeOrName) {
		return unknownChildren.get(typeOrName);
	}

	/** {@inheritDoc} */
	public AttributeMap getUnknownAttributes() {
		return unknownAttributes;
	}


}
