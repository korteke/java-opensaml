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
import org.opensaml.xacml.ctx.XACMLAttribute;
import org.opensaml.xacml.ctx.XACMLSubject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLObjectChildrenList;


/**
 * Implementation of {@link org.opensaml.xacml.ctx.XACMLSubject} objects.
 *
 */

public class XACMLSubjectImpl extends AbstractSAMLObject implements
		XACMLSubject {
	
	/** Subject category of the Subject*/
	private String SubjectCategory;
	
	/**Lists of the atrributes in the subject*/
	private XMLObjectChildrenList<XACMLAttribute> attributes;

		 
	/**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
	protected XACMLSubjectImpl(String namespaceURI, String elementLocalName, String namespacePrefix){
		super(namespaceURI,elementLocalName,namespacePrefix);
		SubjectCategory = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
		
		attributes = new XMLObjectChildrenList<XACMLAttribute>(this);
	}
	
	 /** {@inheritDoc} */
	public String getSubjectCategory() {
		return SubjectCategory;
	}

	 /** {@inheritDoc} */
	public void setSubjectCategory(String subjectCategory) {
		SubjectCategory = prepareForAssignment(this.SubjectCategory,subjectCategory);
	}
	
	/** {@inheritDoc} */
	public List<XMLObject> getOrderedChildren() {
		ArrayList<XMLObject> children = new ArrayList<XMLObject>();
		
		children.addAll(attributes);		
				
		return Collections.unmodifiableList(children);
	}	

	 /** {@inheritDoc} */
	public XMLObjectChildrenList<XACMLAttribute> getAttributes() {
		return attributes;
	}	

}
