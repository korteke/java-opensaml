/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */

package org.opensaml.saml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.saml2.common.Extensions;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;

/**
 * Concrete implementation of {@link org.opensaml.saml.saml2.metadata.Organization}.
 */
public class OrganizationImpl extends AbstractSAMLObject implements Organization {

    /** element extensions. */
    private Extensions extensions;

    /** OrganizationName children. */
    private final XMLObjectChildrenList<OrganizationName> names;

    /** OrganizationDisplayName children. */
    private final XMLObjectChildrenList<OrganizationDisplayName> displayNames;

    /** OrganizationURL children. */
    private final XMLObjectChildrenList<OrganizationURL> urls;
    
    /** "anyAttribute" attributes. */
    private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected OrganizationImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        names = new XMLObjectChildrenList<OrganizationName>(this);
        displayNames = new XMLObjectChildrenList<OrganizationDisplayName>(this);
        urls = new XMLObjectChildrenList<OrganizationURL>(this);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Override
    public Extensions getExtensions() {
        return extensions;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtensions(Extensions newExtensions) {
        this.extensions = prepareForAssignment(this.extensions, newExtensions);
    }

    /** {@inheritDoc} */
    @Override
    public List<OrganizationName> getOrganizationNames() {
        return names;
    }

    /** {@inheritDoc} */
    @Override
    public List<OrganizationDisplayName> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public List<OrganizationURL> getURLs() {
        return urls;
    }
    
    /** {@inheritDoc} */
    @Override
    public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Override
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();

        children.add(extensions);
        children.addAll(names);
        children.addAll(displayNames);
        children.addAll(urls);

        return children;
    }
}