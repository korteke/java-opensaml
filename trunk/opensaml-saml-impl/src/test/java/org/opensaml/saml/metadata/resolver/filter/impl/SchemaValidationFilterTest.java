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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.metadata.resolver.filter.impl.SchemaValidationFilter;
import org.opensaml.saml.metadata.resolver.impl.HTTPMetadataResolver;

/**
 * Unit tests for {@link SchemaValidationFilter}.
 */
public class SchemaValidationFilterTest extends XMLObjectBaseTestCase {
    
    private DefaultHttpClient httpClient;

    /** URL to InCommon metadata. */
    private String inCommonMDURL;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 1000 * 5);
        
        inCommonMDURL = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/data/org/opensaml/saml/saml2/metadata/InCommon-metadata.xml?content-type=text%2Fplain&view=co";
    }

    @Test
    public void test() throws Exception {
        HTTPMetadataResolver metadataProvider = new HTTPMetadataResolver(httpClient, inCommonMDURL);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setMetadataFilter(new SchemaValidationFilter(null));
        metadataProvider.initialize();
    }
}