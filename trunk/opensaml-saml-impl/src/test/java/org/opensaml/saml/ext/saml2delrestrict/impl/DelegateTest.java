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

package org.opensaml.saml.ext.saml2delrestrict.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2delrestrict.Delegate;
import org.opensaml.saml.saml2.core.NameID;

/**
 * Test case for creating, marshalling, and unmarshalling {@link Delegate}.
 */
public class DelegateTest extends XMLObjectProviderBaseTestCase {

    private DateTime expectedDelegationInstant;
    
    private String expectedConfirmationMethod;
    

    /** Constructor */
    public DelegateTest() {
        singleElementFile = "/data/org/opensaml/saml/ext/saml2delrestrict/impl/Delegate.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/ext/saml2delrestrict/impl/DelegateOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/ext/saml2delrestrict/impl/DelegateChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedDelegationInstant = new DateTime(1984, 8, 26, 10, 01, 30, 43, ISOChronology.getInstanceUTC());
        expectedConfirmationMethod = "urn:oasis:names:tc:SAML:2.0:cm:bearer";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Delegate delegate = (Delegate) unmarshallElement(singleElementFile);

        AssertJUnit.assertNotNull(delegate);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Delegate delegate = (Delegate) unmarshallElement(singleElementOptionalAttributesFile);
        
        AssertJUnit.assertNotNull(delegate);

        DateTime instant = delegate.getDelegationInstant();
        AssertJUnit.assertEquals("DelegationInstant was unexpected value", expectedDelegationInstant, instant);

        String cm = delegate.getConfirmationMethod();
        AssertJUnit.assertEquals("ConfirmationMethod was unexpected value", expectedConfirmationMethod, cm);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Delegate delegate = (Delegate) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull(delegate);
        
        AssertJUnit.assertNotNull("NameID was null", delegate.getNameID());
        AssertJUnit.assertNull("BaseID was non-null", delegate.getBaseID());
        AssertJUnit.assertNull("EncryptedID was non-null", delegate.getEncryptedID());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Delegate delegate = (Delegate) buildXMLObject(Delegate.DEFAULT_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, delegate);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        Delegate delegate = (Delegate) buildXMLObject(Delegate.DEFAULT_ELEMENT_NAME);
        
        delegate.setConfirmationMethod(expectedConfirmationMethod);
        delegate.setDelegationInstant(expectedDelegationInstant);

        assertXMLEquals(expectedOptionalAttributesDOM, delegate);
    }



    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Delegate delegate = (Delegate) buildXMLObject(Delegate.DEFAULT_ELEMENT_NAME);
        
        delegate.setNameID((NameID) buildXMLObject(NameID.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, delegate);
    }
}