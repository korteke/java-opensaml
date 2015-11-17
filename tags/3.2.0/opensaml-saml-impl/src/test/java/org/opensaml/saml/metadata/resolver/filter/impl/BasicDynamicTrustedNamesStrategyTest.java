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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.util.Set;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BasicDynamicTrustedNamesStrategyTest extends XMLObjectBaseTestCase {
    
    private BasicDynamicTrustedNamesStrategy strategy = new BasicDynamicTrustedNamesStrategy();
    
    @Test
    public void testEntityDescriptor() {
        EntityDescriptor target = buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        target.setEntityID("foo");
        Set<String> result = strategy.apply(target);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains("foo"));
    }

    @Test
    public void testEntitiesDescriptor() {
        EntitiesDescriptor target = buildXMLObject(EntitiesDescriptor.DEFAULT_ELEMENT_NAME);
        target.setName("foo");
        Set<String> result = strategy.apply(target);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains("foo"));
    }
    
    @Test
    public void testRoleDescriptor() {
        EntityDescriptor parent = buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        parent.setEntityID("foo");
        SPSSODescriptor target = buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        parent.getRoleDescriptors().add(target);
        Set<String> result = strategy.apply(target);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains("foo"));
    }
    
    @Test
    public void testAffiliationDescriptor() {
        EntityDescriptor parent = buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        parent.setEntityID("foo");
        AffiliationDescriptor target = buildXMLObject(AffiliationDescriptor.DEFAULT_ELEMENT_NAME);
        target.setOwnerID("bar");
        parent.setAffiliationDescriptor(target);
        Set<String> result = strategy.apply(target);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);
        Assert.assertTrue(result.contains("foo"));
        Assert.assertTrue(result.contains("bar"));
    }
    
    @Test
    public void testUnknown() {
        XMLObject target = buildXMLObject(simpleXMLObjectQName);
        Set<String> result = strategy.apply(target);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }
    
    @Test
    public void testNull() {
        XMLObject target = null;
        Set<String> result = strategy.apply(target);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }
}
