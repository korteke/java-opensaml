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

package org.opensaml.saml.saml2.ecp.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.ecp.SubjectConfirmation;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * Test case for creating, marshalling, and unmarshalling {@link SubjectConfirmation}.
 */
public class SubjectConfirmationTest extends XMLObjectProviderBaseTestCase {

    private String expectedMethod;
    private String expectedSOAP11Actor;
    private Boolean expectedSOAP11MustUnderstand;
    
    /** Constructor */
    public SubjectConfirmationTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml/saml2/ecp/impl/SubjectConfirmation.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/ecp/impl/SubjectConfirmationChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedMethod = "conf method";
        expectedSOAP11Actor = "https://soap11actor.example.org";
        expectedSOAP11MustUnderstand = true;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        SubjectConfirmation subjectConfirmation = (SubjectConfirmation) unmarshallElement(singleElementFile);

        String method = subjectConfirmation.getMethod();
        Assert.assertEquals(expectedMethod, method, "Method not as expected");
        Assert.assertEquals(expectedSOAP11MustUnderstand, subjectConfirmation.isSOAP11MustUnderstand(),
                "SOAP mustUnderstand had unxpected value");
        Assert.assertEquals(expectedSOAP11Actor, subjectConfirmation.getSOAP11Actor(),
                "SOAP actor had unxpected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        SubjectConfirmation subjectConfirmation = (SubjectConfirmation) buildXMLObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME);

        subjectConfirmation.setSOAP11Actor(expectedSOAP11Actor);
        subjectConfirmation.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        subjectConfirmation.setMethod(expectedMethod);
        
        assertXMLEquals(expectedDOM, subjectConfirmation);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        SubjectConfirmation subjectConfirmation = (SubjectConfirmation) unmarshallElement(childElementsFile);

        Assert.assertNotNull(subjectConfirmation.getSubjectConfirmationData(), "SubjectConfirmationData element not present");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        SubjectConfirmation subjectConfirmation = (SubjectConfirmation) buildXMLObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME);

        subjectConfirmation.setSOAP11Actor(expectedSOAP11Actor);
        subjectConfirmation.setSOAP11MustUnderstand(expectedSOAP11MustUnderstand);
        subjectConfirmation.setMethod(expectedMethod);
        subjectConfirmation.setSubjectConfirmationData((SubjectConfirmationData) buildXMLObject(SubjectConfirmationData.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, subjectConfirmation);
    }
    
}