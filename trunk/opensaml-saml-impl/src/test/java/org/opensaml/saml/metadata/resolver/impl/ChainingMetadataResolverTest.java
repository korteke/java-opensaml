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

package org.opensaml.saml.metadata.resolver.impl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder.SAML1Version;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.impl.SchemaValidationFilter;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ChainingMetadataResolverTest extends XMLObjectBaseTestCase {

    private ChainingMetadataResolver metadataProvider;

    private String entityID;

    private String entityID2;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";
        entityID2 = "urn:mace:switch.ch:SWITCHaai:ethz.ch";

        metadataProvider = new ChainingMetadataResolver();
        ArrayList<MetadataResolver> resolvers = new ArrayList<>();

        URL mdURL = ChainingMetadataResolverTest.class
                .getResource("/data/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        File mdFile = new File(mdURL.toURI());
        FilesystemMetadataResolver fileProvider = new FilesystemMetadataResolver(mdFile);
        fileProvider.setParserPool(parserPool);
        fileProvider.initialize();
        resolvers.add(fileProvider);

        URL mdURL2 = ChainingMetadataResolverTest.class
                .getResource("/data/org/opensaml/saml/saml2/metadata/metadata.switchaai_signed.xml");
        File mdFile2 = new File(mdURL2.toURI());
        FilesystemMetadataResolver fileProvider2 = new FilesystemMetadataResolver(mdFile2);
        fileProvider2.setParserPool(parserPool);
        // For this test, need to set this because metadata.switchaai_signed.xml has an expired validUntil
        fileProvider2.setRequireValidMetadata(false);
        fileProvider2.initialize();
        resolvers.add(fileProvider2);
        
        metadataProvider.setResolvers(resolvers);
        
        metadataProvider.initialize();
    }

    @Test()
    public void testGetEntityDescriptor() throws ResolverException {
        EntityDescriptor descriptor = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(entityID)));
        Assert.assertNotNull(descriptor, "Retrieved entity descriptor was null");
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");

        EntityDescriptor descriptor2 = metadataProvider.resolveSingle(new CriteriaSet(new EntityIdCriterion(entityID2)));
        Assert.assertNotNull(descriptor2, "Retrieved entity descriptor was null");
        Assert.assertEquals(descriptor2.getEntityID(), entityID2, "Entity's ID does not match requested ID");
    }

    @Test()
    public void testFilterDisallowed() {
        try {
            metadataProvider.setMetadataFilter(new SchemaValidationFilter(new SAMLSchemaBuilder(SAML1Version.SAML_11)));
            Assert.fail("Should fail with an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected, do nothing
        }
    }

}