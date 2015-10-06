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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.util.Collections;
import java.util.Set;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class MetadataIndexManagerTest extends XMLObjectBaseTestCase {
    
    private EntityDescriptor a, b, c;
    private SimpleStringCriterion critA, critB, critC;
    
    private CriteriaSet criteriaSet;
    
    private Set<EntityDescriptor> result;
    
    @BeforeMethod
    public void setUp() {
        a = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        a.setEntityID("urn:test:a");
        critA = new SimpleStringCriterion(a.getEntityID().toUpperCase());
        
        b = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        b.setEntityID("urn:test:b");
        critB = new SimpleStringCriterion(b.getEntityID().toUpperCase());
        
        c = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        c.setEntityID("urn:test:c");
        critC = new SimpleStringCriterion(c.getEntityID().toUpperCase());
        
        criteriaSet = new CriteriaSet();
    }
    
    @Test
    public void testBasic() {
        FunctionDrivenMetadataIndex functionIndex = 
                new FunctionDrivenMetadataIndex(new UppercaseEntityIdDescriptorFunction(), 
                        new SimpleStringCriteriaFunction());
        
        MetadataIndexManager manager = new MetadataIndexManager(Collections.<MetadataIndex>singleton(functionIndex));
        
        criteriaSet.clear();
        criteriaSet.add(critA);
        result = manager.lookupEntityDescriptors(criteriaSet);
        Assert.assertTrue(result.isEmpty());
        
        criteriaSet.clear();
        criteriaSet.add(critB);
        result = manager.lookupEntityDescriptors(criteriaSet);
        Assert.assertTrue(result.isEmpty());
        
        criteriaSet.clear();
        criteriaSet.add(critC);
        result = manager.lookupEntityDescriptors(criteriaSet);
        Assert.assertTrue(result.isEmpty());
        
        manager.indexEntityDescriptor(a);
        manager.indexEntityDescriptor(b);
        manager.indexEntityDescriptor(c);
        
        criteriaSet.clear();
        criteriaSet.add(critA);
        result = manager.lookupEntityDescriptors(criteriaSet);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(a));
        
        criteriaSet.clear();
        criteriaSet.add(critB);
        result = manager.lookupEntityDescriptors(criteriaSet);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(b));
        
        criteriaSet.clear();
        criteriaSet.add(critC);
        result = manager.lookupEntityDescriptors(criteriaSet);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(c));
    }

}
