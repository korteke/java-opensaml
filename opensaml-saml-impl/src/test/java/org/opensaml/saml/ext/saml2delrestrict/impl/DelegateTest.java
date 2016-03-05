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
import org.testng.Assert;
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
        singleElementFile = "/org/opensaml/saml/ext/saml2delrestrict/impl/Delegate.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/ext/saml2delrestrict/impl/DelegateOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/ext/saml2delrestrict/impl/DelegateChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedDelegationInstant = new DateTime(1984, 8, 26, 10, 01, 30, 43, ISOChronology.getInstanceUTC());
        expectedConfirmationMethod = "urn:oasis:names:tc:SAML:2.0:cm:bearer";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Delegate delegate = (Delegate) unmarshallElement(singleElementFile);

        Assert.assertNotNull(delegate);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Delegate delegate = (Delegate) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(delegate);

        DateTime instant = delegate.getDelegationInstant();
        Assert.assertEquals(instant, expectedDelegationInstant, "DelegationInstant was unexpected value");

        String cm = delegate.getConfirmationMethod();
        Assert.assertEquals(cm, expectedConfirmationMethod, "ConfirmationMethod was unexpected value");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Delegate delegate = (Delegate) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(delegate);
        
        Assert.assertNotNull(delegate.getNameID(), "NameID was null");
        Assert.assertNull(delegate.getBaseID(), "BaseID was non-null");
        Assert.assertNull(delegate.getEncryptedID(), "EncryptedID was non-null");
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