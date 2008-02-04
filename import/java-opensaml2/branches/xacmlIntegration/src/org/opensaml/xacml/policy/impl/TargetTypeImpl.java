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
import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.EnvironmentsType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xml.XMLObject;

/**
 * Implementing {@link org.opensaml.xacml.policy.TargetType}.
 */
public class TargetTypeImpl extends AbstractSAMLObject implements TargetType {

    /** The actions in the policy. */
    private ActionsType actions;

    /** The environments in the policy. */
    private EnvironmentsType environments;

    /** The subjects in the policy. */
    private SubjectsType subjects;

    /** The resourcese in the policy. */
    private ResourcesType resources;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected TargetTypeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);       
    }


    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();

        if(subjects != null){
            children.add(subjects);  
        }
        if(actions != null){
            children.add(actions);  
        }        
        if(resources != null){
            children.add(resources);  
        }
        if(environments != null){
            children.add(environments);  
        }
        return Collections.unmodifiableList(children);
    }

    /** {@inheritDoc}*/
    public SubjectsType getSubjects() {
        return subjects;
    }
        
    /** {@inheritDoc}*/
    public ResourcesType getResources() {
        return resources;
    }

    /** {@inheritDoc}*/
    public ActionsType getActions() {
        return actions;
    }

    /**{@inheritDoc}*/
    public EnvironmentsType getEnvironments() {
        return environments;
    }

    /**{@inheritDoc}*/
    public void setActions(ActionsType actions) {
        this.actions = prepareForAssignment(this.actions,actions);
    }

    /**{@inheritDoc}*/
    public void setEnvironments(EnvironmentsType environments) {
        this.environments = prepareForAssignment(this.environments,environments);
    }

    /**{@inheritDoc}*/
    public void setResources(ResourcesType resources) {
        this.resources = prepareForAssignment(this.resources,resources);
    }

    /**{@inheritDoc}*/
    public void setSubjects(SubjectsType subjects) {
        this.subjects = prepareForAssignment(this.subjects,subjects);
    }
}
