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
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.Company;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml.saml2.metadata.EmailAddress;
import org.opensaml.saml.saml2.metadata.GivenName;
import org.opensaml.saml.saml2.metadata.SurName;
import org.opensaml.saml.saml2.metadata.TelephoneNumber;

/**
 * Concrete implementation of {@link org.opensaml.saml.saml2.metadata.ContactPerson}.
 */
public class ContactPersonImpl extends AbstractSAMLObject implements ContactPerson {

    /** Contact person type. */
    private ContactPersonTypeEnumeration type;

    /** Extensions child object. */
    private Extensions extensions;

    /** Company child element. */
    private Company company;

    /** GivenName child objectobject. */
    private GivenName givenName;

    /** SurName child object. */
    private SurName surName;
    
    /** "anyAttribute" attributes. */
    private final AttributeMap unknownAttributes;

    /** Child email address. */
    private final XMLObjectChildrenList<EmailAddress> emailAddresses;

    /** Child telephone numbers. */
    private final XMLObjectChildrenList<TelephoneNumber> telephoneNumbers;

    /**
     * Constructor.
     * 
     * @param namespaceURI name space
     * @param elementLocalName local name
     * @param namespacePrefix prefix
     */
    protected ContactPersonImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        emailAddresses = new XMLObjectChildrenList<>(this);
        telephoneNumbers = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Override
    public ContactPersonTypeEnumeration getType() {
        return type;
    }

    /** {@inheritDoc} */
    @Override
    public void setType(ContactPersonTypeEnumeration theType) {
        this.type = prepareForAssignment(this.type, theType);
    }

    /** {@inheritDoc} */
    @Override
    public Extensions getExtensions() {
        return extensions;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtensions(Extensions theExtensions) {
        this.extensions = prepareForAssignment(this.extensions, theExtensions);
    }

    /** {@inheritDoc} */
    @Override
    public Company getCompany() {
        return company;
    }

    /** {@inheritDoc} */
    @Override
    public void setCompany(Company theCompany) {
        this.company = prepareForAssignment(this.company, theCompany);
    }

    /** {@inheritDoc} */
    @Override
    public GivenName getGivenName() {
        return givenName;
    }

    /** {@inheritDoc} */
    @Override
    public void setGivenName(GivenName name) {
        givenName = prepareForAssignment(givenName, name);
    }

    /** {@inheritDoc} */
    @Override
    public SurName getSurName() {
        return surName;
    }

    /** {@inheritDoc} */
    @Override
    public void setSurName(SurName name) {
        surName = prepareForAssignment(surName, name);
    }

    /** {@inheritDoc} */
    @Override
    public List<EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    /** {@inheritDoc} */
    @Override
    public List<TelephoneNumber> getTelephoneNumbers() {
        return telephoneNumbers;
    }
    
    /** {@inheritDoc} */
    @Override
    public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Override
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<>();

        children.add(extensions);
        children.add(company);
        children.add(givenName);
        children.add(surName);
        children.addAll(emailAddresses);
        children.addAll(telephoneNumbers);

        return children;
    }
}