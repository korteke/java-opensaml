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

package org.opensaml.saml.common.binding;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.criterion.EndpointCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.criterion.SignedRequestCriterion;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IndexedEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.AbstractDestructableIdentifiableInitializableComponent;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * Base implementation that resolves and validates protocol/profile endpoints using a combination of supplied
 * parameters and SAML metadata.
 * 
 * <p>SAML metadata rules are followed for deriving candidate endpoints to evaluate. The base class implements
 * only a subset of required functionality, then extracts a set of candidates from metadata if present, and
 * delegates to a subclass to actually evaluate each one for acceptability.</p>
 * 
 * <p>The supported {@link net.shibboleth.utilities.java.support.resolver.Criterion} types and their use follows:</p>
 * 
 * <dl>
 *  <dt>{@link EndpointCriterion} (required)
 *  <dd>Contains a "template" for the eventual {@link Endpoint}(s) to resolve that identifies at minimum the
 *  type of endpoint object (via schema type or element name) to resolve. It MAY contain other attributes that
 *  will be used in matching candidate endpoints for suitability, such as index, binding, location, etc.
 *  
 *  <dt>{@link SignedRequestCriterion}
 *  <dd>If present, and if the supplied {@link EndpointCriterion} contains a fully usable {@link Endpoint},
 *  that endpoint is returned as the sole resolution result, unless a subclass overrides its validation.
 *  
 *  <dt>{@link RoleDescriptorCriterion}
 *  <dd>If present, provides access to the candidate endpoint(s) to attempt resolution against. Strictly optional,
 *  but if absent, the supplied endpoint (from {@link EndpointCriterion}) is returned as the sole result,
 *  whatever its completeness/usability, allowing for subclass validation.
 * </dl>
 * 
 * <p>Subclasses should override the {{@link #doCheckEndpoint(CriteriaSet, Endpoint)} method to implement
 * further criteria.</p>
 */
public abstract class AbstractEndpointResolver extends AbstractDestructableIdentifiableInitializableComponent
        implements EndpointResolver {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AbstractEndpointResolver.class);
    
    /** Constructor. */
    public AbstractEndpointResolver() {
        super.setId(getClass().getName());
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements public Iterable<Endpoint> resolve(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        validateCriteria(criteria);
        
        if (canUseRequestedEndpoint(criteria)) {
            final Endpoint endpoint = criteria.get(EndpointCriterion.class).getEndpoint();
            if (doCheckEndpoint(criteria, endpoint)) {
                return Collections.singletonList(endpoint);
            } else {
                log.debug("{} Requested endpoint was rejected by extended validation process", getLogPrefix());
                return Collections.emptyList();
            }
        }
        
        final List<Endpoint> candidates = getCandidatesFromMetadata(criteria);
        final Iterator<Endpoint> i = candidates.iterator();
        while (i.hasNext()) {
            if (!doCheckEndpoint(criteria, i.next())) {
                i.remove();
            }
        }
        
        log.debug("{} {} endpoints remain after filtering process", getLogPrefix(), candidates.size());
        return candidates;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public Endpoint resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
        validateCriteria(criteria);

        if (canUseRequestedEndpoint(criteria)) {
            final Endpoint endpoint = criteria.get(EndpointCriterion.class).getEndpoint();
            if (doCheckEndpoint(criteria, endpoint)) {
                return endpoint;
            } else {
                log.debug("{} Requested endpoint was rejected by extended validation process", getLogPrefix());
                return null;
            }
        }
        
        for (final Endpoint candidate : getCandidatesFromMetadata(criteria)) {
            if (doCheckEndpoint(criteria, candidate)) {
                return candidate;
            }
        }
        
        log.debug("{} No candidate endpoints met criteria", getLogPrefix());
        return null;
    }
    
    /**
     * Apply the supplied criteria to a candidate endpoint to determine its suitability. 
     * 
     * @param criteria  input criteria set
     * @param endpoint  candidate endpoint
     * 
     * @return  true iff the endpoint meets the supplied criteria
     */
    protected boolean doCheckEndpoint(@Nonnull final CriteriaSet criteria, @Nonnull final Endpoint endpoint) {
        return true;
    }

    /**
     * Verify that the required {@link EndpointCriterion} is present.
     * 
     * @param criteria  input criteria set
     * 
     * @throws ResolverException if the input set is null or no {@link EndpointCriterion} is present
     */
    private void validateCriteria(@Nullable final CriteriaSet criteria) throws ResolverException {
        if (criteria == null) {
            throw new ResolverException("CriteriaSet cannot be null");
        }

        final EndpointCriterion epCriterion = criteria.get(EndpointCriterion.class);
        if (epCriterion == null) {
            throw new ResolverException("EndpointCriterion not supplied");
        }
    }
    
    /**
     * Optimize the case of resolving a single endpoint if a populated endpoint is supplied via
     * criteria, and validation is unnecessary due to a signed request. Note that this endpoint may
     * turn out to be unusable by the caller, but that's immaterial because the requester must have
     * dictated the binding and location, so we're not allowed to ignore that.
     * 
     * @param criteria  input criteria set
     * 
     * @return true iff the supplied endpoint via {@link EndpointCriterion} should be returned
     */
    private boolean canUseRequestedEndpoint(@Nonnull final CriteriaSet criteria) {
        final Endpoint requestedEndpoint = criteria.get(EndpointCriterion.class).getEndpoint();
        if (criteria.contains(SignedRequestCriterion.class)) {
            if (requestedEndpoint.getBinding() != null && (requestedEndpoint.getLocation() != null
                    || requestedEndpoint.getResponseLocation() != null)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get a mutable list of endpoints of a given type found in the metadata role contained in a
     * {@link RoleDescriptorCriterion} (or an empty list if no metadata exists).
     * 
     * <p>The endpoint type to extract is based on the candidate endpoint in an
     * {@link EndpointCriterion}. If the endpoints are indexed, the first list entry will
     * contain the default endpoint to use in the absence of other limiting criteria.</p>
     * 
     * @param criteria input criteria set
     * 
     * @return mutable list of endpoints from the metadata
     */
    @Nonnull @NonnullElements private List<Endpoint> getCandidatesFromMetadata(@Nonnull final CriteriaSet criteria) {
        
        // Check for metadata.
        final RoleDescriptorCriterion role = criteria.get(RoleDescriptorCriterion.class);
        if (role == null) {
            log.debug("{} No metadata supplied, no candidate endpoints to return", getLogPrefix());
            return Lists.newArrayList();
        }
        
        // Determine the QName type of endpoints to extract based on candidate type.
        final EndpointCriterion epCriterion = criteria.get(EndpointCriterion.class);
        QName endpointType = epCriterion.getEndpoint().getSchemaType();
        if (endpointType == null) {
            endpointType = epCriterion.getEndpoint().getElementQName();
        }
        
        // Return the endpoints in the metadata of the candidate type.
        final List<Endpoint> endpoints = role.getRole().getEndpoints(endpointType);
        if (endpoints.isEmpty()) {
            log.debug("{} No endpoints in metadata of type {}", getLogPrefix(), endpointType);
        } else {
            log.debug("{} Returning {} candidate endpoints of type {}", getLogPrefix(), endpoints.size(),
                    endpointType);
        }
        
        // Use a linked list, and move the default endpoint to the head of the list.
        // SAML defaulting rules apply to IndexedEnpdoint types, and require checking
        // for the isDefault attribute.
        final LinkedList<Endpoint> toReturn = Lists.newLinkedList();
        for (final Endpoint endpoint : endpoints) {
            if (endpoint instanceof IndexedEndpoint) {
                Boolean flag = ((IndexedEndpoint) endpoint).isDefault();
                if (flag != null && flag.booleanValue()) {
                    toReturn.addFirst(endpoint);
                } else {
                    toReturn.addLast(endpoint);
                }
            } else {
                toReturn.addLast(endpoint);
            }
        }
        
        return toReturn;
    }

    /**
     * Return a prefix for logging messages for this component.
     * 
     * @return a string for insertion at the beginning of any log messages
     */
    @Nonnull protected String getLogPrefix() {
        return "Endpoint Resolver " + getId() + ":";
    }

}
