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
import org.opensaml.xacml.ctx.StatusType;
import org.opensaml.xacml.ctx.StatusCodeType;
import org.opensaml.xacml.ctx.StatusDetailType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSString;


/**
 * Implementation of {@link org.opensaml.xacml.ctx.StatusType} objects.
 *
 */

public class XACMLStatusImpl extends AbstractSAMLObject implements StatusType {

	/** Status code*/
	private StatusCodeType statusCode;
	
	/** The staus message */
	private XSString statusMessage;
	
	/**Staus detail element*/
	private StatusDetailType statusDetail;
	
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
	public StatusCodeType getStatusCode() {
		return statusCode;
	}

	 /** {@inheritDoc} */
	public void setStatusCode(StatusCodeType newStatusCode) {
		statusCode = prepareForAssignment(this.statusCode, newStatusCode);
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
		this.statusMessage = prepareForAssignment(this.statusMessage,statusMessage);
	}

	 /** {@inheritDoc} */
	public StatusDetailType getStatusDetail() {
		return statusDetail;
	}

	 /** {@inheritDoc} */
	public void setStatusDetail(StatusDetailType statusDetail) {
		this.statusDetail = prepareForAssignment(this.statusDetail,statusDetail);
	}

}
