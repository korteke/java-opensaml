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
import java.io.File;
import java.net.URL;
import java.util.List;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.provider.FilesystemMetadataProvider;
import org.opensaml.saml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml.saml2.metadata.provider.MetadataProviderException;

public class FilesystemMetadataProviderTest extends XMLObjectBaseTestCase {

    private FilesystemMetadataProvider metadataProvider;

    private String entityID;

    private String supportedProtocol;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        entityID = "urn:mace:incommon:washington.edu";
        supportedProtocol = "urn:oasis:names:tc:SAML:1.1:protocol";

        URL mdURL = FilesystemMetadataProviderTest.class
                .getResource("/data/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml");
        File mdFile = new File(mdURL.toURI());

        metadataProvider = new FilesystemMetadataProvider(mdFile);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.initialize();
    }

    /**
     * Tests the {@link HTTPMetadataProvider#getEntityDescriptor(String)} method.
     */
    @Test
    public void testGetEntityDescriptor() throws MetadataProviderException {
        EntityDescriptor descriptor = metadataProvider.getEntityDescriptor(entityID);
        Assert.assertNotNull(descriptor, "Retrieved entity descriptor was null");
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }

    /**
     * Tests the {@link HTTPMetadataProvider#getRole(String, javax.xml.namespace.QName)} method.
     */
    @Test
    public void testGetRole() throws MetadataProviderException {
        List<RoleDescriptor> roles = metadataProvider.getRole(entityID, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        Assert.assertNotNull(roles, "Roles for entity descriptor was null");
        Assert.assertEquals(roles.size(), 1, "Unexpected number of roles");
    }

    /**
     * Test the {@link HTTPMetadataProvider#getRole(String, javax.xml.namespace.QName, String)} method.
     */
    @Test
    public void testGetRoleWithSupportedProtocol() throws MetadataProviderException {
        RoleDescriptor role = metadataProvider.getRole(entityID, IDPSSODescriptor.DEFAULT_ELEMENT_NAME,
                supportedProtocol);
        Assert.assertNotNull(role, "Roles for entity descriptor was null");
    }
}