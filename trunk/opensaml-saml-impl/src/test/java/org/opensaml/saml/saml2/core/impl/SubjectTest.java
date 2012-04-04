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

package org.opensaml.saml.saml2.core.impl;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.SubjectImpl}.
 */
public class SubjectTest extends XMLObjectProviderBaseTestCase {

    /** Count of SubjectConfirmation subelements */
    protected int expectedSubjectConfirmationCount = 2;

    /** Constructor */
    public SubjectTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/Subject.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/SubjectChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Subject subject = (Subject) unmarshallElement(singleElementFile);

        Assert.assertNotNull(subject);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        Subject subject = (Subject) buildXMLObject(qname);

        assertXMLEquals(expectedDOM, subject);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Subject subject = (Subject) unmarshallElement(childElementsFile);
        Assert.assertNotNull(subject.getNameID(), "Identifier element not present");
        Assert.assertEquals(subject
                .getSubjectConfirmations().size(), expectedSubjectConfirmationCount, "SubjectConfirmation Count not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        Subject subject = (Subject) buildXMLObject(qname);

        QName nameIDQName = new QName(SAMLConstants.SAML20_NS, NameID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        subject.setNameID((NameID) buildXMLObject(nameIDQName));
        
        QName subjectConfirmationQName = new QName(SAMLConstants.SAML20_NS, SubjectConfirmation.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i < expectedSubjectConfirmationCount; i++) {
            subject.getSubjectConfirmations().add((SubjectConfirmation) buildXMLObject(subjectConfirmationQName));
        }

        assertXMLEquals(expectedChildElementsDOM, subject);
    }
}