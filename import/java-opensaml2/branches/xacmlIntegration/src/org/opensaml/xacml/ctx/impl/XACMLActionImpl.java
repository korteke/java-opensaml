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

import java.util.List;

import org.opensaml.common.impl.AbstractSAMLObject;
import org.opensaml.xacml.ctx.XACMLAction;
import org.opensaml.xacml.ctx.XACMLAttribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * Implementation of {@link org.opensaml.xacml.ctx.XACMLAction} objects.
 *
 */

public class XACMLActionImpl extends AbstractSAMLObject implements XACMLAction {

	
	/**Lists of the atrributes in the subject*/
	private XMLObjectChildrenList<XACMLAttribute> attributes;

	/**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
	protected XACMLActionImpl(String namespaceURI, String elementLocalName, String namespacePrefix){
		super(namespaceURI,elementLocalName,namespacePrefix);
		attributes = new XMLObjectChildrenList<XACMLAttribute>(this);
	}
	
	 /** {@inheritDoc} */
	public XMLObjectChildrenList<XACMLAttribute> getAttributes() {
		return attributes;
	}

	 /** {@inheritDoc} */
	public List<XMLObject> getOrderedChildren() {
		return null;
	}

}
