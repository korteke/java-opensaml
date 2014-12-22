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

import java.util.Collections;

import net.shibboleth.ext.spring.resource.ResourceHelper;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.common.profile.logic.EntityIdPredicate;
import org.opensaml.saml.metadata.resolver.filter.impl.PredicateFilter.Direction;
import org.opensaml.saml.metadata.resolver.impl.ResourceBackedMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link PredicateFilter}.
 */
public class PredicateFilterTest extends XMLObjectBaseTestCase {

    private ResourceBackedMetadataResolver metadataProvider;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        
        final Resource resource = new ClassPathResource("data/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        metadataProvider = new ResourceBackedMetadataResolver(null, ResourceHelper.of(resource));
        metadataProvider.setId("test");
        metadataProvider.setParserPool(parserPool);
    }

    @Test
    public void testBlacklist() throws Exception {
        
        final String whitelisted = "urn:mace:incommon:dartmouth.edu";
        final String blacklisted = "urn:mace:incommon:osu.edu";

        final EntityIdPredicate condition = new EntityIdPredicate(Collections.singletonList(blacklisted));
        
        metadataProvider.setMetadataFilter(new PredicateFilter(Direction.EXCLUDE, condition));
        metadataProvider.initialize();
        
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(blacklisted)));
        Assert.assertNull(entity);
        
        entity = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(whitelisted)));
        Assert.assertNotNull(entity);
    }
    
    @Test
    public void testWhitelist() throws Exception {
        
        final String whitelisted = "urn:mace:incommon:dartmouth.edu";
        final String blacklisted = "urn:mace:incommon:osu.edu";

        final EntityIdPredicate condition = new EntityIdPredicate(Collections.singletonList(whitelisted));
        
        metadataProvider.setMetadataFilter(new PredicateFilter(Direction.INCLUDE, condition));
        metadataProvider.initialize();
        
        EntityDescriptor entity = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(blacklisted)));
        Assert.assertNull(entity);
        
        entity = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(whitelisted)));
        Assert.assertNotNull(entity);
    }
}