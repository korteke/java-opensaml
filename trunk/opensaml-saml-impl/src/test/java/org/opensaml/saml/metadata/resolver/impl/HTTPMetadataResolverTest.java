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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link HTTPMetadataResolver}.
 */
public class HTTPMetadataResolverTest extends XMLObjectBaseTestCase {
    
    private DefaultHttpClient httpClient;

    private String inCommonMDURL;
    private String badMDURL;
    private String entityID;
    private HTTPMetadataResolver metadataProvider;
    private CriteriaSet criteriaSet;
    
    /**{@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 1000 * 5);
        
        inCommonMDURL = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/data/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml?content-type=text%2Fplain&view=co";
        badMDURL = "http://www.google.com/";
        entityID = "urn:mace:incommon:washington.edu";
        
        metadataProvider = new HTTPMetadataResolver(httpClient, inCommonMDURL);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
        
        criteriaSet = new CriteriaSet(new EntityIdCriterion(entityID));
    }
    
    /**
     * Tests the {@link HTTPMetadataResolver#lookupEntityID(String)} method.
     */
    @Test
    public void testGetEntityDescriptor() throws ResolverException {
        EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNotNull(descriptor, "Retrieved entity descriptor was null");
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
    /**
     * Test fail-fast = true with known bad metadata URL.
     */
    @Test
    public void testFailFastBadURL() throws ResolverException {
        metadataProvider = new HTTPMetadataResolver(httpClient, badMDURL);
        
        metadataProvider.setFailFastInitialization(true);
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();
            Assert.fail("metadata provider claims to have parsed known invalid data");
        } catch (ComponentInitializationException e) {
            //expected, do nothing
        }
    }
    
    /**
     * Test fail-fast = false with known bad metadata URL.
     */
    @Test
    public void testNoFailFastBadURL() throws ResolverException {
        metadataProvider = new HTTPMetadataResolver(httpClient, badMDURL);
        
        metadataProvider.setFailFastInitialization(false);
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail("Provider failed init with fail-fast=false");
        }
        
        EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNull(descriptor);
    }
}