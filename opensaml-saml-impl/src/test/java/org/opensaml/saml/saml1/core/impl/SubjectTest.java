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

package org.opensaml.saml.saml1.core.impl;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.core.SubjectConfirmation;

/**
 * Test for {@link org.opensaml.saml.saml1.core.impl.Subject}
 */
public class SubjectTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    /**
     * Constructor
     */
    public SubjectTest() {
        super();

        singleElementFile = "/data/org/opensaml/saml/saml1/impl/singleSubject.xml";
        childElementsFile = "/data/org/opensaml/saml/saml1/impl/SubjectWithChildren.xml";
        qname = new QName(SAMLConstants.SAML1_NS, Subject.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Subject subject = (Subject) unmarshallElement(singleElementFile);

        AssertJUnit.assertNull("Non zero number of child NameIdentifier elements", subject.getNameIdentifier());
        AssertJUnit.assertNull("Non zero number of child SubjectConfirmation elements", subject.getSubjectConfirmation());
    }

    /**
     * Test an XML file with children
     */
    @Test
    public void testChildElementsUnmarshall() {
        Subject subject = (Subject) unmarshallElement(childElementsFile);

        AssertJUnit.assertNotNull("Zero child NameIdentifier elements", subject.getNameIdentifier());
        AssertJUnit.assertNotNull("Zero child SubjectConfirmation elements", subject.getSubjectConfirmation());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsMarshall() {
        Subject subject = (Subject) buildXMLObject(qname);

        QName oqname = new QName(SAMLConstants.SAML1_NS, NameIdentifier.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        subject.setNameIdentifier((NameIdentifier) buildXMLObject(oqname));
        oqname = new QName(SAMLConstants.SAML1_NS, SubjectConfirmation.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
        subject.setSubjectConfirmation((SubjectConfirmation) buildXMLObject(oqname));

        assertXMLEquals(expectedChildElementsDOM, subject);
    }
}
