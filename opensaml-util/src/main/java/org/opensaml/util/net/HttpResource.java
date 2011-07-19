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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import net.jcip.annotations.NotThreadSafe;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.util.EntityUtils;
import org.opensaml.util.Assert;
import org.opensaml.util.CloseableSupport;
import org.opensaml.util.Pair;
import org.opensaml.util.StringSupport;
import org.opensaml.util.resource.CachingResource;
import org.opensaml.util.resource.FilebackedRemoteResource;
import org.opensaml.util.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A resource that fetches data from a remote source via HTTP. A backup/cache of the data is maintained in a local file.
 * The backup file is used if a conditional get, based on the ETag and last modified time, indicates that the data has
 * not been modified or if there is a problem fetching the data from the remote source (e.g. if the remote server is
 * down).
 */
// TODO authentication
@NotThreadSafe
public class HttpResource implements CachingResource, FilebackedRemoteResource {

    /** Property name under which the ETag data is stored. */
    public static final String ETAG_PROP = "etag";

    /** Property name under which the Last-Modified data is stored. */
    public static final String LAST_MODIFIED_PROP = "modified";

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HttpResource.class);

    /** Client used to make HTTP requests. */
    private final HttpClient httpClient;

    /** URL of remote resource. */
    private final String resourceUrl;

    /** Backup and local cache of metadata. */
    private final File backupFile;

    /** File used to store ETag and Last-Modified properties associated with backup file. */
    private final File propFile;

    /**
     * Whether the data (ETag and/or Last-Modified) information will be saved in addition to the backup file and then
     * used to bootstrap this resource next time.
     */
    private final boolean savingConditionalGetData;

    /** Instant, in milliseconds since the epoch, when the backup file was created. */
    private long backupFileCreationInstant;

    /** ETag associated with cached metadata. */
    private String cachedResourceETag;

    /** Last modified time associated with cached metadata. */
    private String cachedResourceLastModified;

    /**
     * Constructor.
     * 
     * @param url URL of the remote resource data
     * @param client client used to fetch the remote resource data
     * @param backup file to which remote resource data is written as a backup/cache
     * @param saveConditionalGetData whether to save conditional GET data in addition to backup data
     */
    public HttpResource(final String url, final HttpClient client, final String backup, boolean saveConditionalGetData) {
        resourceUrl = StringSupport.trimOrNull(url);
        Assert.isNotNull(resourceUrl, "Resource URL may not be null or empty");

        Assert.isNotNull(client, "HTTP client may not be null");
        httpClient = client;

        final String trimmedBackup = StringSupport.trimOrNull(backup);
        Assert.isNotNull(trimmedBackup, "Backup file path can not be null");
        backupFile = new File(trimmedBackup);
        propFile = new File(backupFile.getAbsolutePath() + ".props");

        savingConditionalGetData = saveConditionalGetData;
        if (saveConditionalGetData) {
            readInSavedConditionalGetData();
        }
    }

    /** {@inheritDoc} */
    public String getLocation() {
        return resourceUrl;
    }

    /**
     * Gets whether the data (ETag and/or Last-Modified) information will be saved in addition to the backup file and
     * then used to bootstrap this resource next time.
     * 
     * @return whether conditional GET data is saved along with backup data file
     */
    public boolean isSavingConditionalGetData() {
        return savingConditionalGetData;
    }

    /** {@inheritDoc} */
    public long getLastModifiedTime() throws ResourceException {
        if (cachedResourceLastModified == null) {
            return -1;
        }

        try {
            final Date lastModDate = DateUtils.parseDate(cachedResourceLastModified);
            final GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(lastModDate);
            return cal.getTimeInMillis();
        } catch (DateParseException e) {
            throw new ResourceException("Unable to parse " + cachedResourceLastModified
                    + " from HTTP last modified header", e);
        }
    }

    /** {@inheritDoc} */
    public boolean exists() throws ResourceException {
        HttpUriRequest httpRequest = new HttpHead(resourceUrl);

        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            httpRequest.abort();

            if (statusCode == HttpStatus.SC_METHOD_NOT_ALLOWED) {
                log.debug(resourceUrl + " does not support HEAD requests, falling back to GET request");
                httpRequest = buildGetRequest();
                httpResponse = httpClient.execute(httpRequest);
                statusCode = httpResponse.getStatusLine().getStatusCode();
                httpRequest.abort();
            }

            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NOT_MODIFIED) {
                return true;
            }

            return false;
        } catch (IOException e) {
            throw new ResourceException("Error contacting resource " + resourceUrl, e);
        } finally {
            httpRequest.abort();
        }
    }

    /** {@inheritDoc} */
    public InputStream getInputStream() throws ResourceException {
        final HttpGet httpRequest = buildGetRequest();

        try {
            log.debug("Attempting to fetch data from '{}'", resourceUrl);
            final HttpResponse response = httpClient.execute(httpRequest);
            final int httpStatus = response.getStatusLine().getStatusCode();

            switch (httpStatus) {
                case HttpStatus.SC_OK:
                    log.debug("New data available from '{}', processing it", resourceUrl);
                    return getInputStreamFromResponse(response);
                case HttpStatus.SC_NOT_MODIFIED:
                    log.debug("Metadata unchanged since last request");
                    return null;
                default:
                    log.debug(
                            "Unacceptable status code, {}, returned when fetching metadata from '{}', using cached data if available",
                            httpStatus, resourceUrl);
                    InputStream ins = getInputStreamFromBackupFile();
                    if (ins == null) {
                        throw new ResourceException("Unable to read resource from " + resourceUrl + " or backup file "
                                + backupFile.getAbsolutePath());
                    }
                    return ins;
            }
        } catch (IOException e) {
            throw new ResourceException("Error retrieving metadata from " + resourceUrl, e);
        } finally {
            httpRequest.abort();
        }
    }

    /** {@inheritDoc} */
    public long getCacheInstant() {
        return backupFileCreationInstant;
    }

    /** {@inheritDoc} */
    public void expireCache() {
        cachedResourceETag = null;
        cachedResourceLastModified = null;

        if (backupFile.exists()) {
            backupFile.delete();
            backupFileCreationInstant = 0;
        }

        if (propFile.exists()) {
            propFile.delete();
        }
    }

    /** {@inheritDoc} */
    public boolean backupFileExists() {
        return backupFile.exists();
    }

    /** {@inheritDoc} */
    public long getBackupFileCreationInstant() {
        return backupFileCreationInstant;
    }

    /** {@inheritDoc} */
    public String getBackupFilePath() {
        return backupFile.getAbsolutePath();
    }

    /** {@inheritDoc} */
    public InputStream getInputStreamFromBackupFile() throws ResourceException {
        log.debug("Reading HTTP resource from backup file {}", backupFile.getAbsolutePath());
        
        if (!backupFile.exists()) {
            log.debug("Backup file {} does not exist, unable to read data from it", backupFile.getAbsolutePath());
            return null;
        }

        try {
            return new FileInputStream(backupFile);
        } catch (IOException e) {
            throw new ResourceException("Unable to read backup file " + getBackupFilePath(), e);
        }
    }

    /**
     * Gets the ETag from a given HTTP response.
     * 
     * @param response the HTTP response
     * 
     * @return the header value or null if the response is null or no header was present in the response
     */
    private String getETag(final HttpResponse response) {
        if (response == null) {
            return null;
        }

        final Header httpHeader = response.getFirstHeader(HttpHeaders.ETAG);
        if (httpHeader != null) {
            return httpHeader.getValue();
        }

        return null;
    }

    /**
     * Gets the {@value HttpClientSupport#HEADER_LAST_MODIFIED} from a given HTTP response.
     * 
     * @param response the HTTP response
     * 
     * @return the header value or null if the response is null or no header was present in the response
     */
    private String getLastModified(final HttpResponse response) {
        if (response == null) {
            return null;
        }

        final Header httpHeader = response.getFirstHeader(HttpHeaders.LAST_MODIFIED);
        if (httpHeader != null) {
            return httpHeader.getValue();
        }

        return null;
    }

    /** Reads in any previously saved conditional GET data. */
    private void readInSavedConditionalGetData() {
        if (!propFile.exists()) {
            return;
        }

        Properties props = new Properties();
        try {
            props.load(new FileReader(propFile));
            cachedResourceETag = StringSupport.trimOrNull(props.getProperty(ETAG_PROP));
            cachedResourceLastModified = StringSupport.trimOrNull(props.getProperty(LAST_MODIFIED_PROP));
        } catch (IOException e) {
            log.debug("Unable to read in property file {}", propFile.getAbsolutePath());
        }
    }

    /**
     * Builds the HTTP GET method used to fetch the metadata. The returned method advertises support for GZIP and
     * deflate compression, enables conditional GETs if the cached metadata came with either an ETag or Last-Modified
     * information, and sets up basic authentication if such is configured.
     * 
     * @return the constructed GET method
     */
    private HttpGet buildGetRequest() {
        final HttpGet getMethod = new HttpGet(resourceUrl);

        if (cachedResourceETag != null) {
            getMethod.setHeader(HttpHeaders.IF_NONE_MATCH, cachedResourceETag);
        }

        if (cachedResourceLastModified != null) {
            getMethod.setHeader(HttpHeaders.IF_MODIFIED_SINCE, cachedResourceLastModified);
        }

        return getMethod;
    }

    /**
     * Saves the remote data to the backup file and returns an {@link InputStream} to the given data. Also updates the
     * cached resource ETag and last modified time as well as the charset used for the data.
     * 
     * @param response response from the HTTP request
     * 
     * @return the resource data
     * 
     * @throws ResourceException thrown if there is a problem saving the resource data to the backup file
     */
    private InputStream getInputStreamFromResponse(final HttpResponse response) throws ResourceException {
        try {
            final byte[] responseEntity = EntityUtils.toByteArray(response.getEntity());
            log.debug("Recived response entity of {} bytes", responseEntity.length);

            cachedResourceETag = getETag(response);
            cachedResourceLastModified = getLastModified(response);
            log.debug("HTTP response included an ETag of {} and a Last-Modified of {}", cachedResourceETag,
                    cachedResourceLastModified);

            saveToBackupFile(responseEntity, cachedResourceETag, cachedResourceLastModified);

            return new ByteArrayInputStream(responseEntity);
        } catch (Exception e) {
            log.debug(
                    "Error retrieving metadata from '{}' and saving backup copy to '{}'.  Reading data from cached copy if available",
                    resourceUrl, getBackupFilePath());
            return getInputStreamFromBackupFile();
        }
    }

    /**
     * Saves the resource data to the backup file and optionally, based on {@link #savingConditionalGetData}, the
     * associated ETag and/or Last-Modified.
     * 
     * When this method is invoked a temp file is created, the data written to it, and then the existing backup file is
     * deleted and the temp file renamed to the backup file.
     * 
     * @param data resource data to be written to the backup file, never null
     * @param etag ETag data associated with the current data, may be null
     * @param lastModified Last-Modified data associated with the current data, may be null
     * 
     * @throws IOException thrown if there is a problem writing the backup file (e.g. if the process does not have write
     *             permission to the backup file)
     */
    private void saveToBackupFile(final byte[] data, String etag, String lastModified) throws IOException {
        log.debug("Saving backup of response to {}", backupFile.getAbsolutePath());

        final Pair<File, File> tmpFiles = saveToTempFiles(data, etag, lastModified);
        final File tmpDataFile = tmpFiles.getFirst();
        final File tmpPropFile = tmpFiles.getSecond();

        if (backupFile.exists()) {
            backupFile.delete();
        }
        if (!tmpDataFile.renameTo(backupFile)) {
            tmpDataFile.delete();
            if(tmpPropFile.exists()){
                tmpPropFile.delete();
            }
            log.debug("Unable to copy temporary data file to {}", backupFile.getAbsolutePath());
            return;
        }
        backupFileCreationInstant = backupFile.lastModified();

        if (propFile.exists()) {
            propFile.delete();
        }
        if (tmpPropFile != null) {
            if (!tmpPropFile.renameTo(propFile)) {
                tmpPropFile.delete();
                log.debug("Unable to copy temporary property file to {}.props", backupFile.getAbsolutePath());
            }
        }

        log.debug("Wrote {} bytes to backup file {}", backupFile.length(), backupFile.getAbsolutePath());
    }

    /**
     * Writes the data to temporary files.
     * 
     * @param data data to be saved
     * @param etag ETag data associated with the current data, may be null
     * @param lastModified Last-Modified data associated with the current data, may be null
     * 
     * @return the temp files; the first file is the data file and the second file is the property file and may be null
     *         if no data was written
     * 
     * @throws IOException thrown if there is a problem writing the temporary data to disk
     */
    private Pair<File, File> saveToTempFiles(final byte[] data, String etag, String lastModified) throws IOException {
        final String tmpFileName = Integer.toString(resourceUrl.hashCode());

        final File tmpPropFile;
        if (savingConditionalGetData && (etag != null || lastModified != null)) {
            tmpPropFile = File.createTempFile(tmpFileName, "props");
            Properties props = new Properties();
            if (etag != null) {
                props.put("etag", etag);
            }
            if (lastModified != null) {
                props.put("modified", lastModified);
            }

            props.store(new FileWriter(tmpPropFile), "Properties for URL " + resourceUrl);
        } else {
            tmpPropFile = null;
        }

        final File tmpDataFile = File.createTempFile(Integer.toString(resourceUrl.hashCode()), null);
        final FileOutputStream out = new FileOutputStream(tmpDataFile);
        out.write(data);
        out.flush();
        CloseableSupport.closeQuietly(out);

        return new Pair<File, File>(tmpDataFile, tmpPropFile);
    }
}