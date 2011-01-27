/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.util;

import junit.framework.TestCase;

/**
 * Test the simple URL canonicalizer.
 */
public class SimpleURLCanonicalizerTest extends TestCase {
    
    public void testScheme() {
        assertEquals("https://www.example.org/Foo/Bar/baz", SimpleURLCanonicalizer.canonicalize("HttPS://www.example.org/Foo/Bar/baz"));
    }
    
    public void testHostname() {
        assertEquals("https://www.example.org/Foo/Bar/baz", SimpleURLCanonicalizer.canonicalize("https://WWW.eXample.orG/Foo/Bar/baz"));
    }

    public void testPort() {
        assertEquals("https://www.example.org/Foo/Bar/baz", SimpleURLCanonicalizer.canonicalize("https://www.example.org:443/Foo/Bar/baz"));
        assertEquals("https://www.example.org:8443/Foo/Bar/baz", SimpleURLCanonicalizer.canonicalize("https://www.example.org:8443/Foo/Bar/baz"));
        
        assertEquals("http://www.example.org/Foo/Bar/baz", SimpleURLCanonicalizer.canonicalize("http://www.example.org:80/Foo/Bar/baz"));
        assertEquals("http://www.example.org:8080/Foo/Bar/baz", SimpleURLCanonicalizer.canonicalize("http://www.example.org:8080/Foo/Bar/baz"));
        
        SimpleURLCanonicalizer.registerSchemePortMapping("myscheme", 1967);
        assertEquals(new Integer(1967), SimpleURLCanonicalizer.getRegisteredPort("MyScheme"));
        
        assertEquals("gopher://www.example.org:70/Foo/Bar/baz", SimpleURLCanonicalizer.canonicalize("gopher://www.example.org:70/Foo/Bar/baz"));
        SimpleURLCanonicalizer.registerSchemePortMapping("gopher", 70);
        assertEquals("gopher://www.example.org/Foo/Bar/baz", SimpleURLCanonicalizer.canonicalize("gopher://www.example.org:70/Foo/Bar/baz"));
    }

}
