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

package org.opensaml.storage;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Specialization of {@link StorageService} that maintains its data on a per-request basis.
 * 
 * <p>Implementations of this interface will typically be injected with objects or proxies
 * that supply access to the underlying request/response environment, such as servlets.</p>
 * 
 * <p>Implementations of this interface are not required to guarantee coherency across requests
 * operating on the same initial data but must guarantee updates leave data in a consistent
 * state.</p> 
 */
@ThreadSafe
public interface RequestScopedStorageService extends StorageService {

}