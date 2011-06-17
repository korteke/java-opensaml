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

package org.opensaml.saml2.metadata.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A metadata provider that pulls metadata from a file on the local filesystem.
 * 
 * This metadata provider periodically checks to see if the read metadata file has changed. The delay between each
 * refresh interval is calculated as follows. If no validUntil or cacheDuration is present then the
 * {@link #getMaxRefreshDelay()} value is used. Otherwise, the earliest refresh interval of the metadata file is checked
 * by looking for the earliest of all the validUntil attributes and cacheDuration attributes. If that refresh interval
 * is larger than the max refresh delay then {@link #getMaxRefreshDelay()} is used. If that number is smaller than the
 * min refresh delay then {@link #getMinRefreshDelay()} is used. Otherwise the calculated refresh delay multiplied by
 * {@link #getRefreshDelayFactor()} is used. By using this factor, the provider will attempt to be refresh before the
 * cache actually expires, allowing a some room for error and recovery. Assuming the factor is not exceedingly close to
 * 1.0 and a min refresh delay that is not overly large, this refresh will likely occur a few times before the cache
 * expires.
 * 
 */
public class FilesystemMetadataProvider extends AbstractReloadingMetadataProvider {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(FilesystemMetadataProvider.class);

    /** The metadata file. */
    private File metadataFile;

    /**
     * Constructor.
     * 
     * @param metadata the metadata file
     * 
     * @throws MetadataProviderException thrown if the given file path is null, does not exist, does not represent a
     *             file, or if the metadata can not be parsed
     */
    public FilesystemMetadataProvider(File metadata) throws MetadataProviderException {
        super();
        setMetadataFile(metadata);
    }

    /**
     * Constructor.
     * 
     * @param metadata the metadata file
     * @param backgroundTaskTimer timer used to refresh metadata in the background
     * 
     * @throws MetadataProviderException thrown if the given file path is null, does not exist, does not represent a
     *             file, or if the metadata can not be parsed
     */
    public FilesystemMetadataProvider(Timer backgroundTaskTimer, File metadata) throws MetadataProviderException {
        super(backgroundTaskTimer);
        setMetadataFile(metadata);
    }

    /**
     * Sets the file from which metadata is read. The given file path is checked to see if it exists, is a file, and is
     * readable.
     * 
     * @param file path to the metadata file
     * 
     * @throws MetadataProviderException thrown if the file does not exist or is not a readable file
     */
    protected void setMetadataFile(File file) throws MetadataProviderException {

        if (!file.exists()) {
            throw new MetadataProviderException("Give metadata file, " + file.getAbsolutePath() + " does not exist");
        }

        if (!file.isFile()) {
            throw new MetadataProviderException("Give metadata file, " + file.getAbsolutePath() + " is not a file");
        }

        if (!file.canRead()) {
            throw new MetadataProviderException("Give metadata file, " + file.getAbsolutePath() + " is not readable");
        }

        metadataFile = file;
    }

    /**
     * Gets whether cached metadata should be discarded if it expires and can not be refreshed.
     * 
     * @return whether cached metadata should be discarded if it expires and can not be refreshed.
     * 
     * @deprecated use {@link #requireValidMetadata()} instead
     */
    public boolean maintainExpiredMetadata() {
        return !requireValidMetadata();
    }

    /**
     * Sets whether cached metadata should be discarded if it expires and can not be refreshed.
     * 
     * @param maintain whether cached metadata should be discarded if it expires and can not be refreshed.
     * 
     * @deprecated use {@link #setRequireValidMetadata(boolean)} instead
     */
    public void setMaintainExpiredMetadata(boolean maintain) {
        setRequireValidMetadata(!maintain);
    }

    /** {@inheritDoc} */
    protected String getMetadataIdentifier() {
        return metadataFile.getAbsolutePath();
    }

    /** {@inheritDoc} */
    protected byte[] fetchMetadata() throws MetadataProviderException {
        try {
            DateTime metadataUpdateTime = new DateTime(metadataFile.lastModified(), ISOChronology.getInstanceUTC());
            if (getLastRefresh() == null || metadataUpdateTime.isAfter(getLastRefresh())) {
                return inputstreamToByteArray(new FileInputStream(metadataFile));
            }

            return null;
        } catch (IOException e) {
            String errMsg = "Unable to read metadata file " + metadataFile.getAbsolutePath();
            log.error(errMsg, e);
            throw new MetadataProviderException(errMsg, e);
        }
    }
}