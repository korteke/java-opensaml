/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

package org.opensaml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml2.metadata.AttributeProfile;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.NameIDMappingService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSBooleanValue;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * Concrete implementation of {@link org.opensaml.saml2.metadata.IDPSSODescriptor}
 */
public class IDPSSODescriptorImpl extends SSODescriptorImpl implements IDPSSODescriptor {

    /** wantAuthnRequestSigned attribute */
    private XSBooleanValue wantAuthnRequestsSigned;

    /** SingleSignOn services for this entity */
    private final XMLObjectChildrenList<SingleSignOnService> singleSignOnServices;

    /** NameID mapping services for this entity */
    private final XMLObjectChildrenList<NameIDMappingService> nameIDMappingServices;

    /** AssertionID request services for this entity */
    private final XMLObjectChildrenList<AssertionIDRequestService> assertionIDRequestServices;

    /** Attribute profiles supported by this entity */
    private final XMLObjectChildrenList<AttributeProfile> attributeProfiles;

    /** Attributes accepted by this entity */
    private final XMLObjectChildrenList<Attribute> attributes;
    
    /**
     * Constructor
     * 
     * @param namespaceURI
     * @param elementLocalName
     * @param namespacePrefix
     */
    protected IDPSSODescriptorImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        singleSignOnServices = new XMLObjectChildrenList<SingleSignOnService>(this);
        nameIDMappingServices = new XMLObjectChildrenList<NameIDMappingService>(this);
        assertionIDRequestServices = new XMLObjectChildrenList<AssertionIDRequestService>(this);
        attributeProfiles = new XMLObjectChildrenList<AttributeProfile>(this);
        attributes = new XMLObjectChildrenList<Attribute>(this);
    }

    /*
     * @see org.opensaml.saml2.metadata.IDPSSODescriptor#wantAuthnRequestsSigned()
     */
    public XSBooleanValue wantAuthnRequestsSigned() {
        return wantAuthnRequestsSigned;
    }

    /*
     * @see org.opensaml.saml2.metadata.IDPSSODescriptor#setWantAuthnRequestSigned(boolean)
     */
    public void setWantAuthnRequestSigned(XSBooleanValue wantSigned) {
        wantAuthnRequestsSigned = prepareForAssignment(wantAuthnRequestsSigned, wantSigned);
    }

    /*
     * @see org.opensaml.saml2.metadata.IDPSSODescriptor#getSingleSignOnServices()
     */
    public List<SingleSignOnService> getSingleSignOnServices() {
        return singleSignOnServices;
    }

    /*
     * @see org.opensaml.saml2.metadata.IDPSSODescriptor#getNameIDMappingServices()
     */
    public List<NameIDMappingService> getNameIDMappingServices() {
        return nameIDMappingServices;
    }

    /*
     * @see org.opensaml.saml2.metadata.IDPSSODescriptor#getAssertionIDRequestServices()
     */
    public List<AssertionIDRequestService> getAssertionIDRequestServices() {
        return assertionIDRequestServices;
    }

    /*
     * @see org.opensaml.saml2.metadata.IDPSSODescriptor#getAttributeProfiles()
     */
    public List<AttributeProfile> getAttributeProfiles() {
        return attributeProfiles;
    }

    /*
     * @see org.opensaml.saml2.metadata.IDPSSODescriptor#getAttributes()
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /*
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();

        children.addAll(super.getOrderedChildren());
        children.addAll(singleSignOnServices);
        children.addAll(nameIDMappingServices);
        children.addAll(assertionIDRequestServices);
        children.addAll(attributeProfiles);
        children.addAll(attributes);

        return Collections.unmodifiableList(children);
    }
}