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
package org.opensaml.util.http;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.opensaml.util.resource.CachingResource;
import org.opensaml.util.resource.FilebackedRemoteResource;
import org.opensaml.util.resource.ResourceException;

/**
 *
 */
public class HttpResource implements CachingResource, FilebackedRemoteResource {

    /** {@inheritDoc} */
    public void expireCache() {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    public long getCacheInstant() {
        // TODO Auto-generated method stub
        return 0;
    }

    /** {@inheritDoc} */
    public boolean exists() throws ResourceException {
        // TODO Auto-generated method stub
        return false;
    }

    /** {@inheritDoc} */
    public Charset getCharacterSet() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public InputStream getInputStream() throws ResourceException {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public long getLastModifiedTime() throws ResourceException {
        // TODO Auto-generated method stub
        return 0;
    }

    /** {@inheritDoc} */
    public String getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public boolean backupFileExists() {
        // TODO Auto-generated method stub
        return false;
    }

    /** {@inheritDoc} */
    public long getBackupFileCerationInstant() {
        // TODO Auto-generated method stub
        return 0;
    }

    /** {@inheritDoc} */
    public String getBackupFilePath() {
        // TODO Auto-generated method stub
        return null;
    }

}
