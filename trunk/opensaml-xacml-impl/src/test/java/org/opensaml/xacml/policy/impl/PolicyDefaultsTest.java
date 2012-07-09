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

package org.opensaml.xacml.policy.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.xacml.policy.DefaultsType;
import org.opensaml.xacml.policy.XPathVersion;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.DefaultsType} PolicyDefaults case.
 */
public class PolicyDefaultsTest extends XMLObjectProviderBaseTestCase {
    
    private final static String EXPECTED_XPATH_VERSION = "https://example.org/Policy/Defaults/Xpath/Version";
    
    /**
     * Constructor
     */
    public PolicyDefaultsTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/PolicyDefaults.xml";
        childElementsFile = "/data/org/opensaml/xacml/policy/impl/PolicyDefaultsChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        DefaultsType policyDefaults = (DefaultsType) unmarshallElement(singleElementFile);

        Assert.assertNull(policyDefaults.getXPathVersion());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        DefaultsType policyDefaults = buildXMLObject(DefaultsType.POLICY_DEFAULTS_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, policyDefaults);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        DefaultsType policyDefaults = buildXMLObject(DefaultsType.POLICY_DEFAULTS_ELEMENT_NAME);
        
        XSString xpath = buildXMLObject(XPathVersion.DEFAULTS_ELEMENT_NAME);
        xpath.setValue(EXPECTED_XPATH_VERSION);
        policyDefaults.setXPathVersion(xpath);


        assertXMLEquals(expectedChildElementsDOM, policyDefaults);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        DefaultsType policyDefaults = (DefaultsType) unmarshallElement(childElementsFile);
        XSString xpath = policyDefaults.getXPathVersion();
        Assert.assertEquals(xpath.getValue(), EXPECTED_XPATH_VERSION);
        Assert.assertEquals(xpath.getElementQName(), XPathVersion.DEFAULTS_ELEMENT_NAME);
    }
}