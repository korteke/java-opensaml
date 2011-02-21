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

package org.opensaml.util.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;

/** An {@link Resource} that reads data from a fileystem file. */
public class FilesystemResource implements Resource {

    /** Filesytem file read by this resource. */
    private final File resourceFile;

    /**
     * Constructor. The file character set is set to the system default character set.
     * 
     * @param resourcePath file read by this resource, never null or empty
     */
    public FilesystemResource(final String resourcePath) {
        final String trimmedPath = StringSupport.trimOrNull(resourcePath);
        Assert.isNotNull(trimmedPath, "Resource file path may not be null or empty");
        resourceFile = new File(trimmedPath);
    }

    /** {@inheritDoc} */
    public boolean exists() throws ResourceException {
        return resourceFile.exists();
    }

    /** {@inheritDoc} */
    public InputStream getInputStream() throws ResourceException {
        try {
            return new FileInputStream(resourceFile);
        } catch (FileNotFoundException e) {
            throw new ResourceException("Resource file " + resourceFile.getPath() + " can not be read", e);
        }
    }

    /** {@inheritDoc} */
    public long getLastModifiedTime() throws ResourceException {
        return resourceFile.lastModified();
    }

    /** {@inheritDoc} */
    public String getLocation() {
        return resourceFile.getAbsolutePath();
    }
}