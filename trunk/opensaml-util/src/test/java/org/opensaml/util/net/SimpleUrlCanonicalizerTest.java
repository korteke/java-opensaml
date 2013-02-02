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

import org.testng.Assert;
import org.testng.annotations.Test;
import java.net.MalformedURLException;

/**
 * Test the simple URL canonicalizer.
 */
public class SimpleUrlCanonicalizerTest {
    
    @Test
    public void testScheme() throws MalformedURLException {
        Assert.assertEquals(SimpleUrlCanonicalizer.canonicalize("HttPS://www.example.org/Foo/Bar/baz"), "https://www.example.org/Foo/Bar/baz");
    }
    
    @Test
    public void testHostname() throws MalformedURLException {
        Assert.assertEquals(SimpleUrlCanonicalizer.canonicalize("https://WWW.eXample.orG/Foo/Bar/baz"), "https://www.example.org/Foo/Bar/baz");
    }

    @Test
    public void testPort() throws MalformedURLException {
        Assert.assertEquals(SimpleUrlCanonicalizer.canonicalize("https://www.example.org:443/Foo/Bar/baz"), "https://www.example.org/Foo/Bar/baz");
        Assert.assertEquals(SimpleUrlCanonicalizer.canonicalize("https://www.example.org:8443/Foo/Bar/baz"), "https://www.example.org:8443/Foo/Bar/baz");
        
        Assert.assertEquals(SimpleUrlCanonicalizer.canonicalize("http://www.example.org:80/Foo/Bar/baz"), "http://www.example.org/Foo/Bar/baz");
        Assert.assertEquals(SimpleUrlCanonicalizer.canonicalize("http://www.example.org:8080/Foo/Bar/baz"), "http://www.example.org:8080/Foo/Bar/baz");
        
        SimpleUrlCanonicalizer.registerSchemePortMapping("myscheme", 1967);
        Assert.assertEquals(SimpleUrlCanonicalizer.getRegisteredPort("MyScheme"), new Integer(1967));
    }

}
