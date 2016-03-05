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

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.OneTimeUse;
import org.opensaml.saml.saml2.core.ProxyRestriction;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.ConditionsImpl}.
 */
public class ConditionsTest extends XMLObjectProviderBaseTestCase {

    /** Expected NotBefore value */
    private DateTime expectedNotBefore;

    /** Expected NotOnOrAfter value */
    private DateTime expectedNotOnOrAfter;

    /** Count of Condition subelements */
    private int conditionCount = 6;

    /** Count of AudienceRestriction subelements */
    private int audienceRestrictionCount = 3;

    /** Constructor */
    public ConditionsTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/Conditions.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/ConditionsOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/ConditionsChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedNotBefore = new DateTime(1984, 8, 26, 10, 01, 30, 43, ISOChronology.getInstanceUTC());
        expectedNotOnOrAfter = new DateTime(1984, 8, 26, 10, 11, 30, 43, ISOChronology.getInstanceUTC());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Conditions conditions = (Conditions) unmarshallElement(singleElementFile);

        DateTime notBefore = conditions.getNotBefore();
        Assert.assertEquals(notBefore, expectedNotBefore, "NotBefore was " + notBefore + ", expected " + expectedNotBefore);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Conditions conditions = (Conditions) unmarshallElement(singleElementOptionalAttributesFile);

        DateTime notBefore = conditions.getNotBefore();
        Assert.assertEquals(notBefore, expectedNotBefore, "NotBefore was " + notBefore + ", expected " + expectedNotBefore);

        DateTime notOnOrAfter = conditions.getNotOnOrAfter();
        Assert.assertEquals(notOnOrAfter, expectedNotOnOrAfter,
                "NotOnOrAfter was " + notOnOrAfter + ", expected " + expectedNotOnOrAfter);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, Conditions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        Conditions conditions = (Conditions) buildXMLObject(qname);

        conditions.setNotBefore(expectedNotBefore);
        assertXMLEquals(expectedDOM, conditions);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, Conditions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        Conditions conditions = (Conditions) buildXMLObject(qname);

        conditions.setNotBefore(expectedNotBefore);
        conditions.setNotOnOrAfter(expectedNotOnOrAfter);

        assertXMLEquals(expectedOptionalAttributesDOM, conditions);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Conditions conditions = (Conditions) unmarshallElement(childElementsFile);
        Assert.assertEquals(conditions.getConditions().size(), conditionCount, "Condition count not as expected");
        Assert.assertNotNull(conditions.getOneTimeUse(), "OneTimeUse absent");
        Assert.assertNotNull(conditions.getProxyRestriction(), "ProxyRestriction absent");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, Conditions.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        Conditions conditions = (Conditions) buildXMLObject(qname);

        QName oneTimeUserQName = new QName(SAMLConstants.SAML20_NS, OneTimeUse.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        conditions.getConditions().add((Condition) buildXMLObject(oneTimeUserQName));
        
        QName audienceRestrictionQName = new QName(SAMLConstants.SAML20_NS, AudienceRestriction.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i < audienceRestrictionCount; i++) {
            conditions.getAudienceRestrictions().add((AudienceRestriction) buildXMLObject(audienceRestrictionQName));
        }
        
        conditions.getConditions().add((Condition) buildXMLObject(oneTimeUserQName));
        
        QName proxyRestrictionQName = new QName(SAMLConstants.SAML20_NS, ProxyRestriction.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        conditions.getConditions().add((Condition) buildXMLObject(proxyRestrictionQName));
        
        assertXMLEquals(expectedChildElementsDOM, conditions);
    }
}