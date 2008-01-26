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
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLObjectChildrenList;


/**
 * Implementation of {@link org.opensaml.xacml.ctx.AttributeType} objects.
 *
 */
public class XACMLAttributeImpl extends AbstractSAMLObject implements AttributeType {
	

    /** Issuer of the attribute */
    private String Issuer;

    /** AttributeID of the attribute */
    private String AttributeID;

   /** Datatype of the attribute */
    private String Datatype;
 
    /** List of values for this attribute */
    private final XMLObjectChildrenList<XMLObject> attributeValues;
	 
	/**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
    protected XACMLAttributeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributeValues = new XMLObjectChildrenList<XMLObject>(this);
    }

    /** {@inheritDoc} */
	public String getAttributeID() {
		return AttributeID;
	}

	 /** {@inheritDoc} */
	public String getDataType() {
		return Datatype;
	}

	 /** {@inheritDoc} */
	public String getIssuer() {
		return Issuer;
	}

	 /** {@inheritDoc} */
	public void setAttributeID(String attributeId) {
		AttributeID = prepareForAssignment(this.AttributeID, attributeId);
	}

	 /** {@inheritDoc} */
	public void setDataType(String datatype) {
		Datatype = prepareForAssignment(this.Datatype,datatype);
	}

	 /** {@inheritDoc} */
	public void setIssuer(String issuer) {
		Issuer = prepareForAssignment(this.Issuer,issuer);
	}

	 /** {@inheritDoc} */
	public List<XMLObject> getOrderedChildren() {
		ArrayList<XMLObject> children = new ArrayList<XMLObject>();

        children.addAll(attributeValues);

        return Collections.unmodifiableList(children);
	}
	
    /** {@inheritDoc} */
    public List<XMLObject> getAttributeValues() {
        return attributeValues;
    }
}
