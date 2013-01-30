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

import java.net.MalformedURLException;

import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * Tests building and parsing URLs with the builder.
 */
public class UrlBuilderTest {
    
    /**
     * Test with scheme and host.
     * @throws MalformedURLException 
     */
    @Test
    public void testURLBuilder1() throws MalformedURLException{
        String url = "http://www.example.com";
        UrlBuilder builder1 = new UrlBuilder(url);
        Assert.assertEquals(builder1.getScheme(), "http");
        Assert.assertEquals(builder1.getUsername(), null);
        Assert.assertEquals(builder1.getPassword(), null);
        Assert.assertEquals(builder1.getHost(), "www.example.com");
        Assert.assertEquals(builder1.getPort(), -1);
        Assert.assertEquals(builder1.getPath(), null);
        Assert.assertEquals(builder1.getQueryParams().size(), 0);
        Assert.assertEquals(builder1.getFragment(), null);
        
        Assert.assertEquals(builder1.buildURL(), url);
    }
    
    /**
     * Test with scheme, host, and path.
     * @throws MalformedURLException 
     */
    @Test
    public void testURLBuilder2() throws MalformedURLException{
        String url = "https://www.example.com/foo/index.html";
        UrlBuilder builder1 = new UrlBuilder(url);
        Assert.assertEquals(builder1.getScheme(), "https");
        Assert.assertEquals(builder1.getUsername(), null);
        Assert.assertEquals(builder1.getPassword(), null);
        Assert.assertEquals(builder1.getHost(), "www.example.com");
        Assert.assertEquals(builder1.getPort(), -1);
        Assert.assertEquals(builder1.getPath(), "/foo/index.html");
        Assert.assertEquals(builder1.getQueryParams().size(), 0);
        Assert.assertEquals(builder1.getFragment(), null);
        
        Assert.assertEquals(builder1.buildURL(), url);
    }
    
    /**
     * Test with scheme, host, port, path, and query params.
     * @throws MalformedURLException 
     */
    @Test
    public void testURLBuilder3() throws MalformedURLException{
        String url = "http://www.example.com:8080/index.html?attrib1=value1&attrib2=value&attrib3";
        UrlBuilder builder1 = new UrlBuilder(url);
        Assert.assertEquals(builder1.getScheme(), "http");
        Assert.assertEquals(builder1.getUsername(), null);
        Assert.assertEquals(builder1.getPassword(), null);
        Assert.assertEquals(builder1.getHost(), "www.example.com");
        Assert.assertEquals(builder1.getPort(), 8080);
        Assert.assertEquals(builder1.getPath(), "/index.html");
        Assert.assertEquals(builder1.getQueryParams().size(), 3);
        Assert.assertEquals(builder1.getFragment(), null);
        
        Assert.assertEquals(builder1.buildURL(), url);
    }
    
    /**
     * Test with scheme, host, and fragment.
     * @throws MalformedURLException 
     */
    @Test
    public void testURLBuilder4() throws MalformedURLException{
        String url = "https://www.example.com#anchor";
        UrlBuilder builder1 = new UrlBuilder(url);
        Assert.assertEquals(builder1.getScheme(), "https");
        Assert.assertEquals(builder1.getUsername(), null);
        Assert.assertEquals(builder1.getPassword(), null);
        Assert.assertEquals(builder1.getHost(), "www.example.com");
        Assert.assertEquals(builder1.getPort(), -1);
        Assert.assertEquals(builder1.getPath(), null);
        Assert.assertEquals(builder1.getQueryParams().size(), 0);
        Assert.assertEquals(builder1.getFragment(), "anchor");
        
        Assert.assertEquals(builder1.buildURL(), url);
    }

    /**
     * Test with scheme, host, port, path, query params, and anchor.
     * @throws MalformedURLException 
     */
    @Test
    public void testURLBuilder5() throws MalformedURLException{
        String url = "http://www.example.com/index.html?attrib1=value1&attrib2=value&attrib3#anchor";
        UrlBuilder builder1 = new UrlBuilder(url);
        Assert.assertEquals(builder1.getScheme(), "http");
        Assert.assertEquals(builder1.getUsername(), null);
        Assert.assertEquals(builder1.getPassword(), null);
        Assert.assertEquals(builder1.getHost(), "www.example.com");
        Assert.assertEquals(builder1.getPort(), -1);
        Assert.assertEquals(builder1.getPath(), "/index.html");
        Assert.assertEquals(builder1.getQueryParams().size(), 3);
        Assert.assertEquals(builder1.getFragment(), "anchor");
        
        Assert.assertEquals(builder1.buildURL(), url);
    }
    
    /**
     * Test with scheme, username, password, and host.
     * @throws MalformedURLException 
     */
    @Test
    public void testURLBuilder6() throws MalformedURLException{
        String url = "http://user:pass@www.example.com";
        UrlBuilder builder1 = new UrlBuilder(url);
        Assert.assertEquals(builder1.getScheme(), "http");
        Assert.assertEquals(builder1.getUsername(), "user");
        Assert.assertEquals(builder1.getPassword(), "pass");
        Assert.assertEquals(builder1.getHost(), "www.example.com");
        Assert.assertEquals(builder1.getPort(), -1);
        Assert.assertEquals(builder1.getPath(), null);
        Assert.assertEquals(builder1.getQueryParams().size(), 0);
        Assert.assertEquals(builder1.getFragment(), null);
        
        Assert.assertEquals(builder1.buildURL(), url);
    }
    
    /**
     * Test with scheme, username, and host.
     * @throws MalformedURLException 
     */
    @Test
    public void testURLBuilder7() throws MalformedURLException{
        String url = "http://user@www.example.com";
        UrlBuilder builder1 = new UrlBuilder(url);
        Assert.assertEquals(builder1.getScheme(), "http");
        Assert.assertEquals(builder1.getUsername(), "user");
        Assert.assertEquals(builder1.getPassword(), null);
        Assert.assertEquals(builder1.getHost(), "www.example.com");
        Assert.assertEquals(builder1.getPort(), -1);
        Assert.assertEquals(builder1.getPath(), null);
        Assert.assertEquals(builder1.getQueryParams().size(), 0);
        Assert.assertEquals(builder1.getFragment(), null);
        
        Assert.assertEquals(builder1.buildURL(), url);
    }
}