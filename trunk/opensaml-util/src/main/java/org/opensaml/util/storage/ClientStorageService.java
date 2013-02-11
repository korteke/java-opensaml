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

package org.opensaml.util.storage;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Extension of {@link StorageService} that maintains its data with client-side storage.
 */
@ThreadSafe
public interface ClientStorageService extends StorageService {

    /**
     * Reconstitute stored data from client request.
     * 
     * @param request   client request
     * @throws IOException  if an error occurs reconstituing the data
     */
    public void load(@Nonnull final ServletRequest request) throws IOException;
    
    /**
     * Preserve stored data in client.
     * 
     * @param response  response to client
     * @throws IOException  if an error occurs preserving the data
     */
    public void save(@Nonnull final ServletResponse response) throws IOException;
    
}