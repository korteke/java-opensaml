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

import java.io.InputStream;
import java.nio.charset.Charset;

/** An interface representing an data resource. */
public interface Resource {

    /**
     * Gets resource location information.  Examples might be filesystem path, URL, etc.
     * 
     * @return resource location information
     */
    public String getLocation();
    
    /**
     * Gets the character encoding scheme used by the resource.  This is only used if the represented resource 
     * is textual.
     * 
     * @return character encoding scheme used by the resource
     */
    public Charset getCharacterSet();
    
    /**
     * Checks whether the resource exists.
     * 
     * @return true if the resource exists, false if not
     * 
     * @throws ResourceException thrown if there is a problem determining if the resource exists
     */
    public boolean exists() throws ResourceException;
    
    /**
     * Gets the input stream to the resource's data.
     * 
     * @return input stream to the resource's data
     * 
     * @throws ResourceException thrown if an input stream can not be created for the resource
     */
    public InputStream getInputStream() throws ResourceException;
    
    /**
     * Gets the time, in milliseconds since the epoch, when the resource was last modified.
     * 
     * @return time, in milliseconds since the epoch, when the resource was last modified
     * 
     * @throws ResourceException thrown if the last modified time can not be determined
     */
    public long getLastModifiedTime() throws ResourceException;

}