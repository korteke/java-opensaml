/*
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.util.resource;

import java.io.InputStream;

import junit.framework.TestCase;

/** Unit test for {@link ClasspathResource}. */
public class ClasspathResourceTest extends TestCase {

    /** Path to a resource that exists. */
    private final String realResrc = "/data/org/opensaml/util/resource/replacementFilterTest.txt";

    /** Tests the {@link ClasspathResource#exists()} method. */
    public void testExists() throws Exception {
        ClasspathResource resource = new ClasspathResource(realResrc);
        assertTrue(resource.exists());
    }

    /** Tests the {@link ClasspathResource#getInputStream()} method. */
    public void testGetInputStream() throws Exception {
        ClasspathResource resource = new ClasspathResource(realResrc);
        
        InputStream ins = resource.getInputStream(); 
        assertNotNull(ins);
        assertTrue(ins.available() > 0);
    }

    /** Tests the {@link ClasspathResource#getLastModifiedTime()} method. */
    public void testGetLastModifiedTime() throws Exception {
        ClasspathResource resource = new ClasspathResource(realResrc);
        assertNotNull(resource.getLastModifiedTime());
    }

    /** Tests the {@link ClasspathResource#getLocation()} method. */
    public void testGetLocation() throws Exception {
        ClasspathResource resource = new ClasspathResource(realResrc);
        assertEquals(ClasspathResourceTest.class.getResource(realResrc).toString(), resource.getLocation());
    }

    /** Tests creating a classpath resource pointing to a resource that doesn't exist. */
    public void testNonexistantResource() {
        try {
            new ClasspathResource("/foo");
            fail("Resource allowed initialization with non-existant classpath resource");
        } catch (ResourceException e) {
            // nothing, we expect this
        }
    }
}