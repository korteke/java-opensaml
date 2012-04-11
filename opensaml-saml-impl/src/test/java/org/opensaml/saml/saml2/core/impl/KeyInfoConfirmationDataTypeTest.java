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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.config.SAMLConfigurationSupport;
import org.opensaml.saml.saml2.core.KeyInfoConfirmationDataType;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.KeyInfoConfirmationDataTypeImpl}.
 */
public class KeyInfoConfirmationDataTypeTest extends XMLObjectProviderBaseTestCase {

    /** Expected NotBefore value. */
    private DateTime expectedNotBefore;

    /** Expected NotOnOrAfter value. */
    private DateTime expectedNotOnOrAfter;

    /** Expected Recipient value. */
    private String expectedRecipient;

    /** Expected InResponseTo value. */
    private String expectedInResponseTo;

    /** Expected Address value. */
    private String expectedAddress;
    
    /** Expected xsi:type value. */
    private QName expectedType;
    
    /** Expected number of KeyInfo child elements. */
    private int expectedNumKeyInfoChildren;

    /** Constructor. */
    public KeyInfoConfirmationDataTypeTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/KeyInfoConfirmationDataType.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/KeyInfoConfirmationDataTypeOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/core/impl/KeyInfoConfirmationDataTypeChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedNotBefore = new DateTime(1984, 8, 26, 10, 01, 30, 43, ISOChronology.getInstanceUTC());
        expectedNotOnOrAfter = new DateTime(1984, 8, 26, 10, 11, 30, 43, ISOChronology.getInstanceUTC());
        expectedRecipient = "recipient";
        expectedInResponseTo = "inresponse";
        expectedAddress = "address";
        expectedType = new QName(SAMLConstants.SAML20_NS, "KeyInfoConfirmationDataType", SAMLConstants.SAML20_PREFIX);
        expectedNumKeyInfoChildren = 3;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        KeyInfoConfirmationDataType kicd = (KeyInfoConfirmationDataType) unmarshallElement(singleElementFile);
        Assert.assertNotNull(kicd, "Object was null");
        
        Assert.assertEquals(kicd.getSchemaType(), expectedType, "Object xsi:type was not the expected value");

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        KeyInfoConfirmationDataType kicd = (KeyInfoConfirmationDataType) unmarshallElement(singleElementOptionalAttributesFile);

        DateTime notBefore = kicd.getNotBefore();
        Assert.assertEquals(notBefore, expectedNotBefore, "NotBefore was " + notBefore + ", expected " + expectedNotBefore);

        DateTime notOnOrAfter = kicd.getNotOnOrAfter();
        Assert.assertEquals(notOnOrAfter, expectedNotOnOrAfter,
                "NotOnOrAfter was " + notOnOrAfter + ", expected " + expectedNotOnOrAfter);

        String recipient = kicd.getRecipient();
        Assert.assertEquals(recipient, expectedRecipient, "Recipient was " + recipient + ", expected " + expectedRecipient);

        String inResponseTo = kicd.getInResponseTo();
        Assert.assertEquals(inResponseTo, expectedInResponseTo,
                "InResponseTo was " + inResponseTo + ", expected " + expectedInResponseTo);

        String address = kicd.getAddress();
        Assert.assertEquals(address, expectedAddress, "Address was " + address + ", expected " + expectedAddress);
        
        Assert.assertEquals(kicd.getSchemaType(), expectedType, "Object xsi:type was not the expected value");
    }
    
    @Test
    public void testChildElementsUnmarshall() {
        KeyInfoConfirmationDataType kicd = (KeyInfoConfirmationDataType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(kicd.getKeyInfos().size(), 3, "Unexpected number of KeyInfo children");
        Assert.assertEquals(kicd.getUnknownXMLObjects(KeyInfo.DEFAULT_ELEMENT_NAME).size(), 3, "Unexpected number of KeyInfo children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        KeyInfoConfirmationDataType kicd = buildXMLObject();

        assertXMLEquals(expectedDOM, kicd);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        KeyInfoConfirmationDataType kicd = buildXMLObject();

        kicd.setNotBefore(expectedNotBefore);
        kicd.setNotOnOrAfter(expectedNotOnOrAfter);
        kicd.setRecipient(expectedRecipient);
        kicd.setInResponseTo(expectedInResponseTo);
        kicd.setAddress(expectedAddress);

        assertXMLEquals(expectedOptionalAttributesDOM, kicd);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        KeyInfoConfirmationDataType kicd = buildXMLObject();
        
        for (int i=0; i<expectedNumKeyInfoChildren; i++) {
            KeyInfo keyinfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
            kicd.getKeyInfos().add(keyinfo);
        }
        
        assertXMLEquals(expectedChildElementsDOM, kicd);
    }
    
    /** {@inheritDoc} */
    public KeyInfoConfirmationDataType buildXMLObject() {
        SAMLObjectBuilder builder = 
            (SAMLObjectBuilder) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(KeyInfoConfirmationDataType.TYPE_NAME);
        
        if(builder == null){
            Assert.fail("Unable to retrieve builder for object QName " + KeyInfoConfirmationDataType.TYPE_NAME);
        }
        return (KeyInfoConfirmationDataType) builder.buildObject();
    }

}