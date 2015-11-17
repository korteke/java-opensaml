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

package org.opensaml.saml.common.profile.logic;

import java.util.Collections;
import java.util.regex.Pattern;

import net.shibboleth.ext.spring.resource.ResourceHelper;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.common.profile.logic.EntityAttributesPredicate.Candidate;
import org.opensaml.saml.metadata.resolver.impl.ResourceBackedMetadataResolver;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link EntityAttributesPredicate}.
 */
public class EntityAttributesPredicateTest extends XMLObjectBaseTestCase {

    private ResourceBackedMetadataResolver metadataProvider;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        
        final Resource resource =
                new ClassPathResource("data/org/opensaml/saml/metadata/resolver/filter/impl/EntitiesDescriptor-Name-metadata.xml");
        metadataProvider = new ResourceBackedMetadataResolver(null, ResourceHelper.of(resource));
        metadataProvider.setId("test");
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
    }

    @Test
    public void testWrongName() throws Exception {

        final Candidate candidate = new Candidate("urn:foo:bar", Attribute.URI_REFERENCE);
        candidate.setValues(Collections.singletonList("bar"));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(Collections.singletonList(candidate), false);
                
        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        Assert.assertNotNull(entity);
        
        Assert.assertFalse(condition.apply(entity));
    }

    @Test
    public void testWrongNameFormat() throws Exception {

        final Candidate candidate = new Candidate("urn:foo", Attribute.BASIC);
        candidate.setValues(Collections.singletonList("bar"));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(Collections.singletonList(candidate), false);
                
        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        Assert.assertNotNull(entity);
        
        Assert.assertFalse(condition.apply(entity));
    }
    
    @Test
    public void testGroupUnspecified() throws Exception {

        final Candidate candidate = new Candidate("urn:foo", null);
        candidate.setValues(Collections.singletonList("bar"));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(Collections.singletonList(candidate), false);
                
        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        Assert.assertNotNull(entity);
        
        Assert.assertTrue(condition.apply(entity));
    }
    
    @Test
    public void testGroupExact() throws Exception {

        final Candidate candidate = new Candidate("urn:foo", Attribute.URI_REFERENCE);
        candidate.setValues(Collections.singletonList("bar"));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(Collections.singletonList(candidate), false);
                
        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        Assert.assertNotNull(entity);
        
        Assert.assertTrue(condition.apply(entity));
    }
    
    @Test
    public void testGroupAdditional() throws Exception {

        final Candidate candidate = new Candidate("urn:foo", Attribute.URI_REFERENCE);
        candidate.setValues(Collections.singletonList("bar"));
        candidate.setRegexps(Collections.singletonList(Pattern.compile("baz")));
        final EntityAttributesPredicate condition =
                new EntityAttributesPredicate(Collections.singletonList(candidate), false);
                
        final EntityDescriptor entity =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-top.example.org")));
        Assert.assertNotNull(entity);
        Assert.assertFalse(condition.apply(entity));

        final EntityDescriptor entity2 =
                metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion("https://idp-sub1.example.org")));
        Assert.assertNotNull(entity2);
        Assert.assertTrue(condition.apply(entity2));
    }
}