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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.impl.NodeProcessingMetadataFilter.MetadataNodeProcessor;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolverTest;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NodeProcessingMetadataFilterTest extends XMLObjectBaseTestCase {
    
    private FilesystemMetadataResolver metadataProvider;
    
    private File mdFile;

    private String entityID;

    private CriteriaSet criteriaSet;
    
    private NodeProcessingMetadataFilter metadataFilter;
    
    private ArrayList<MetadataNodeProcessor> processors;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";

        URL mdURL = FilesystemMetadataResolverTest.class
                .getResource("/data/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        mdFile = new File(mdURL.toURI());

        metadataProvider = new FilesystemMetadataResolver(mdFile);
        metadataProvider.setParserPool(parserPool);
        
        metadataFilter = new NodeProcessingMetadataFilter();
        
        processors = new ArrayList<>();
        
        criteriaSet = new CriteriaSet(new EntityIdCriterion(entityID));
    }
    
    @Test
    public void testBasicVisit() throws ComponentInitializationException {
        MetadataNodeProcessor processor = new MetadataNodeProcessor() {
            public void process(XMLObject metadataNode) throws FilterException {
                if (metadataNode instanceof EntityDescriptor) {
                    metadataNode.getObjectMetadata().put(new TestData("EntityDescriptor"));
                } else if (metadataNode instanceof RoleDescriptor) {
                    metadataNode.getObjectMetadata().put(new TestData("RoleDescriptor"));
                } else if (metadataNode instanceof KeyDescriptor) {
                    metadataNode.getObjectMetadata().put(new TestData("KeyDescriptor"));
                }
            }
        };
        
        processors.add(processor);
        metadataFilter.setNodeProcessors(processors);
        metadataFilter.initialize();
        
        metadataProvider.setMetadataFilter(metadataFilter);
        metadataProvider.initialize();
        
        for (EntityDescriptor ed : metadataProvider) {
            checkNode(ed);
        }
        
    }
    
    /**
     * @param ed
     */
    private void checkNode(XMLObject node) {
        List<TestData> objectTestData = node.getObjectMetadata().get(TestData.class);
        if (node instanceof EntityDescriptor) {
            Assert.assertEquals(objectTestData.size(), 1);
            Assert.assertEquals(objectTestData.get(0).getData(), "EntityDescriptor");
        } else if (node instanceof RoleDescriptor) {
            Assert.assertEquals(objectTestData.size(), 1);
            Assert.assertEquals(objectTestData.get(0).getData(), "RoleDescriptor");
        } else if (node instanceof KeyDescriptor) {
            Assert.assertEquals(objectTestData.size(), 1);
            Assert.assertEquals(objectTestData.get(0).getData(), "KeyDescriptor");
        } else {
            Assert.assertEquals(0, objectTestData.size());
        }
        
        List<XMLObject> children = node.getOrderedChildren();
        if (children != null) {
            for (XMLObject child : children) {
                if (child != null) {
                    checkNode(child);
                }
            }
        }
    }

    public class TestData {
        private String data;
        
        public TestData(String newData) {
            data = newData;
        }
        
        public String getData() {
            return data;
        }
    }

}
