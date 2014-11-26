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

package org.opensaml.saml.metadata.resolver;

/**
 * Marker interface for {@link MetadataResolver} implementations which resolve
 * metadata by dynamically querying for the requested data individually at the time of the
 * resolution operation, for example by invoking a request to a remote network-based
 * metadata publishing endpoint or metadata oracle service. Implementations may however
 * cache the results of previous resolutions so that subsequent queries may be answered
 * locally.
 */
public interface DynamicMetadataResolver extends MetadataResolver {

}
