
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
import org.opensaml.xacml.ctx.ActionType;
import org.opensaml.xacml.ctx.EnvironmentType;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResourceType;
import org.opensaml.xacml.ctx.SubjectType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * A concrete implementation of {@link org.opensaml.xacml.ctx.RequestType}
 */

public class XACMLRequestImpl extends AbstractSAMLObject implements RequestType {
	
    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
	
	/** The subjects of the request */
	private XMLObjectChildrenList<SubjectType> subjects;
	
	/** The resources of the request */
	private XMLObjectChildrenList<ResourceType> resources;
	
	/** The environment of the request */
	private XMLObjectChildrenList<EnvironmentType> environment;
	
	/** The action of the request*/
	private ActionType action;
	
	
	protected XACMLRequestImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        subjects = new XMLObjectChildrenList<SubjectType>(this);
        resources = new XMLObjectChildrenList<ResourceType>(this);
        environment = new XMLObjectChildrenList<EnvironmentType>(this);
        
    }

	/** {@inheritDoc} */
	public List<SubjectType> getSubjects() {
		return subjects;
	}

	
    /** {@inheritDoc} */
	public List<ResourceType> getResources() {
		return resources;
    }
	public List<EnvironmentType> getEnvironment() {
		return environment;
	}

	/** {@inheritDoc} */
	public ActionType getAction() {
		return action;
	}

	/** {@inheritDoc} */
	public void setAction(ActionType action) {
		this.action = prepareForAssignment(this.action,action);
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


