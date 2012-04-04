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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.common.Extensions;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AuthzService;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.PDPDescriptor;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.metadata.impl.PDPDescriptorImpl}.
 */
public class PDPDescriptorTest extends XMLObjectProviderBaseTestCase {

    /** List of expected supported protocols */
    protected ArrayList<String> expectedSupportedProtocol;

    /** Expected cacheDuration value in miliseconds */
    protected long expectedCacheDuration;

    /** Expected validUntil value */
    protected DateTime expectedValidUntil;

    /** Expected error url */
    protected String expectedErrorURL;

    /**
     * Constructor
     */
    public PDPDescriptorTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/PDPDescriptor.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/PDPDescriptorOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/saml/saml2/metadata/impl/PDPDescriptorChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedSupportedProtocol = new ArrayList<String>();
        expectedSupportedProtocol.add("urn:foo:bar");
        expectedSupportedProtocol.add("urn:fooz:baz");

        expectedCacheDuration = 90000;
        expectedValidUntil = new DateTime(2005, 12, 7, 10, 21, 0, 0, ISOChronology.getInstanceUTC());

        expectedErrorURL = "http://example.org";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        PDPDescriptor descriptor = (PDPDescriptor) unmarshallElement(singleElementFile);

        AssertJUnit.assertEquals("Supported protocols not equal to expected value", expectedSupportedProtocol, descriptor
                .getSupportedProtocols());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        PDPDescriptor descriptor = (PDPDescriptor) unmarshallElement(singleElementOptionalAttributesFile);

        AssertJUnit.assertEquals("Cache duration was not expected value", expectedCacheDuration, descriptor.getCacheDuration()
                .longValue());
        AssertJUnit.assertEquals("ValidUntil was not expected value", expectedValidUntil, descriptor.getValidUntil());
        AssertJUnit.assertEquals("ErrorURL was not expected value", expectedErrorURL, descriptor.getErrorURL());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        PDPDescriptor descriptor = (PDPDescriptor) unmarshallElement(childElementsFile);

        AssertJUnit.assertNotNull("<Extensions>", descriptor.getExtensions());
        AssertJUnit.assertEquals("KeyDescriptor", 0, descriptor.getKeyDescriptors().size());

        AssertJUnit.assertEquals("AuthzService count", 3, descriptor.getAuthzServices().size());
        AssertJUnit.assertEquals("AssertionIDRequestService count", 2, descriptor.getAssertionIDRequestServices().size());
        AssertJUnit.assertEquals("NameIDFormat count", 1, descriptor.getNameIDFormats().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, PDPDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        PDPDescriptor descriptor = (PDPDescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        assertXMLEquals(expectedDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, PDPDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        PDPDescriptor descriptor = (PDPDescriptor) buildXMLObject(qname);

        for (String protocol : expectedSupportedProtocol) {
            descriptor.addSupportedProtocol(protocol);
        }

        descriptor.setCacheDuration(expectedCacheDuration);
        descriptor.setValidUntil(expectedValidUntil);
        descriptor.setErrorURL(expectedErrorURL);

        assertXMLEquals(expectedOptionalAttributesDOM, descriptor);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, PDPDescriptor.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        PDPDescriptor descriptor = (PDPDescriptor) buildXMLObject(qname);

        QName extensionsQName = new QName(SAMLConstants.SAML20MD_NS, Extensions.LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        descriptor.setExtensions((Extensions) buildXMLObject(extensionsQName));

        QName authzQName = new QName(SAMLConstants.SAML20MD_NS, AuthzService.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 3; i++) {
            descriptor.getAuthzServices().add((AuthzService) buildXMLObject(authzQName));
        }

        QName assertIDReqQName = new QName(SAMLConstants.SAML20MD_NS, AssertionIDRequestService.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        for (int i = 0; i < 2; i++) {
            descriptor.getAssertionIDRequestServices()
                    .add((AssertionIDRequestService) buildXMLObject(assertIDReqQName));
        }

        QName nameIDFormatQName = new QName(SAMLConstants.SAML20MD_NS, NameIDFormat.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20MD_PREFIX);
        descriptor.getNameIDFormats().add((NameIDFormat) buildXMLObject(nameIDFormatQName));

        assertXMLEquals(expectedChildElementsDOM, descriptor);
    }
}