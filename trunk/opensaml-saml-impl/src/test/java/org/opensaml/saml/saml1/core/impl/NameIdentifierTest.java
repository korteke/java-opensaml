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
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.NameIdentifier;

/**
 * Test case for NameIdentifier
 */
public class NameIdentifierTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects */
    private final QName qname;

    private String expectedNameIdentifier;
    private String expectedFormat;
    private String expectedNameQualifier;
    
    /**
     * Constructor
     */
    public NameIdentifierTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml1/impl/singleNameIdentifier.xml";
        singleElementOptionalAttributesFile  = "/org/opensaml/saml/saml1/impl/singleNameIdentifierAttributes.xml";
        expectedFormat = "format";
        expectedNameIdentifier = "IdentifierText";
        expectedNameQualifier = "Qualifier";
        qname = new QName(SAMLConstants.SAML1_NS, NameIdentifier.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        NameIdentifier nameIdentifier = (NameIdentifier) unmarshallElement(singleElementFile);
        
        Assert.assertNull(nameIdentifier.getValue(), "Name Identifer contents present");
        Assert.assertNull(nameIdentifier.getNameQualifier(), "NameQualifier present");
        Assert.assertNull(nameIdentifier.getFormat(), "Format present");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        NameIdentifier nameIdentifier = (NameIdentifier) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertEquals(nameIdentifier.getValue(), expectedNameIdentifier, "Name Identifier contents");
        Assert.assertEquals(nameIdentifier.getNameQualifier(), expectedNameQualifier, "NameQualfier attribute");
        Assert.assertEquals(nameIdentifier.getFormat(), expectedFormat, "Format attribute");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        NameIdentifier nameIdentifier = (NameIdentifier) buildXMLObject(qname);
        
        nameIdentifier.setFormat(expectedFormat);
        nameIdentifier.setValue(expectedNameIdentifier);
        nameIdentifier.setNameQualifier(expectedNameQualifier);
        
        assertXMLEquals(expectedOptionalAttributesDOM, nameIdentifier);
    }
}
