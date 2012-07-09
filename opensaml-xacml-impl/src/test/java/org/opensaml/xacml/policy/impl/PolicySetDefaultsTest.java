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
 * {@link org.opensaml.xacml.policy.DefaultsType} PolicySetDefaults case.
 */
public class PolicySetDefaultsTest extends XMLObjectProviderBaseTestCase {
    
    private final static String EXPECTED_XPATH_VERSION = "https://example.org/Policy/Set/Defaults/Xpath/Version";
    
    /**
     * Constructor
     */
    public PolicySetDefaultsTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/PolicySetDefaults.xml";
        childElementsFile = "/data/org/opensaml/xacml/policy/impl/PolicySetDefaultsChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        DefaultsType policySetDefaults = (DefaultsType) unmarshallElement(singleElementFile);

        Assert.assertNull(policySetDefaults.getXPathVersion());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        DefaultsType policySetDefaults = buildXMLObject(DefaultsType.POLICY_SET_DEFAULTS_ELEMENT_NAME);

        assertXMLEquals(expectedDOM, policySetDefaults);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        DefaultsType policySetDefaults = buildXMLObject(DefaultsType.POLICY_SET_DEFAULTS_ELEMENT_NAME);
        
        XSString xpath = buildXMLObject(XPathVersion.DEFAULTS_ELEMENT_NAME);
        xpath.setValue(EXPECTED_XPATH_VERSION);
        policySetDefaults.setXPathVersion(xpath);


        assertXMLEquals(expectedChildElementsDOM, policySetDefaults);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        DefaultsType policySetDefaults = (DefaultsType) unmarshallElement(childElementsFile);
        XSString xpath = policySetDefaults.getXPathVersion();
        Assert.assertEquals(xpath.getValue(), EXPECTED_XPATH_VERSION);
        Assert.assertEquals(xpath.getElementQName(), XPathVersion.DEFAULTS_ELEMENT_NAME);
    }
}