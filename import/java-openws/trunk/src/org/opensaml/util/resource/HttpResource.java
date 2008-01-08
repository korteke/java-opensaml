/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.joda.time.DateTime;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * A resource representing a file retrieved from a URL using Apache Commons' HTTPClient.
 */
public class HttpResource implements Resource {

    /** HTTP URL of the resource. */
    private String resourceUrl;

    /** HTTP client. */
    private HttpClient httpClient;

    /**
     * Constructor.
     * 
     * @param resource HTTP(S) URL of the resource
     */
    public HttpResource(String resource) {
        resourceUrl = DatatypeHelper.safeTrimOrNullString(resource);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Resource URL may not be null or empty");
        }

        httpClient = new HttpClient();
    }

    /** {@inheritDoc} */
    public boolean exists() throws ResourceException {
        HeadMethod headMethod = new HeadMethod(resourceUrl);

        try {
            httpClient.executeMethod(headMethod);
            if (headMethod.getStatusCode() != HttpStatus.SC_OK) {
                return false;
            }

            return true;
        } catch (IOException e) {
            throw new ResourceException("Unable to contact resource URL: " + resourceUrl, e);
        }
    }

    /** {@inheritDoc} */
    public InputStream getInputStream() throws ResourceException {
        GetMethod getMethod = getResource();
        try{
            return getMethod.getResponseBodyAsStream();
        }catch(IOException e){
            throw new ResourceException("Unable to read response", e);
        }
    }

    /** {@inheritDoc} */
    public DateTime getLastModifiedTime() throws ResourceException {
        HeadMethod headMethod = new HeadMethod(resourceUrl);

        try {
            httpClient.executeMethod(headMethod);
            if (headMethod.getStatusCode() != HttpStatus.SC_OK) {
                throw new ResourceException("Unable to retrieve resource URL " + resourceUrl
                        + ", received HTTP status code " + headMethod.getStatusCode());
            }
            Header lastModifiedHeader = headMethod.getResponseHeader("Last-Modified");
            if (lastModifiedHeader != null) {
                HeaderElement[] elements = lastModifiedHeader.getElements();
                if (elements.length > 0) {
                    long lastModifiedTime = DateUtil.parseDate(elements[0].getValue()).getTime();
                    return new DateTime(lastModifiedTime);
                }
            }

            return new DateTime();
        } catch (IOException e) {
            throw new ResourceException("Unable to contact resource URL: " + resourceUrl, e);
        } catch (DateParseException e) {
            throw new ResourceException("Unable to parse last modified date for resource:" + resourceUrl, e);
        }
    }

    /** {@inheritDoc} */
    public String getLocation() {
        return resourceUrl;
    }

    /** {@inheritDoc} */
    public String toString() {
        return getLocation();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return getLocation().hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(Object o) {
        if(o == this){
            return true;
        }
        
        if (o instanceof HttpResource) {
            return getLocation().equals(((ClasspathResource) o).getLocation());
        }

        return false;
    }
    
    /**
     * Gets remote resource.
     * 
     * @return the remove resource
     * 
     * @throws ResourceException thrown if the resource could not be fetched
     */
    protected GetMethod getResource() throws ResourceException{
        GetMethod getMethod = new GetMethod(resourceUrl);

        try {
            httpClient.executeMethod(getMethod);
            if (getMethod.getStatusCode() != HttpStatus.SC_OK) {
                throw new ResourceException("Unable to retrieve resource URL " + resourceUrl
                        + ", received HTTP status code " + getMethod.getStatusCode());
            }
            return getMethod;
        } catch (IOException e) {
            throw new ResourceException("Unable to contact resource URL: " + resourceUrl, e);
        }
    }
}