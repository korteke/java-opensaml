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

import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

/** Unit test for {@link FilesystemResource}. */
public class FilesystemResourceTest extends TestCase {

    /** Path to a resource that exists. */
    private String realResrc;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();

        realResrc = new File(FilesystemResource.class.getResource(
                "/data/org/opensaml/util/resource/replacementFilterTest.txt").toURI()).getAbsolutePath();
    }

    /** Tests the {@link FilesystemResource#exists()} method. */
    public void testExists() throws Exception {
        FilesystemResource resource = new FilesystemResource(realResrc);
        assertTrue(resource.exists());

        resource = new FilesystemResource("/foo");
        assertFalse(resource.exists());
    }

    /** Tests the {@link FilesystemResource#getInputStream()} method. */
    public void testGetInputStream() throws Exception {
        FilesystemResource resource = new FilesystemResource(realResrc);

        InputStream ins = resource.getInputStream();
        assertNotNull(ins);
        assertTrue(ins.available() > 0);

        resource = new FilesystemResource("/foo");
        try {
            ins = resource.getInputStream();
            fail("Inputstream provided for non-existant resource");
        } catch (ResourceException e) {
            // we expect this
        }
    }

    /** Tests the {@link FilesystemResource#getLastModifiedTime()} method. */
    public void testGetLastModifiedTime() throws Exception {
        FilesystemResource resource = new FilesystemResource(realResrc);
        assertNotNull(resource.getLastModifiedTime());

        resource = new FilesystemResource("/foo");
        try {
            resource.getLastModifiedTime();
            fail("Last modified time provided for non-existant resource");
        } catch (ResourceException e) {
            // we expect this
        }
    }

    /** Tests the {@link FilesystemResource#getLocation()} method. */
    public void testGetLocation() throws Exception {
        FilesystemResource resource = new FilesystemResource(realResrc);
        assertEquals(realResrc, resource.getLocation());

        resource = new FilesystemResource("/foo");
        assertEquals("/foo", resource.getLocation());
    }
}