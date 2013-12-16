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

package org.opensaml.saml.metadata.resolver.impl;

import java.io.IOException;
import java.util.Timer;

import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.resource.ShibbolethResource;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A metadata provider that reads metadata from a {#link {@link ShibbolethResource}.
 * 
 * @since 2.2
 */
public class ResourceBackedMetadataResolver extends AbstractReloadingMetadataResolver {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ResourceBackedMetadataResolver.class);

    /** Resource from which metadata is read. */
    private ShibbolethResource metadataResource;


    /**
     * Constructor.
     * 
     * @param resource resource from which to read the metadata file.
     * @param timer task timer used to schedule metadata refresh tasks
     * @param maxMetadataCacheDuration maximum amount of time, in milliseconds, that metadata may be cached before being
     *            re-read
     * 
     * @throws ResolverException thrown if there is a problem retrieving information about the resource 
     * 
     * @deprecated
     */
    public ResourceBackedMetadataResolver(ShibbolethResource resource, Timer timer, long maxMetadataCacheDuration)
            throws ResolverException {
        super(timer);

        try {
            if (!resource.exists()) {
                throw new IOException("Resource " + resource.getDescription() + " does not exist.");
            }
            metadataResource = resource;
        } catch (IOException e) {
            throw new ResolverException("Unable to read resource", e);
        }
    }

    /**
     * Constructor.
     * 
     * @param resource resource from which to read the metadata file.
     * @param timer task timer used to schedule metadata refresh tasks
     * 
     * @throws IOException thrown if there is a problem retrieving information about the resource
     */
    public ResourceBackedMetadataResolver(Timer timer, ShibbolethResource resource) throws IOException {
        super(timer);

        if (!resource.exists()) {
            throw new IOException("Resource " + resource.getDescription() + " does not exist.");
        }
        metadataResource = resource;
}

    /** {@inheritDoc} */
    protected void doDestroy() {
        metadataResource = null;
        
        super.doDestroy();
    }
    
    /** {@inheritDoc} */
    protected String getMetadataIdentifier() {
        return metadataResource.getDescription();
    }

    /** {@inheritDoc} */
    protected byte[] fetchMetadata() throws ResolverException {
        try {
            DateTime metadataUpdateTime = new DateTime(metadataResource.lastModified());
            log.debug("resource {} was last modified {}", metadataResource.getDescription(), metadataUpdateTime);
            if (getLastRefresh() == null || metadataUpdateTime.isAfter(getLastRefresh())) {
                return inputstreamToByteArray(metadataResource.getInputStream());
            }

            return null;
        } catch (IOException e) {
            String errorMsg = "Unable to read metadata file";
            log.error(errorMsg, e);
            throw new ResolverException(errorMsg, e);
        }
    }
}