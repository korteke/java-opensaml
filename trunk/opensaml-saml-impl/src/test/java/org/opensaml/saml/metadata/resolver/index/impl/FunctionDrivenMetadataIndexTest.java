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

import java.util.Set;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.metadata.resolver.index.SimpleStringMetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 *
 */
public class FunctionDrivenMetadataIndexTest extends OpenSAMLInitBaseTestCase {
    
    private FunctionDrivenMetadataIndex metadataIndex;
    
    private EntityDescriptor a, b, c;
    private MetadataIndexKey keyA, keyB, keyC;
    
    @BeforeClass
    protected void setUp() {
        metadataIndex = new FunctionDrivenMetadataIndex(new UppercaseEntityIdDescriptorFunction(), new SimpleStringCriteriaFunction());
        
        a = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        a.setEntityID("urn:test:a");
        keyA = new SimpleStringMetadataIndexKey(a.getEntityID().toUpperCase());
        
        b = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        b.setEntityID("urn:test:b");
        keyB = new SimpleStringMetadataIndexKey(b.getEntityID().toUpperCase());
        
        c = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        c.setEntityID("urn:test:c");
        keyC = new SimpleStringMetadataIndexKey(c.getEntityID().toUpperCase());
    }
    
    @Test
    public void testGenerateKeysFromDescriptor() {
        Set<MetadataIndexKey> keys = metadataIndex.generateKeys(a);
        
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keyA));
    }
    
    @Test
    public void testGenerateKeysFromCriteria() {
        CriteriaSet criteriaSet = new CriteriaSet();
        
        criteriaSet.add(new SimpleStringCriterion("URN:TEST:A"));
        
        Set<MetadataIndexKey> keys = metadataIndex.generateKeys(criteriaSet);
        
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keyA));
    }

}
