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

package org.opensaml.saml.security.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.LockableClassToInstanceMultiMap;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.component.InitializableComponent;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.metadata.resolver.RoleDescriptorResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.MutableCredential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.credential.impl.AbstractCriteriaFilteringCredentialResolver;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A credential resolver capable of resolving credentials from SAML 2 metadata.
 * 
 * <p>
 * Credentials may be resolved either by directly supplying an instance of {@link RoleDescriptor} in
 * the input {@link CriteriaSet}, or by looking up the role descriptor via a supplied {@link RoleDescriptorResolver}.
 * </p>
 * 
 * <p>
 * The following resolution modes and associated {@link net.shibboleth.utilities.java.support.resolver.Criterion}
 * inputs are supported:
 * 
 * Direct resolution from a supplied {@link RoleDescriptor}:
 * 
 * <ul> 
 * <li>{@link RoleDescriptorCriterion} - required</li>
 * <li>{@link UsageCriterion} - optional; if absent, the effective value 
 *     {@link UsageType#UNSPECIFIED} will be used for credential resolution.</li>
 * </ul>
 * 
 * Resolution from a metadata source using a {@link RoleDescriptorResolver}:
 * 
 * <ul>
 * <li>{@link EntityIdCriterion} - required</li>
 * <li>{@link EntityRoleCriterion} - required</li>
 * <li>{@link ProtocolCriterion} - optional; if absent, credentials will be resolved from all matching roles, 
 *     regardless of protocol support.</li>
 * <li>{@link UsageCriterion} - optional; if absent, the effective value 
 *     {@link UsageType#UNSPECIFIED} will be used for credential resolution.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * In order to support resolution from a metadata source using {@link EntityIdCriterion} + {@link EntityRoleCriterion}, 
 * an instance of {@link RoleDescriptorResolver} must be supplied.  Otherwise it is optional.
 * </p>
 * 
 * <p>
 * An instance of {@link KeyInfoCredentialResolver} must always be supplied.
 * </p>
 * 
 */
public class MetadataCredentialResolver extends AbstractCriteriaFilteringCredentialResolver 
        implements InitializableComponent {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(MetadataCredentialResolver.class);
    
    /** Metadata RoleDescriptor resolver which is the source of credentials. */
    @Nullable private RoleDescriptorResolver roleDescriptorResolver;

    /** Credential resolver used to resolve credentials from role descriptor KeyInfo elements. */
    @NonnullAfterInit private KeyInfoCredentialResolver keyInfoCredentialResolver;
    
    /** Initialization flag. */
    private boolean isInitialized;
    
    /** Constructor. */
    public MetadataCredentialResolver() {
        super();
    }

    /** {@inheritDoc} */
    public boolean isInitialized() {
        return isInitialized;
    }

    /** {@inheritDoc} */
    public void initialize() throws ComponentInitializationException {
        if (getKeyInfoCredentialResolver() == null) {
            throw new ComponentInitializationException("A KeyInfoCredentialResolver instance is required");
        }
        
        if (getRoleDescriptorResolver() == null) {
            log.info("RoleDescriptorResolver was not supplied, " 
                    + "credentials may only be resolved via RoleDescriptorCriterion");
        }
        
        isInitialized = true;
    }
    
    /**
     * Get the metadata RoleDescriptor resolver instance used by this resolver.
     * 
     * <p>
     * This is optional.  If not supplied, credentials may only be resolved via
     * input of a {@link RoleDescriptorCriterion}.
     * </p>
     *
     * @return the resolver's RoleDescriptor metadata resolver instance
     */
    @Nullable public RoleDescriptorResolver getRoleDescriptorResolver() {
        return roleDescriptorResolver;
    }
    
    /**
     * Set the metadata RoleDescriptor resolver instance used by this resolver.
     * 
     * <p>
     * This is optional.  If not supplied, credentials may only be resolved via
     * input of a {@link RoleDescriptorCriterion}.
     * </p>
     * 
     * @param resolver the new RoleDescriptorResolver to use
     */
    public void setRoleDescriptorResolver(@Nullable final RoleDescriptorResolver resolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        roleDescriptorResolver = resolver;
    }
    
    /**
     * Get the KeyInfo credential resolver used by this entityDescriptorResolver resolver to handle KeyInfo elements.
     * 
     * @return KeyInfo credential resolver
     */
    @NonnullAfterInit public KeyInfoCredentialResolver getKeyInfoCredentialResolver() {
        return keyInfoCredentialResolver;
    }

    /**
     * Set the KeyInfo credential resolver used by this entityDescriptorResolver resolver to handle KeyInfo elements.
     * 
     * @param resolver the new KeyInfoCredentialResolver to use
     */
    public void setKeyInfoCredentialResolver(@Nonnull final KeyInfoCredentialResolver resolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        keyInfoCredentialResolver = Constraint.isNotNull(resolver, "KeyInfoCredentialResolver may not be null");
    }

    /** {@inheritDoc} */
    @Nonnull protected Iterable<Credential> resolveFromSource(@Nonnull final CriteriaSet criteriaSet) 
            throws ResolverException {
        
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        Constraint.isNotNull(criteriaSet, "CriteriaSet was null");

        UsageType usage = getEffectiveUsageInput(criteriaSet);
        
        if (criteriaSet.contains(RoleDescriptorCriterion.class)) {
            RoleDescriptor roleDescriptor = criteriaSet.get(RoleDescriptorCriterion.class).getRole();
            return resolveFromRoleDescriptor(criteriaSet, roleDescriptor, usage);
        } else if (criteriaSet.contains(EntityIdCriterion.class) && criteriaSet.contains(EntityRoleCriterion.class)) {
            if (getRoleDescriptorResolver() == null) {
                throw new ResolverException("EntityID and role input were supplied " 
                        + "but no RoleDescriptorResolver is configured");
            }
            
            String entityID = criteriaSet.get(EntityIdCriterion.class).getEntityId();
            QName role = criteriaSet.get(EntityRoleCriterion.class).getRole();
            
            String protocol = null;
            ProtocolCriterion protocolCriteria = criteriaSet.get(ProtocolCriterion.class);
            if (protocolCriteria != null) {
                protocol = protocolCriteria.getProtocol();
            }
            
            return resolveFromMetadata(criteriaSet, entityID, role, protocol, usage);
            
        } else {
            throw new ResolverException("Criteria contained neither RoleDescriptorCriterion nor " 
                    + "EntityIdCriterion + EntityRoleCriterion, could not perform resolution");
        }

    }
    
    /**
     * Get the effective {@link UsageType} input to use.
     * 
     * @param criteriaSet the criteria set being processed
     * @return the effective usage value
     */
    @Nonnull protected UsageType getEffectiveUsageInput(@Nonnull final CriteriaSet criteriaSet) {
        UsageCriterion usageCriteria = criteriaSet.get(UsageCriterion.class);
        if (usageCriteria != null) {
            return usageCriteria.getUsage();
        } else {
            return UsageType.UNSPECIFIED;
        } 
    }
    
    /**
     * Resolves credentials using a supplied instance of {@link RoleDescriptor}.
     * 
     * @param criteriaSet the criteria set being processed
     * @param roleDescriptor the role descriptor being processed
     * @param usage intended usage of resolved credentials
     * 
     * @return the resolved credentials or null
     * 
     * @throws ResolverException thrown if the key, certificate, or CRL information is represented in an unsupported
     *             format
     */
    @Nonnull protected Collection<Credential> resolveFromRoleDescriptor(@Nonnull final CriteriaSet criteriaSet, 
            @Nonnull final RoleDescriptor roleDescriptor, @Nonnull final UsageType usage) throws ResolverException {
        
        // EntityID here is optional. Not used in resolution, just info stored on the resolved credential(s).
        String entityID = null;
        if (roleDescriptor.getParent() instanceof EntityDescriptor) {
            entityID = ((EntityDescriptor)roleDescriptor.getParent()).getEntityID();
        }
        
        log.debug("Resolving credentials from supplied RoleDescriptor using entityID: {}, usage: {}", entityID, usage);
        HashSet<Credential> credentials = new HashSet<Credential>(3);
        
        processRoleDescriptor(credentials, roleDescriptor, entityID, usage);
        
        return credentials;
    }

    /**
     * Resolves credentials using this resolver's configured instance of {@link RoleDescriptorResolver}.
     * 
     * @param criteriaSet the criteria set being processed
     * @param entityID entityID of the credential owner
     * @param role role in which the entity is operating
     * @param protocol protocol over which the entity is operating (may be null)
     * @param usage intended usage of resolved credentials
     * 
     * @return the resolved credentials or null
     * 
     * @throws ResolverException thrown if the key, certificate, or CRL information is represented in an unsupported
     *             format
     */
    @Nonnull protected Collection<Credential> resolveFromMetadata(@Nonnull final CriteriaSet criteriaSet, 
            @Nonnull @NotEmpty final String entityID, @Nonnull final QName role, 
            @Nullable final String protocol, @Nonnull final UsageType usage) throws ResolverException {

        log.debug("Resolving credentials from metdata using entityID: {}, role: {}, protocol: {}, usage: {}", 
                entityID, role, protocol, usage);
        HashSet<Credential> credentials = new HashSet<Credential>(3);

        Iterable<RoleDescriptor> roleDescriptors = getRoleDescriptors(criteriaSet, entityID, role, protocol);
            
        for (RoleDescriptor roleDescriptor : roleDescriptors) {
            processRoleDescriptor(credentials, roleDescriptor, entityID, usage);
        }

        return credentials;
    }

    /**
     * Process a RoleDescriptor by examing each of its KeyDescriptors.
     * 
     *  @param accumulator the set of credentials being accumulated for return to the caller
     * @param roleDescriptor the KeyDescriptor being processed
     * @param entityID the entity ID of the KeyDescriptor being processed
     * @param usage the credential usage type specified as resolve input
     * 
     * @throws ResolverException if there is a problem resolving credentials from the KeyDescriptor's KeyInfo element
     */
    protected void processRoleDescriptor(@Nonnull final HashSet<Credential> accumulator, 
            @Nonnull final RoleDescriptor roleDescriptor, @Nullable final String entityID, 
            @Nonnull final UsageType usage) throws ResolverException {
        
        List<KeyDescriptor> keyDescriptors = roleDescriptor.getKeyDescriptors();
        for (KeyDescriptor keyDescriptor : keyDescriptors) {
            UsageType mdUsage = keyDescriptor.getUse();
            if (mdUsage == null) {
                mdUsage = UsageType.UNSPECIFIED;
            }
            if (matchUsage(mdUsage, usage)) {
                if (keyDescriptor.getKeyInfo() != null) {
                    extractCredentials(accumulator, keyDescriptor, entityID, mdUsage);
                }
            }
        }
    }

    /**
     * Extract the credentials from the specified KeyDescriptor. First the credentials are looking up in 
     * object metadata cache. If they are not found there, then they will be resolved from the KeyDescriptor's
     * KeyInfo and then cached in the KeyDescriptor's object metadata before returning.
     * 
     * @param accumulator the set of credentials being accumulated for return to the caller
     * @param keyDescriptor the KeyDescriptor being processed
     * @param entityID the entity ID of the KeyDescriptor being processed
     * @param mdUsage the effective credential usage type in effect for the resolved credentials
     * 
     * @throws ResolverException if there is a problem resolving credentials from the KeyDescriptor's KeyInfo element
     */
    protected void extractCredentials(@Nonnull final HashSet<Credential> accumulator, 
            @Nonnull final KeyDescriptor keyDescriptor, @Nullable final String entityID, 
            @Nonnull final UsageType mdUsage) throws ResolverException {
        
        LockableClassToInstanceMultiMap<Object> keyDescriptorObjectMetadata = keyDescriptor.getObjectMetadata();
        ReadWriteLock rwlock = keyDescriptorObjectMetadata.getReadWriteLock();
        
        try {
            rwlock.readLock().lock();
            List<Credential> cachedCreds = keyDescriptorObjectMetadata.get(Credential.class);
            if (!cachedCreds.isEmpty()) {
                log.debug("Resolved cached credentials from KeyDescriptor object metadata");
                accumulator.addAll(cachedCreds);
                return;
            } else {
                log.debug("Found no cached credentials in KeyDescriptor object metadata, resolving from KeyInfo");
            }
        } finally {
            // Note: with the standard Java ReentrantReadWriteLock impl, you can not upgrade a read lock
            // to a write lock!  So have to release here and then acquire the write lock below.
            rwlock.readLock().unlock();
        }
        
        try {
            rwlock.writeLock().lock();
            
            // Need to check again in case another waiting writer beat us in acquiring the write lock
            List<Credential> cachedCreds = keyDescriptorObjectMetadata.get(Credential.class);
            if (!cachedCreds.isEmpty()) {
                log.debug("Credentials were resolved and cached by another thread "
                        + "while this thread was waiting on the write lock");
                accumulator.addAll(cachedCreds);
                return;
            }
            
            List<Credential> newCreds = new ArrayList<>();
            
            CriteriaSet critSet = new CriteriaSet();
            critSet.add(new KeyInfoCriterion(keyDescriptor.getKeyInfo()));
            
            Iterable<Credential> resolvedCreds = getKeyInfoCredentialResolver().resolve(critSet);
            for (Credential cred : resolvedCreds) {
                if (cred instanceof MutableCredential) {
                    MutableCredential mutableCred = (MutableCredential) cred;
                    mutableCred.setEntityId(entityID);
                    mutableCred.setUsageType(mdUsage);
                }
                cred.getCredentialContextSet().add(new SAMLMDCredentialContext(keyDescriptor));
                newCreds.add(cred);
            }
            
            keyDescriptorObjectMetadata.putAll(newCreds);
            
            accumulator.addAll(newCreds);
            
        } finally {
            rwlock.writeLock().unlock();
        }
        
    }

    /**
     * Match usage enum type values from entityDescriptorResolver KeyDescriptor and from credential criteria.
     * 
     * @param metadataUsage the value from the 'use' attribute of a entityDescriptorResolver KeyDescriptor element
     * @param criteriaUsage the value from credential criteria
     * @return true if the two usage specifiers match for purposes of resolving credentials, false otherwise
     */
    protected boolean matchUsage(@Nonnull final UsageType metadataUsage, @Nonnull final UsageType criteriaUsage) {
        if (metadataUsage == UsageType.UNSPECIFIED || criteriaUsage == UsageType.UNSPECIFIED) {
            return true;
        }
        return metadataUsage == criteriaUsage;
    }

    /**
     * Get the list of role descriptors which match the given entityID, role and protocol.
     * 
     * @param criteriaSet criteria set being processed
     * @param entityID entity ID of the credential owner
     * @param role role in which the entity is operating
     * @param protocol protocol over which the entity is operating (may be null)
     * @return a list of role descriptors matching the given parameters, or null
     * @throws ResolverException thrown if there is an error retrieving role descriptors 
     *          from the entityDescriptorResolver provider
     */
    @Nonnull protected Iterable<RoleDescriptor> getRoleDescriptors(@Nonnull final CriteriaSet criteriaSet, 
            @Nonnull final String entityID, @Nonnull final QName role, 
            @Nullable final String protocol) throws ResolverException {
        
        if (getRoleDescriptorResolver() == null) {
            throw new ResolverException("No RoleDescriptorResolver is configured");
        }
        
        try {
            if (log.isDebugEnabled()) {
                log.debug("Retrieving role descriptor metadata for entity '{}' in role '{}' for protocol '{}'", 
                        new Object[] {entityID, role, protocol});
            }
            
            // Construct a new criteria set with just the specific criteria we want considered.
            CriteriaSet criteria = new CriteriaSet(new EntityIdCriterion(entityID), new EntityRoleCriterion(role));
            if (protocol != null) {
                criteria.add(new ProtocolCriterion(protocol));
            }
            
            return getRoleDescriptorResolver().resolve(criteria);

        } catch (ResolverException e) {
            log.error("Unable to resolve information from metadata", e);
            throw new ResolverException("Unable to resolve information from metadata", e);
        }
    }

}