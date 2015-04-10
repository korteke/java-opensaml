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
package org.opensaml.saml.ext.saml2mdrpi.impl;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.ext.saml2mdrpi.PublicationInfo;
import org.opensaml.saml.ext.saml2mdrpi.UsagePolicy;

/**
 * Concrete {@link PublicationInfo}.
 */
public class PublicationInfoImpl extends AbstractSAMLObject implements PublicationInfo {

    /** The policies. */
    private XMLObjectChildrenList<UsagePolicy> usagePolicies;

    /** The publisher. */
    private String publisher;

    /** The creation instant. */
    private DateTime creationInstant;

    /** The publicationId. */
    private String publicationId;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected PublicationInfoImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        usagePolicies = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getPublisher() {
        return publisher;
    }

    /** {@inheritDoc} */
    @Override
    public void setPublisher(String thePublisher) {
        publisher = prepareForAssignment(publisher, thePublisher);
    }

    /** {@inheritDoc} */
    @Override
    public DateTime getCreationInstant() {
        return creationInstant;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreationInstant(DateTime dateTime) {
        creationInstant = prepareForAssignment(creationInstant, dateTime);
    }

    /** {@inheritDoc} */
    @Override
    public String getPublicationId() {
        return publicationId;
    }

    /** {@inheritDoc} */
    @Override
    public void setPublicationId(String id) {
        publicationId = prepareForAssignment(publicationId, id);
    }

    /** {@inheritDoc} */
    @Override
    public List<UsagePolicy> getUsagePolicies() {
        return usagePolicies;
    }

    /** {@inheritDoc} */
    @Override
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<>();
        children.addAll(usagePolicies);
        return children;
    }

}
