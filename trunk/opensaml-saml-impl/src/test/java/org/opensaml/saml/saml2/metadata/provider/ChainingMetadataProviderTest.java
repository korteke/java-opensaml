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

package org.opensaml.saml.saml2.metadata.provider;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.Assert;
import java.io.File;
import java.net.URL;
import java.util.List;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.provider.ChainingMetadataProvider;
import org.opensaml.saml.saml2.metadata.provider.FilesystemMetadataProvider;
import org.opensaml.saml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml.saml2.metadata.provider.SchemaValidationFilter;

public class ChainingMetadataProviderTest extends XMLObjectBaseTestCase {

    private ChainingMetadataProvider metadataProvider;

    private String entityID;

    private String entityID2;

    private String supportedProtocol;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";
        entityID2 = "urn:mace:switch.ch:SWITCHaai:ethz.ch";
        supportedProtocol = "urn:oasis:names:tc:SAML:1.1:protocol";

        metadataProvider = new ChainingMetadataProvider();

        URL mdURL = FilesystemMetadataProviderTest.class
                .getResource("/data/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        File mdFile = new File(mdURL.toURI());
        FilesystemMetadataProvider fileProvider = new FilesystemMetadataProvider(mdFile);
        fileProvider.setParserPool(parserPool);
        fileProvider.initialize();
        metadataProvider.addMetadataProvider(fileProvider);

        URL mdURL2 = FilesystemMetadataProviderTest.class
                .getResource("/data/org/opensaml/saml/saml2/metadata/metadata.switchaai_signed.xml");
        File mdFile2 = new File(mdURL2.toURI());
        FilesystemMetadataProvider fileProvider2 = new FilesystemMetadataProvider(mdFile2);
        fileProvider2.setParserPool(parserPool);
        fileProvider2.initialize();
        metadataProvider.addMetadataProvider(fileProvider2);
    }

    /** Test the {@link ChainingMetadataProvider#getMetadata()} method. */
    @Test
    public void testGetMetadata() throws MetadataProviderException {
        EntitiesDescriptor descriptor1 = (EntitiesDescriptor) metadataProvider.getMetadata();
        Assert.assertEquals(descriptor1.getEntitiesDescriptors().size(), 2);
        Assert.assertEquals(descriptor1.getEntityDescriptors().size(), 0);

        EntitiesDescriptor descriptor2 = (EntitiesDescriptor) metadataProvider.getMetadata();
        Assert.assertEquals(descriptor2.getEntitiesDescriptors().size(), 2);
        Assert.assertEquals(descriptor2.getEntityDescriptors().size(), 0);
    }

    /** Tests the {@link ChainingMetadataProvider#getEntityDescriptor(String)} method. */
    @Test
    public void testGetEntityDescriptor() throws MetadataProviderException {
        EntityDescriptor descriptor = metadataProvider.getEntityDescriptor(entityID);
        Assert.assertNotNull(descriptor, "Retrieved entity descriptor was null");
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");

        EntityDescriptor descriptor2 = metadataProvider.getEntityDescriptor(entityID2);
        Assert.assertNotNull(descriptor2, "Retrieved entity descriptor was null");
        Assert.assertEquals(descriptor2.getEntityID(), entityID2, "Entity's ID does not match requested ID");
    }

    /** Tests the {@link ChainingMetadataProvider#getRole(String, javax.xml.namespace.QName) method.  */
    @Test
    public void testGetRole() throws MetadataProviderException {
        List<RoleDescriptor> roles = metadataProvider.getRole(entityID, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        Assert.assertNotNull(roles, "Roles for entity descriptor was null");
        Assert.assertEquals(roles.size(), 1, "Unexpected number of roles");

        List<RoleDescriptor> roles2 = metadataProvider.getRole(entityID2, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        Assert.assertNotNull(roles2, "Roles for entity descriptor was null");
        Assert.assertEquals(roles2.size(), 1, "Unexpected number of roles");
    }

    /** Test the {@link ChainingMetadataProvider#getRole(String, javax.xml.namespace.QName, String) method.  */
    @Test
    public void testGetRoleWithSupportedProtocol() throws MetadataProviderException {
        RoleDescriptor role = metadataProvider.getRole(entityID, IDPSSODescriptor.DEFAULT_ELEMENT_NAME,
                supportedProtocol);
        Assert.assertNotNull(role, "Roles for entity descriptor was null");

        RoleDescriptor role2 = metadataProvider.getRole(entityID2, IDPSSODescriptor.DEFAULT_ELEMENT_NAME,
                supportedProtocol);
        Assert.assertNotNull(role2, "Roles for entity descriptor was null");
    }

    /** Tests that metadata filters are disallowed on the chaining provider. */
    @Test
    public void testFilterDisallowed() {
        try {
            metadataProvider.setMetadataFilter(new SchemaValidationFilter(new String[] {}));
            Assert.fail("Should fail with an UnsupportedOperationException");
        } catch (MetadataProviderException e) {
            Assert.fail("Should fail with an UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected, do nothing
        }
    }

}