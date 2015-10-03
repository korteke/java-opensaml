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

import java.io.File;
import java.net.URL;
import java.util.HashSet;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolverTest;
import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test metadata indexing implemented in {@link AbstractBatchMetadataResolver}, using
 * {@link FilesystemMetadataResolver}.
 */
public class MetadataIndexingTest extends XMLObjectBaseTestCase {
    
    private FilesystemMetadataResolver metadataProvider;
    
    private File mdFile;

    private String entityID;

    private CriteriaSet criteriaSet;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";
        
        HashSet<MetadataIndex> indexes = new HashSet<>();
        indexes.add(new FunctionDrivenMetadataIndex(new UppercaseEntityIdDescriptorFunction(), new SimpleStringCriteriaFunction()));
        indexes.add(new RoleMetadataIndex());

        URL mdURL = FilesystemMetadataResolverTest.class
                .getResource("/data/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        mdFile = new File(mdURL.toURI());

        metadataProvider = new FilesystemMetadataResolver(mdFile);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");
        metadataProvider.setIndexes(indexes);
        metadataProvider.initialize();
        
        criteriaSet = new CriteriaSet();
    }
    
    @Test
    public void testResolveSingleFromIndex() throws ResolverException {
        //Empty criteria set.  Not an error, just no result.
        criteriaSet.clear();
        EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNull(descriptor);
        
        //Criteria with non-matching criterion value. Not an error, just no result.
        criteriaSet.clear();
        criteriaSet.add(new SimpleStringCriterion("foobar"));
        descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNull(descriptor);
        
        //Criteria with matching criterion value. This should be resolved from the index.
        criteriaSet.clear();
        criteriaSet.add(new SimpleStringCriterion(entityID.toUpperCase()));
        descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNotNull(descriptor, "Retrieved entity descriptor was null");
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
    @Test
    public void testResolveRoles() throws ResolverException {
        HashSet<EntityDescriptor> descriptors = new HashSet<>();
        
        criteriaSet.clear();
        criteriaSet.add(new EntityRoleCriterion(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
        descriptors.clear();
        for (EntityDescriptor descriptor : metadataProvider.resolve(criteriaSet)) {
            descriptors.add(descriptor);
        }
        
        Assert.assertEquals(descriptors.size(), 15);
        for (EntityDescriptor descriptor : descriptors) {
            Assert.assertTrue(descriptor.getRoleDescriptors(IDPSSODescriptor.DEFAULT_ELEMENT_NAME).size() > 0);
        }
        
        criteriaSet.clear();
        criteriaSet.add(new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
        descriptors.clear();
        for (EntityDescriptor descriptor : metadataProvider.resolve(criteriaSet)) {
            descriptors.add(descriptor);
        }
        
        Assert.assertEquals(descriptors.size(), 16);
        for (EntityDescriptor descriptor : descriptors) {
            Assert.assertTrue(descriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).size() > 0);
        }
    }

}
