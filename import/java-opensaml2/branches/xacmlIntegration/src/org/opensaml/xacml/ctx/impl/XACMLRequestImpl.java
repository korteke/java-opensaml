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
import org.opensaml.xacml.ctx.XACMLAction;
import org.opensaml.xacml.ctx.XACMLEnvironment;
import org.opensaml.xacml.ctx.XACMLRequest;
import org.opensaml.xacml.ctx.XACMLResource;
import org.opensaml.xacml.ctx.XACMLSubject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * A concrete implementation of {@link org.opensaml.xacml.ctx.XACMLRequest}
 */

public class XACMLRequestImpl extends AbstractSAMLObject implements XACMLRequest {
	
    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
	
	/** The subjects of the request */
	private XMLObjectChildrenList<XACMLSubject> subjects;
	
	/** The resources of the request */
	private XMLObjectChildrenList<XACMLResource> resources;
	
	/** The environment of the request */
	private XMLObjectChildrenList<XACMLEnvironment> environment;
	
	/** The action of the request*/
	private XACMLAction action;
	
	
	protected XACMLRequestImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        subjects = new XMLObjectChildrenList<XACMLSubject>(this);
        resources = new XMLObjectChildrenList<XACMLResource>(this);
        environment = new XMLObjectChildrenList<XACMLEnvironment>(this);
        
    }

	/** {@inheritDoc} */
	public XMLObjectChildrenList<XACMLSubject> getSubjects() {
		return subjects;
	}

	
    /** {@inheritDoc} */
	public XMLObjectChildrenList<XACMLResource> getResources() {
		return resources;
	}

	
    /** {@inheritDoc} */
	public XMLObjectChildrenList<XACMLEnvironment> getEnvironment() {
		return environment;
	}

	/** {@inheritDoc} */
	public XACMLAction getAction() {
		return action;
	}

	/** {@inheritDoc} */
	public void setAction(XACMLAction action) {
		this.action = action;
	}
	
	 /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
       
        children.addAll(subjects);
        children.addAll(environment);
        children.addAll(resources);
        
        if(action != null)
        	children.add(action);
        
        return Collections.unmodifiableList(children);
    }

		
}