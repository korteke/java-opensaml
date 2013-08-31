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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.criterion.EntityIdCriterion;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.provider.MetadataProviderException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for {@link FileBackedHTTPMetadataResolver}.
 */
public class FileBackedHTTPMetadataResolverTest extends XMLObjectBaseTestCase {
    
    private DefaultHttpClient httpClient;

    private String mdUrl;
    private String badMDURL;
    private String backupFilePath;
    private FileBackedHTTPMetadataResolver metadataProvider;
    private String entityID;
    private CriteriaSet criteriaSet;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 1000 * 5);
        
        mdUrl="http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/data/org/opensaml/saml/saml2/metadata/ukfederation-metadata.xml?content-type=text%2Fplain&view=co";
        entityID = "urn:mace:ac.uk:sdss.ac.uk:provider:service:target.iay.org.uk";
        badMDURL = "http://www.google.com/";
        backupFilePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") 
                + "filebacked-http-metadata.xml";
        
        criteriaSet = new CriteriaSet(new EntityIdCriterion(entityID));
    }

    /** {@inheritDoc} */
    @AfterMethod
    protected void tearDown() {
        File backupFile = new File(backupFilePath);
        backupFile.delete();
    }
    
    /**
     * Tests the basic success case.
     * @throws ComponentInitializationException 
     * @throws ResolverException 
     */
    @Test
    public void testGetEntityDescriptor() throws ComponentInitializationException, ResolverException {
        metadataProvider = new FileBackedHTTPMetadataResolver(httpClient, mdUrl, backupFilePath);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
        
        EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNotNull(descriptor, "Retrieved entity descriptor was null");
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
    /**
     * Test fail-fast = true with known bad metadata URL.
     */
    @Test
    public void testFailFastBadURL() throws ResolverException {
        metadataProvider = new FileBackedHTTPMetadataResolver(httpClient, badMDURL, backupFilePath);
        
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
        metadataProvider = new FileBackedHTTPMetadataResolver(httpClient, badMDURL, backupFilePath);
        
        metadataProvider.setFailFastInitialization(false);
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail("Provider failed init with fail-fast=false");
        }
    }
    
    /**
     *  Test fail-fast = true and bad backup file
     */
    @Test
    public void testFailFastBadBackupFile() {
        try {
            // Use a known existing directory as backup file path, which is an invalid argument.
            metadataProvider = new FileBackedHTTPMetadataResolver(httpClient, mdUrl, System.getProperty("java.io.tmpdir"));
        } catch (ResolverException e) {
            Assert.fail("Provider failed bad backup file in constructor");
            
        }
        metadataProvider.setFailFastInitialization(true);
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();
            Assert.fail("Provider passed init with bad backup file, fail-fast=true");
        } catch (ComponentInitializationException e) {
            // expected do nothing
        }
    }
    
    /**
     *  Test case of fail-fast = false and bad backup file
     * @throws MetadataProviderException 
     */
    @Test
    public void testNoFailFastBadBackupFile() throws ResolverException {
        try {
            // Use a known existing directory as backup file path, which is an invalid argument.
            metadataProvider = new FileBackedHTTPMetadataResolver(httpClient, mdUrl, System.getProperty("java.io.tmpdir"));
        } catch (ResolverException e) {
            Assert.fail("Provider failed bad backup file in constructor");
            
        }
        metadataProvider.setFailFastInitialization(false);
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail("Provider failed init with bad backup file, fail-fast=false");
        }
        
        Assert.assertNotNull(metadataProvider.resolveSingle(criteriaSet), "Metadata retrieved from backing file was null");
    }
    
    /**
     * Tests use of backup file on simulated restart.
     * @throws ComponentInitializationException 
     * 
     * @throws MetadataProviderException
     */
    @Test
    public void testBackupFileOnRestart() throws ResolverException, ComponentInitializationException {
        // Do a setup here to get a good backup file
        metadataProvider = new FileBackedHTTPMetadataResolver(httpClient, mdUrl, backupFilePath);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
        
        Assert.assertNotNull(metadataProvider.resolveSingle(criteriaSet), "Retrieved metadata was null");

        File backupFile = new File(backupFilePath);
        Assert.assertTrue(backupFile.exists(), "Backup file was not created");
        Assert.assertTrue(backupFile.length() > 0, "Backup file contains no data");
        
        // Now do a new provider to simulate a restart (have to set fail-fast=false).
        // Verify that can use the data from backing file.
        FileBackedHTTPMetadataResolver badProvider = new FileBackedHTTPMetadataResolver(httpClient, badMDURL, backupFilePath);
        badProvider.setParserPool(parserPool);
        badProvider.setFailFastInitialization(false);
        badProvider.initialize();
        
        Assert.assertNotNull(metadataProvider.resolveSingle(criteriaSet), "Metadata retrieved from backing file was null");
    }

}