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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;

/** A resource which reads data from the classpath. */
public class ClasspathResource implements Resource {

    /** URL to the classpath resoruce. */
    private final URL classpathResource;

    /**
     * Constructor. ClassLoader used to locate the resource is the loader used to load this class. Default system
     * character set is used as the resource character set.
     * 
     * @param resourcePath classpath path to the resource
     */
    public ClasspathResource(final String resourcePath) {
        this(resourcePath, ClasspathResource.class.getClassLoader());
    }

    /**
     * Constructor.
     * 
     * @param resourcePath classpath path to the resource
     * @param classLoader class loader used to locate the resource
     */
    public ClasspathResource(final String resourcePath, final ClassLoader classLoader) {
        final String trimmedPath = StringSupport.trimOrNull(resourcePath);
        Assert.isNotNull(trimmedPath, "Resource path may not be null or empty");

        Assert.isNotNull(classLoader, "Resource class loader may not be null");

        classpathResource = classLoader.getResource(trimmedPath);
        Assert.isNotNull(classpathResource, "Resource " + resourcePath + " does not exist on the classpath");
    }

    /** {@inheritDoc} */
    public boolean exists() throws ResourceException {
        return true;
    }

    /** {@inheritDoc} */
    public InputStream getInputStream() throws ResourceException {
        try {
            return classpathResource.openStream();
        } catch (IOException e) {
            throw new ResourceException("Resource " + getLocation() + " can not be read", e);
        }
    }

    /** {@inheritDoc} */
    public long getLastModifiedTime() throws ResourceException {
        return 0;
    }

    /** {@inheritDoc} */
    public String getLocation() {
        return classpathResource.toString();
    }
}