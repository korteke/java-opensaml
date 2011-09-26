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

package org.opensaml.util.net;

import java.io.File;
import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.opensaml.util.resource.ResourceException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/** Unit test for {@link HttpResource}. */
public class HttpResourceTest {

    private HttpClient httpClient;

    private File backupFile;

    private File propFile;

    @BeforeTest
    public void testSetUp() {
        HttpClientBuilder clientBuilder = new HttpClientBuilder();
        clientBuilder.setConnectionPooling(false);
        httpClient = clientBuilder.buildClient();

        backupFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "index");
        if (backupFile.exists()) {
            backupFile.delete();
        }

        propFile = new File(backupFile.getAbsolutePath() + ".props");
        if (propFile.exists()) {
            propFile.delete();
        }
    }

    @AfterMethod
    public void postMethod() {
        if (backupFile.exists()) {
            backupFile.delete();
        }

        if (propFile.exists()) {
            propFile.delete();
        }
    }

    @Test
    public void testInvalidInstantiation() {
        try {
            new HttpResource(null, "http://example.org");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expected this
        }

        try {
            new HttpResource(httpClient, null);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expected this
        }

    }

    @Test
    public void testValidUrl() throws Exception {
        String url = "http://shibboleth.net";

        HttpResource resource = new HttpResource(httpClient, url);
        Assert.assertEquals(resource.getLocation(), url);

        Assert.assertTrue(resource.exists());

        InputStream ins = resource.getInputStream();
        Assert.assertNotNull(ins);
        Assert.assertTrue(ins.available() > 0);
    }

    @Test
    public void testInvalidUrl() throws Exception {
        String url = "http://shibboleth.net/lkjeiocjkljn";

        HttpResource resource = new HttpResource(httpClient, url);
        Assert.assertEquals(resource.getLocation(), url);

        Assert.assertFalse(resource.exists());

        try {
            resource.getInputStream();
            Assert.fail();
        } catch (ResourceException e) {
            // expected this
        }
    }
}