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

package org.opensaml.saml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.shibboleth.utilities.java.support.collection.LazyList;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSignableSAMLObject;
import org.opensaml.saml.saml2.common.Extensions;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;

/** Concrete implementation of {@link org.opensaml.saml.saml2.metadata.RoleDescriptor}. */
public abstract class RoleDescriptorImpl extends AbstractSignableSAMLObject implements RoleDescriptor {

    /** ID attribute. */
    private String id;

    /** validUntil attribute. */
    private DateTime validUntil;

    /** cacheDurection attribute. */
    private Long cacheDuration;

    /** Set of supported protocols. */
    private final List<String> supportedProtocols;

    /** Error URL. */
    private String errorURL;

    /** Extensions child. */
    private Extensions extensions;

    /** Organization administering this role. */
    private Organization organization;

    /** "anyAttribute" attributes. */
    private final AttributeMap unknownAttributes;

    /** Contact persons for this role. */
    private final XMLObjectChildrenList<ContactPerson> contactPersons;

    /** Key descriptors for this role. */
    private final XMLObjectChildrenList<KeyDescriptor> keyDescriptors;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RoleDescriptorImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        supportedProtocols = new LazyList<String>();
        contactPersons = new XMLObjectChildrenList<ContactPerson>(this);
        keyDescriptors = new XMLObjectChildrenList<KeyDescriptor>(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getID() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public void setID(String newID) {
        String oldID = this.id;
        this.id = prepareForAssignment(this.id, newID);
        registerOwnID(oldID, this.id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid() {
        if (null == validUntil) {
            return true;
        }
        
        DateTime now = new DateTime();
        return now.isBefore(validUntil);
    }

    /** {@inheritDoc} */
    @Override
    public DateTime getValidUntil() {
        return validUntil;
    }

    /** {@inheritDoc} */
    @Override
    public void setValidUntil(DateTime dt) {
        validUntil = prepareForAssignment(validUntil, dt);
    }

    /** {@inheritDoc} */
    @Override
    public Long getCacheDuration() {
        return cacheDuration;
    }

    /** {@inheritDoc} */
    @Override
    public void setCacheDuration(Long duration) {
        cacheDuration = prepareForAssignment(cacheDuration, duration);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getSupportedProtocols() {
        return Collections.unmodifiableList(supportedProtocols);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportedProtocol(String protocol) {
        return supportedProtocols.contains(protocol);
    }

    /** {@inheritDoc} */
    @Override
    public void addSupportedProtocol(final String protocol) {
        final String trimmed = StringSupport.trimOrNull(protocol);
        if (trimmed != null && !supportedProtocols.contains(trimmed)) {
            releaseThisandParentDOM();
            supportedProtocols.add(trimmed);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeSupportedProtocol(final String protocol) {
        final String trimmed = StringSupport.trimOrNull(protocol);
        if (trimmed != null && supportedProtocols.contains(trimmed)) {
            releaseThisandParentDOM();
            supportedProtocols.remove(trimmed);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeSupportedProtocols(Collection<String> protocols) {
        for (String protocol : protocols) {
            removeSupportedProtocol(protocol);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllSupportedProtocols() {
        supportedProtocols.clear();
    }

    /** {@inheritDoc} */
    @Override
    public String getErrorURL() {
        return errorURL;
    }

    /** {@inheritDoc} */
    @Override
    public void setErrorURL(String url) {

        errorURL = prepareForAssignment(errorURL, url);
    }

    /** {@inheritDoc} */
    @Override
    public Extensions getExtensions() {
        return extensions;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtensions(Extensions ext) {
        extensions = prepareForAssignment(extensions, ext);
    }

    /** {@inheritDoc} */
    @Override
    public Organization getOrganization() {
        return organization;
    }

    /** {@inheritDoc} */
    @Override
    public void setOrganization(Organization org) {
        organization = prepareForAssignment(organization, org);
    }

    /** {@inheritDoc} */
    @Override
    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    /** {@inheritDoc} */
    @Override
    public List<KeyDescriptor> getKeyDescriptors() {
        return keyDescriptors;
    }

    /** {@inheritDoc} */
    @Override
    public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Override
    public String getSignatureReferenceID() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();

        if (getSignature() != null) {
            children.add(getSignature());
        }

        if (extensions != null) {
            children.add(getExtensions());
        }
        children.addAll(getKeyDescriptors());
        if (organization != null) {
            children.add(getOrganization());
        }
        children.addAll(getContactPersons());

        return Collections.unmodifiableList(children);
    }
}