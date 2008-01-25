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
import org.opensaml.xacml.ctx.XACMLStatus;
import org.opensaml.xacml.ctx.XACMLStatusCode;
import org.opensaml.xacml.ctx.XACMLStatusDetail;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSString;


/**
 * Implementation of {@link org.opensaml.xacml.ctx.XACMLStatus} objects.
 *
 */

public class XACMLStatusImpl extends AbstractSAMLObject implements XACMLStatus {

	/** Status code*/
	private XACMLStatusCode statusCode;
	
	/** The staus message */
	private XSString statusMessage;
	
	/**Staus detail element*/
	private XACMLStatusDetail statusDetail;
	
	/**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
	protected XACMLStatusImpl(String namespaceURI, String elementLocalName, String namespacePrefix){
		super(namespaceURI,elementLocalName,namespacePrefix);
	}
	
	 /** {@inheritDoc} */
	public XACMLStatusCode getStatusCode() {
		return statusCode;
	}

	 /** {@inheritDoc} */
	public void setStatusCode(XACMLStatusCode newStatusCode) {
		statusCode = newStatusCode;
	}

	 /** {@inheritDoc} */
	public List<XMLObject> getOrderedChildren() {
		ArrayList<XMLObject> children = new ArrayList<XMLObject>();
		
		if(statusCode != null)
			children.add(statusCode);
		if(statusMessage != null)
			children.add(statusMessage);
		if(statusDetail != null)
			children.add(statusDetail);
		
		return Collections.unmodifiableList(children);
	}

	 /** {@inheritDoc} */
	public XSString getStatusMessage() {
		return statusMessage;
	}

	 /** {@inheritDoc} */
	public void setStatusMessage(XSString statusMessage) {
		this.statusMessage = statusMessage;
	}

	 /** {@inheritDoc} */
	public XACMLStatusDetail getStatusDetail() {
		return statusDetail;
	}

	 /** {@inheritDoc} */
	public void setStatusDetail(XACMLStatusDetail statusDetail) {
		this.statusDetail = statusDetail;
	}

}
