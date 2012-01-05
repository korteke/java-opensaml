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

import net.shibboleth.utilities.java.support.component.IdentifiableComponent;
import net.shibboleth.utilities.java.support.component.ValidatableComponent;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Resolver;

import org.opensaml.saml2.metadata.EntityDescriptor;

/**
 * A metadata resolver that scans SAML metadata for Entities that match a given set of criteria.
 * 
 * Note, this interface differs from a {@link org.opensaml.saml2.metadata.provider.MetadataProvider} in that the
 * provider is focused on reading in batches of metadata and making them accessible while the resolver is focused on
 * searching for metadata. The resolution process may simply search a static batch or metadata provided by a
 * {@link org.opensaml.saml2.metadata.provider.MetadataProvider} or it may do something more dynamic such as querying a
 * remote service.
 * 
 * At a minimum, a {@link MetadataResolver} implementation MUST support the following criteria:
 * <ul>
 * <li>{@link org.opensaml.saml.criterion.EntityIdCriterion}</li>
 * </ul>
 * 
 * Implementations SHOULD also support the following criteria when possible:
 * <ul>
 * <li>{@link org.opensaml.saml.criterion.ProtocolCriterion}</li>
 * <li>{@link org.opensaml.saml.criterion.EntityRoleCriterion}</li>
 * <li>{@link org.opensaml.saml.criterion.BindingCriterion}</li>
 * </ul>
 */
public interface MetadataResolver extends Resolver<EntityDescriptor, CriteriaSet>, IdentifiableComponent,
        ValidatableComponent {

}