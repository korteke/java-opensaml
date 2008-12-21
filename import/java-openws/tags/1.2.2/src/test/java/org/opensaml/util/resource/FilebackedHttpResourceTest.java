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

/** Unit test for {@link FileBackedHttpResource}. */
public class FilebackedHttpResourceTest extends TestCase {

    /** Path to a resource that exists. */
    private final String realResrc = "http://www.google.com";

    /** Temporary backup file. */
    private final String backupFile = "unitTest.tmp";

    /** {@inheritDoc} */
    protected void tearDown() throws Exception {
        super.tearDown();

        File file = new File(backupFile);
        if (file.exists()) {
            file.delete();
        }
    }

    /** Tests the {@link FileBackedHttpResource#exists()} method. */
    public void testExists() throws Exception {
        FileBackedHttpResource resource = new FileBackedHttpResource(realResrc, backupFile);
        assertTrue(resource.exists());

        // force resource to write out backup file
        resource.getInputStream();

        resource = new FileBackedHttpResource(realResrc + "/doesNotExist", backupFile);
        assertTrue(resource.exists());
    }

    /** Tests the {@link FileBackedHttpResource#getInputStream()} method. */
    public void testGetInputStream() throws Exception {
        FileBackedHttpResource resource = new FileBackedHttpResource(realResrc, backupFile);
        InputStream ins = resource.getInputStream();
        assertNotNull(ins);
        assertTrue(ins.available() > 0);

        resource = new FileBackedHttpResource(realResrc + "/doesNotExist", backupFile);
        ins = resource.getInputStream();
        assertNotNull(ins);
        assertTrue(ins.available() > 0);
    }

    /** Tests the {@link FileBackedHttpResource#getLastModifiedTime()} method. */
    public void testGetLastModifiedTime() throws Exception {
        FileBackedHttpResource resource = new FileBackedHttpResource(realResrc, backupFile);
        assertNotNull(resource.getLastModifiedTime());

        // force resource to write out backup file
        resource.getInputStream();

        resource = new FileBackedHttpResource(realResrc + "/doesNotExist", backupFile);
        assertNotNull(resource.getLastModifiedTime());
    }

    /** Tests the {@link FileBackedHttpResource#getLocation()} method. */
    public void testGetLocation() throws Exception {
        FileBackedHttpResource resource = new FileBackedHttpResource(realResrc, backupFile);
        assertEquals(realResrc, resource.getLocation());

        // force resource to write out backup file
        resource.getInputStream();

        resource = new FileBackedHttpResource(realResrc + "/doesNotExist", backupFile);
        assertEquals(realResrc + "/doesNotExist", resource.getLocation());
    }
}