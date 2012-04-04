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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameID;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.NameIDImpl}.
 */
public class NameIDTest extends XMLObjectProviderBaseTestCase {

    /** Expected Name value */
    private String expectedName;
    
    /** Expected NameQualifier value */
    private String expectedNameQualifier;

    /** Expected SPNameQualifier value */
    private String expectedSPNameQualifier;

    /** Expected Format value */
    private String expectedFormat;

    /** Expected SPProvidedID value */
    private String expectedSPID;

    /** Constructor */
    public NameIDTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/NameID.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/NameIDOptionalAttributes.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedName = "id name";
        expectedNameQualifier = "nq";
        expectedSPNameQualifier = "spnq";
        expectedFormat = "format style";
        expectedSPID = "spID";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        NameID nameID = (NameID) unmarshallElement(singleElementFile);

        String name = nameID.getValue();
        Assert.assertEquals(expectedName, name, "Name not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        NameID nameID = (NameID) unmarshallElement(singleElementOptionalAttributesFile);

        String name = nameID.getValue();
        Assert.assertEquals(expectedName, name, "Name not as expected");

        String nameQualifier = nameID.getNameQualifier();
        Assert.assertEquals(expectedNameQualifier, nameQualifier, "NameQualifier not as expected");

        String spNameQualifier = nameID.getSPNameQualifier();
        Assert.assertEquals(expectedSPNameQualifier, spNameQualifier, "SPNameQualifier not as expected");

        String format = nameID.getFormat();
        Assert.assertEquals(expectedFormat, format, "Format not as expected");

        String spProvidedID = nameID.getSPProvidedID();
        Assert.assertEquals(expectedSPID, spProvidedID, "SPProviderID not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, NameID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        NameID nameID = (NameID) buildXMLObject(qname);

        nameID.setValue(expectedName);
        assertXMLEquals(expectedDOM, nameID);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, NameID.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        NameID nameID = (NameID) buildXMLObject(qname);

        nameID.setValue(expectedName);
        nameID.setNameQualifier(expectedNameQualifier);
        nameID.setSPNameQualifier(expectedSPNameQualifier);
        nameID.setFormat(expectedFormat);
        nameID.setSPProvidedID(expectedSPID);
        assertXMLEquals(expectedOptionalAttributesDOM, nameID);
    }
}