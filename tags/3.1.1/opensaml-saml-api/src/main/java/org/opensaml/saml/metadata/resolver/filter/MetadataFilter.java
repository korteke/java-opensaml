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

package org.opensaml.saml.metadata.resolver.filter;

import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;

/**
 * A metadata filter is used to process a metadata document after it has been unmarshalled into an 
 * instance of {@link XMLObject}, either an {@link org.opensaml.saml.saml2.metadata.EntityDescriptor}
 * or an {@link org.opensaml.saml.saml2.metadata.EntitiesDescriptor}.
 * 
 * <p>
 * Some example filters might remove everything but identity providers roles, decreasing the data a service provider
 * needs to work with, or a filter could be used to perform integrity checking on the retrieved metadata by verifying a
 * digital signature.
 * </p>
 * 
 * <p>
 * If a filter wishes to completely remove the top-level document element, or otherwise indicate that it
 * has successfully produced an empty data set from the input document, <code>null</code> may be returned
 * by the filter's {@link #filter(XMLObject)} method.
 * </p>
 */
public interface MetadataFilter {

    /**
     * Filters the given metadata, perhaps to remove elements that are not wanted.
     * 
     * @param metadata the metadata to be filtered.
     * 
     * @return the filtered XMLObject, which may or may not be the same as the XMLObject instance
     *          passed in to the method. Maybe be null, for example if the top-level element 
     *          was removed by the filter.
     * 
     * @throws FilterException thrown if an error occurs during the filtering process
     */
    @Nullable XMLObject filter(@Nullable final XMLObject metadata) throws FilterException;
}