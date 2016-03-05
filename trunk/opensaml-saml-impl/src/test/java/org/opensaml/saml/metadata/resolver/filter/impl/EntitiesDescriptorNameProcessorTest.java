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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.metadata.EntityGroupName;
import org.opensaml.saml.metadata.resolver.filter.MetadataNodeProcessor;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolverTest;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EntitiesDescriptorNameProcessorTest extends XMLObjectBaseTestCase {
    
    private FilesystemMetadataResolver metadataProvider;
    
    private File mdFile;
    
    private NodeProcessingMetadataFilter metadataFilter;
    
    private ArrayList<MetadataNodeProcessor> processors;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        URL mdURL = FilesystemMetadataResolverTest.class
                .getResource("/org/opensaml/saml/metadata/resolver/filter/impl/EntitiesDescriptor-Name-metadata.xml");
        mdFile = new File(mdURL.toURI());

        processors = new ArrayList<>();
        processors.add(new EntitiesDescriptorNameProcessor());
        
        metadataFilter = new NodeProcessingMetadataFilter();
        metadataFilter.setNodeProcessors(processors);
        metadataFilter.initialize();
        
        metadataProvider = new FilesystemMetadataResolver(mdFile);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setMetadataFilter(metadataFilter);
        metadataProvider.setId("Test");
        metadataProvider.initialize();
    }
    
    @Test
    public void testGroupHierarchy() throws ResolverException {
        EntityGroupName groupTop = new EntityGroupName("GroupTop");
        EntityGroupName groupSub1 = new EntityGroupName("GroupSub1");
        EntityGroupName groupSub2 = new EntityGroupName("GroupSub2");
        EntityGroupName groupSub2A = new EntityGroupName("GroupSub2A");
        
        EntityDescriptor entityDescriptor = null;
        List<EntityGroupName> groups = null;
        
        entityDescriptor = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        groups = entityDescriptor.getObjectMetadata().get(EntityGroupName.class);
        Assert.assertEquals(groups.size(), 1);
        Assert.assertTrue(groups.contains(groupTop));
        
        entityDescriptor = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-sub1.example.org")));
        groups = entityDescriptor.getObjectMetadata().get(EntityGroupName.class);
        Assert.assertEquals(groups.size(), 2);
        Assert.assertTrue(groups.contains(groupTop));
        Assert.assertTrue(groups.contains(groupSub1));
        
        entityDescriptor = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-sub2.example.org")));
        groups = entityDescriptor.getObjectMetadata().get(EntityGroupName.class);
        Assert.assertEquals(groups.size(), 2);
        Assert.assertTrue(groups.contains(groupTop));
        Assert.assertTrue(groups.contains(groupSub2));
        
        entityDescriptor = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-sub2a.example.org")));
        groups = entityDescriptor.getObjectMetadata().get(EntityGroupName.class);
        Assert.assertEquals(groups.size(), 3);
        Assert.assertTrue(groups.contains(groupTop));
        Assert.assertTrue(groups.contains(groupSub2));
        Assert.assertTrue(groups.contains(groupSub2A));
    }

}
