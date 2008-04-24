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

/** Unit test for {@link HttpResource}. */
public class HttpResourceTest extends TestCase {

    /** URL to a resource that exists. */
    private String realResrc = "http://www.google.com";

    /** Tests the {@link HttpResource#exists()} method. */
    public void testExists() throws Exception {
        HttpResource resource = new HttpResource(realResrc);
        assertTrue(resource.exists());

        resource = new HttpResource("http://www.google.com/doesNotExist");
        assertFalse(resource.exists());
    }

    /** Tests the {@link HttpResource#getInputStream()} method. */
    public void testGetInputStream() throws Exception {
        HttpResource resource = new HttpResource(realResrc);

        InputStream ins = resource.getInputStream();
        assertNotNull(ins);
        assertTrue(ins.available() >= 0);

        resource = new HttpResource("http://www.google.com/doesNotExist");
        try {
            ins = resource.getInputStream();
            fail("Inputstream provided for non-existant resource");
        } catch (ResourceException e) {
            // we expect this
        }
    }

    /** Tests the {@link HttpResource#getLastModifiedTime()} method. */
    public void testGetLastModifiedTime() throws Exception {
        HttpResource resource = new HttpResource(realResrc);
        assertNotNull(resource.getLastModifiedTime());

        resource = new HttpResource("http://www.google.com/doesNotExist");
        try {
            resource.getLastModifiedTime();
            fail("Last modified time provided for non-existant resource");
        } catch (ResourceException e) {
            // we expect this
        }
    }

    /** Tests the {@link HttpResource#getLocation()} method. */
    public void testGetLocation() throws Exception {
        HttpResource resource = new HttpResource(realResrc);
        assertEquals(realResrc, resource.getLocation());

        resource = new HttpResource("http://www.google.com/doesNotExist");
        assertEquals("http://www.google.com/doesNotExist", resource.getLocation());
    }
}