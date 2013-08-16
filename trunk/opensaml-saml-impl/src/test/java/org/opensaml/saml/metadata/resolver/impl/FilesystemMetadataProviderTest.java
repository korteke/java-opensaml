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
import java.io.IOException;
import java.net.URL;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataProvider;
import org.opensaml.saml.metadata.resolver.impl.HTTPMetadataProvider;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.Files;

public class FilesystemMetadataProviderTest extends XMLObjectBaseTestCase {

    private FilesystemMetadataProvider metadataProvider;
    
    private File mdFile;

    private String entityID;

    private CriteriaSet criteriaSet;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";

        URL mdURL = FilesystemMetadataProviderTest.class
                .getResource("/data/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        mdFile = new File(mdURL.toURI());

        metadataProvider = new FilesystemMetadataProvider(mdFile);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
        
        criteriaSet = new CriteriaSet(new EntityIdCriterion(entityID));
    }

    /**
     * Tests the {@link HTTPMetadataProvider#getEntityDescriptor(String)} method.
     * @throws ResolverException 
     */
    @Test
    public void testGetEntityDescriptor() throws ResolverException {
        EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNotNull(descriptor, "Retrieved entity descriptor was null");
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
    /**
     * Tests failure mode of an invalid metadata file that does not exist.
     * 
     * @throws ResolverException
     */
    @Test(expectedExceptions = {ResolverException.class})
    public void testNonexistentMetadataFile() throws ResolverException {
        metadataProvider = new FilesystemMetadataProvider(new File("I-Dont-Exist.xml"));
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
    }
    
    /**
     * Tests failure mode of an invalid metadata file that is actually a directory.
     * 
     * @throws IOException 
     * @throws ResolverException
     */
    @Test(expectedExceptions = {ResolverException.class})
    public void testInvalidMetadataFile() throws IOException, ResolverException {
        File targetFile = new File(System.getProperty("java.io.tmpdir"), "filesystem-md-provider-test");
        if (targetFile.exists()) {
            Assert.assertTrue(targetFile.delete());
        }
        Assert.assertTrue(targetFile.mkdir());
        Assert.assertTrue(targetFile.exists());
        Assert.assertTrue(targetFile.isDirectory());
        
        try {
            metadataProvider = new FilesystemMetadataProvider(targetFile);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.initialize();
        } finally {
            targetFile.delete();
        }
    }
    
    /**
     * Tests failure mode of an invalid metadata file that is unreadable.
     * 
     * @throws IOException 
     * @throws ResolverException
     */
    @Test(expectedExceptions = {ResolverException.class})
    public void testUnreadableMetadataFile() throws IOException, ResolverException {
        File targetFile = File.createTempFile("filesystem-md-provider-test", "xml");
        Assert.assertTrue(targetFile.exists());
        Assert.assertTrue(targetFile.isFile());
        Assert.assertTrue(targetFile.canRead());
        
        targetFile.setReadable(false);
        Assert.assertFalse(targetFile.canRead());
        
        try {
            metadataProvider = new FilesystemMetadataProvider(targetFile);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.initialize();
        } finally {
            targetFile.delete();
        }
    }
    
    /**
     * Tests failure mode of a metadata file which disappears after initial creation of the provider.
     * 
     * @throws IOException 
     * @throws ResolverException
     */
    @Test(expectedExceptions = {ResolverException.class})
    public void testDisappearingMetadataFile() throws IOException, ResolverException {
        File targetFile = new File(System.getProperty("java.io.tmpdir"), "filesystem-md-provider-test.xml");
        if (targetFile.exists()) {
            Assert.assertTrue(targetFile.delete());
        }
        Files.copy(mdFile, targetFile);
        Assert.assertTrue(targetFile.exists());
        Assert.assertTrue(targetFile.canRead());
        
        try {
            metadataProvider = new FilesystemMetadataProvider(targetFile);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.initialize();
        } catch (ResolverException e) {
            Assert.fail("Filesystem metadata provider init failed with file: " + targetFile.getAbsolutePath());
        }
        
        Assert.assertTrue(targetFile.delete());
        
        metadataProvider.refresh();
    }
    
    /**
     * Tests failfast init of false, with graceful recovery when file later appears.
     * 
     * @throws IOException 
     * @throws InterruptedException 
     */
    public void testRecoveryFromNoFailFast() throws IOException, InterruptedException {
        File targetFile = new File(System.getProperty("java.io.tmpdir"), "filesystem-md-provider-test.xml");
        if (targetFile.exists()) {
            Assert.assertTrue(targetFile.delete());
        }
        
        try {
            metadataProvider = new FilesystemMetadataProvider(targetFile);
            metadataProvider.setFailFastInitialization(false);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.initialize();
        } catch (ResolverException e) {
            Assert.fail("Filesystem metadata provider init failed with non-existent file and fail fast = false");
        }
        
        // Test that things don't blow up when initialized, no fail fast, but have no data.
        try {
            EntityDescriptor entity = metadataProvider.resolveSingle(criteriaSet);
            Assert.assertNull(entity, "Retrieved entity descriptor was not null"); 
        } catch (ResolverException e) {
            Assert.fail("Metadata provider behaved non-gracefully when initialized with fail fast = false");
        }
        
        // Filesystem timestamp may only have 1-second precision, so need to sleep for a couple of seconds just 
        // to make sure that the new copied file's timestamp is later than the Jodatime lastRefresh time
        // in the metadata provider.
        //Thread.sleep(2000);
        
        Files.copy(mdFile, targetFile);
        Assert.assertTrue(targetFile.exists());
        Assert.assertTrue(targetFile.canRead());
        
        try {
            metadataProvider.refresh();
            Assert.assertNotNull(metadataProvider.getMetadata());
            EntityDescriptor descriptor = metadataProvider.getEntityDescriptor(entityID);
            Assert.assertNotNull(descriptor, "Retrieved entity descriptor was null");
        } catch (ResolverException e) {
            Assert.fail("Filesystem metadata provider refresh failed recovery from initial init failure");
        }
    }
}