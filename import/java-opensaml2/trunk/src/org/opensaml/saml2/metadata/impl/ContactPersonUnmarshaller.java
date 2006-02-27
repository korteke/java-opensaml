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

import org.opensaml.common.impl.AbstractSAMLObjectUnmarshaller;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Extensions;
import org.opensaml.saml2.metadata.Company;
import org.opensaml.saml2.metadata.ContactPerson;
import org.opensaml.saml2.metadata.ContactPersonType;
import org.opensaml.saml2.metadata.EmailAddress;
import org.opensaml.saml2.metadata.GivenName;
import org.opensaml.saml2.metadata.SurName;
import org.opensaml.saml2.metadata.TelephoneNumber;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.saml2.metadata.ContactPerson} objects.
 */
public class ContactPersonUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /**
     * Constructor
     */
    public ContactPersonUnmarshaller() {
        super(SAMLConstants.SAML20MD_NS, ContactPerson.LOCAL_NAME);
    }

    /*
     * @see org.opensaml.xml.io.AbstractXMLObjectUnmarshaller#processChildElement(org.opensaml.xml.XMLObject,
     *      org.opensaml.xml.XMLObject)
     */
    protected void processChildElement(XMLObject parentSAMLObject, XMLObject childSAMLObject)
            throws UnmarshallingException {
        ContactPerson person = (ContactPerson) parentSAMLObject;

        if (childSAMLObject instanceof Extensions) {
            person.setExtensions((Extensions) childSAMLObject);
        } else if (childSAMLObject instanceof Company) {
            person.setCompany((Company) childSAMLObject);
        } else if (childSAMLObject instanceof GivenName) {
            person.setGivenName((GivenName) childSAMLObject);
        } else if (childSAMLObject instanceof SurName) {
            person.setSurName((SurName) childSAMLObject);
        } else if (childSAMLObject instanceof EmailAddress) {
            person.getEmailAddresses().add((EmailAddress) childSAMLObject);
        } else if (childSAMLObject instanceof TelephoneNumber) {
            person.getTelephoneNumbers().add((TelephoneNumber) childSAMLObject);
        } else {
            super.processChildElement(parentSAMLObject, childSAMLObject);
        }
    }

    /*
     * @see org.opensaml.xml.io.AbstractXMLObjectUnmarshaller#processAttribute(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Attr)
     */
    protected void processAttribute(XMLObject samlObject, Attr attribute) throws UnmarshallingException {
        ContactPerson person = (ContactPerson) samlObject;

        if (attribute.getLocalName().equals(ContactPerson.CONTACT_TYPE_ATTRIB_NAME)) {
            if (ContactPersonType.TECHNICAL.toString().equals(attribute.getValue())) {
                person.setType(ContactPersonType.TECHNICAL);
            } else if (ContactPersonType.SUPPORT.toString().equals(attribute.getValue())) {
                person.setType(ContactPersonType.SUPPORT);
            } else if (ContactPersonType.ADMINISTRATIVE.toString().equals(attribute.getValue())) {
                person.setType(ContactPersonType.ADMINISTRATIVE);
            } else if (ContactPersonType.BILLING.toString().equals(attribute.getValue())) {
                person.setType(ContactPersonType.BILLING);
            } else if (ContactPersonType.OTHER.toString().equals(attribute.getValue())) {
                person.setType(ContactPersonType.OTHER);
            } else {
                super.processAttribute(samlObject, attribute);
            }
        } else {
            super.processAttribute(samlObject, attribute);
        }
    }
}