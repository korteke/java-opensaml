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

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.metadata.resolver.impl.HTTPMetadataResolver;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link EntityRoleFilter}.
 */
public class EntityRoleFilterTest extends XMLObjectBaseTestCase {
    
    private DefaultHttpClient httpClient;

    /** URL to InCommon metadata. */
    private String inCommonMDURL;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 1000 * 5);
        
        inCommonMDURL = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml?content-type=text%2Fplain&view=co";
    }

    @Test
    public void testWhiteListSPRole() throws Exception {
        ArrayList<QName> retainedRoles = new ArrayList<>();
        retainedRoles.add(SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        HTTPMetadataResolver metadataProvider = new HTTPMetadataResolver(httpClient, inCommonMDURL);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setMetadataFilter(new EntityRoleFilter(retainedRoles));
        metadataProvider.setId("test");
        metadataProvider.initialize();
    }
    
    @Test
    public void testWhiteListIdPRoles() throws Exception {
        ArrayList<QName> retainedRoles = new ArrayList<>();
        retainedRoles.add(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        retainedRoles.add(AttributeAuthorityDescriptor.DEFAULT_ELEMENT_NAME);

        HTTPMetadataResolver metadataProvider = new HTTPMetadataResolver(httpClient, inCommonMDURL);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setMetadataFilter(new EntityRoleFilter(retainedRoles));
        metadataProvider.setId("test");
        metadataProvider.initialize();
    }
    
    @Test
    public void testWhiteListNoRole() throws Exception {
        ArrayList<QName> retainedRoles = new ArrayList<>();

        HTTPMetadataResolver metadataProvider = new HTTPMetadataResolver(httpClient, inCommonMDURL);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setMetadataFilter(new EntityRoleFilter(retainedRoles));
        metadataProvider.setId("test");
        metadataProvider.initialize();
    }
}