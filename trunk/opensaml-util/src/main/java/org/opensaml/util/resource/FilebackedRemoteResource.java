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

/**
 * A resource that fetches data from a remote source but keeps a backup copy on the filesystem in case the remote file
 * can not be reached.
 * 
 * This resource will read data from the remote resource per usual but if there is an error it will retrieve the data
 * from the backup file. Upon any successful read of the remote resource the backup file will be recreated with the
 * latest data.
 */
public interface FilebackedRemoteResource extends Resource {

    /**
     * Gets the filesystem path to the backup file, whether the file currently exists or not.
     * 
     * @return filesystem path to the backup file
     */
    public String getBackupFilePath();

    /**
     * Gets whether the backup file exists.
     * 
     * @return true if the backup file exists, false otherwise
     */
    public boolean backupFileExists();

    /**
     * Gets the creation time, in millisconds since the epoch, of the backup file.
     * 
     * @return creation time of the backup file or 0 if the file does not yet exist
     */
    public long getBackupFileCerationInstant();

    /**
     * Gets an InputStream of the backup file, if it exists. Implementations of this method must not perform any caching
     * but instead returns a new inputstream, on every call, if the backup file exits.
     * 
     * @return the InputStream for the the backup file if it exists, otherwise null
     * 
     * @throws ResourceException thrown if there is a problem reading the backup file
     */
    public InputStream getInputStreamFromBackupFile() throws ResourceException;
}