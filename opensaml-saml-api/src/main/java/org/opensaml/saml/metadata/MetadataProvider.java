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

package org.opensaml.saml.metadata;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/** 
 * A simple interface for components which provide a collection of SAML metadata. 
 * 
 * While the interface for a {@link MetadataProvider} is quite simplistic, the services offered by the provider might
 * be quite complex.  It is recommended that, at a minimum, implementations of this interface provide some mechanism
 * to decrease any potential overhead of rapid, possibly concurrent, calls to {@link #getMetadata()}.  
 */
@ThreadSafe
public interface MetadataProvider {

    /**
     * Gets the collection of metadata offered by this provider.
     * 
     * @return the collection of metadata
     */
    public @Nonnull @NonnullElements Iterable<EntityDescriptor> getMetadata();
    
    // should this return some object that can also carry an expiration time?
}