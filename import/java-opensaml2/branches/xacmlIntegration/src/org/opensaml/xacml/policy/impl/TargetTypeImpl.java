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
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * Implementing {@link org.opensaml.xacml.policy.TargetType}
 */
public class TargetTypeImpl extends AbstractSAMLObject implements TargetType {

    /** List of the actions in the policy. */
    private XMLObjectChildrenList<ActionsType> actions;

    /** List of the environments in the policy. */
    private XMLObjectChildrenList<EnvironmentsType> environments;

    /** List of the subjects in the policy. */
    private XMLObjectChildrenList<SubjectsType> subjects;

    /** List of the resourcese in the policy. */
    private XMLObjectChildrenList<ResourcesType> resources;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected TargetTypeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        actions = new XMLObjectChildrenList<ActionsType>(this);
        subjects = new XMLObjectChildrenList<SubjectsType>(this);
        resources = new XMLObjectChildrenList<ResourcesType>(this);
        environments = new XMLObjectChildrenList<EnvironmentsType>(this);
    }

    /** {@inheritDoc}* */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();

        children.addAll(subjects);
        children.addAll(actions);
        children.addAll(resources);
        children.addAll(environments);

        return Collections.unmodifiableList(children);
    }

    /** {@inheritDoc}* */
    public List<SubjectsType> getSubjects() {
        return subjects;
    }

    /** {@inheritDoc}* */
    public List<ResourcesType> getResources() {
        return resources;
    }

    /** {@inheritDoc}* */
    public List<ActionsType> getActions() {
        return actions;
    }

    /** {@inheritDoc}* */
    public List<EnvironmentsType> getEnvironments() {
        return environments;
    }

}
