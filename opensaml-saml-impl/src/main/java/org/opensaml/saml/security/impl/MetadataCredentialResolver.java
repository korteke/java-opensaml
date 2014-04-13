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
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.collection.LockableClassToInstanceMultiMap;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.component.InitializableComponent;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.metadata.resolver.RoleDescriptorResolver;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.MutableCredential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.credential.impl.AbstractCriteriaFilteringCredentialResolver;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A credential resolver capable of resolving credentials from SAML 2 metadata.
 * 
 * <p>
 * The instance of {@link CriteriaSet} passed to {@link #resolve(CriteriaSet)} and {@link #resolveSingle(CriteriaSet)}
 * must minimally contain 2 criteria: {@link EntityIdCriterion} and {@link EntityRoleCriterion}.
 * {@link ProtocolCriterion} is optional; if absent, credentials will be resolved from all matching roles, 
 * regardless of protocol support. {@link UsageCriterion} is optional; if absent, the effective value 
 * {@link UsageType#UNSPECIFIED} will be used for credential resolution.
 * </p>
 * 
 */
public class MetadataCredentialResolver extends AbstractCriteriaFilteringCredentialResolver 
        implements InitializableComponent {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(MetadataCredentialResolver.class);
    
    /** Metadata RoleDescriptor resolver which is the source of credentials. */
    private RoleDescriptorResolver roleDescriptorResolver;

    /** Credential resolver used to resolve credentials from role descriptor KeyInfo elements. */
    private KeyInfoCredentialResolver keyInfoCredentialResolver;
    
    /** Initialization flag. */
    private boolean isInitialized;

    /**
     * Constructor.
     * 
     * @param resolver resolver of metadata RoleDescriptors
     */
    public MetadataCredentialResolver(@Nonnull final RoleDescriptorResolver resolver) {
        super();
        roleDescriptorResolver = Constraint.isNotNull(resolver, "RoleDescriptor resolver cannot be null");
        
    }

    /** {@inheritDoc} */
    public boolean isInitialized() {
        return isInitialized;
    }

    /** {@inheritDoc} */
    public void initialize() throws ComponentInitializationException {
        if (keyInfoCredentialResolver == null) {
            keyInfoCredentialResolver = SecurityConfigurationSupport.getGlobalXMLSecurityConfiguration()
                    .getDefaultKeyInfoCredentialResolver();
        }
        
        isInitialized = true;
    }
    
    /**
     * Get the metadata RoleDescriptor resolver instance used by this resolver.
     *
     * @return the resolver's RoleDescriptor metadata resolver instance
     */
    public RoleDescriptorResolver getRoleDescriptorResolver() {
        return roleDescriptorResolver;
    }
    
    /**
     * Get the KeyInfo credential resolver used by this entityDescriptorResolver resolver to handle KeyInfo elements.
     * 
     * @return KeyInfo credential resolver
     */
    public KeyInfoCredentialResolver getKeyInfoCredentialResolver() {
        return keyInfoCredentialResolver;
    }

    /**
     * Set the KeyInfo credential resolver used by this entityDescriptorResolver resolver to handle KeyInfo elements.
     * 
     * @param keyInfoResolver the new KeyInfoCredentialResolver to use
     */
    public void setKeyInfoCredentialResolver(KeyInfoCredentialResolver keyInfoResolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        keyInfoCredentialResolver = keyInfoResolver;
    }

    /** {@inheritDoc} */
    protected Iterable<Credential> resolveFromSource(CriteriaSet criteriaSet) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);

        checkCriteriaRequirements(criteriaSet);

        String entityID = criteriaSet.get(EntityIdCriterion.class).getEntityId();
        
        EntityRoleCriterion roleCriteria = criteriaSet.get(EntityRoleCriterion.class);
        QName role = roleCriteria.getRole();
        
        String protocol = null;
        ProtocolCriterion protocolCriteria = criteriaSet.get(ProtocolCriterion.class);
        if (protocolCriteria != null) {
            protocol = protocolCriteria.getProtocol();
        }
        
        UsageCriterion usageCriteria = criteriaSet.get(UsageCriterion.class);
        UsageType usage = null;
        if (usageCriteria != null) {
            usage = usageCriteria.getUsage();
        } else {
            usage = UsageType.UNSPECIFIED;
        }

        Collection<Credential> credentials = retrieveFromMetadata(criteriaSet, entityID, role, protocol, usage);

        return credentials;
    }

    /**
     * Check that all necessary credential criteria are available.
     * 
     * @param criteriaSet the credential set to evaluate
     */
    protected void checkCriteriaRequirements(CriteriaSet criteriaSet) {
        EntityIdCriterion entityCriteria = Constraint.isNotNull(criteriaSet.get(EntityIdCriterion.class), 
                "EntityIdCriterion must be supplied");
        Constraint.isNotNull(StringSupport.trimOrNull(entityCriteria.getEntityId()), 
                "Credential owner entity ID criteria value must be supplied");
        
        EntityRoleCriterion roleCriteria = Constraint.isNotNull(criteriaSet.get(EntityRoleCriterion.class), 
                "EntityRoleCriterion must be supplied");
        Constraint.isNotNull(roleCriteria.getRole(), 
                "Credential entity role criteria value must be supplied");
    }

    /**
     * Retrieves credentials from the provided entityDescriptorResolver.
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
    protected Collection<Credential> retrieveFromMetadata(CriteriaSet criteriaSet, String entityID, QName role, 
            String protocol, UsageType usage) throws ResolverException {

        log.debug("Attempting to retrieve credentials from entityDescriptorResolver for entity: {}", entityID);
        HashSet<Credential> credentials = new HashSet<Credential>(3);

        Iterable<RoleDescriptor> roleDescriptors = getRoleDescriptors(criteriaSet, entityID, role, protocol);
            
        for (RoleDescriptor roleDescriptor : roleDescriptors) {
            List<KeyDescriptor> keyDescriptors = roleDescriptor.getKeyDescriptors();
            for (KeyDescriptor keyDescriptor : keyDescriptors) {
                UsageType mdUsage = keyDescriptor.getUse();
                if (mdUsage == null) {
                    mdUsage = UsageType.UNSPECIFIED;
                }
                if (matchUsage(mdUsage, usage)) {
                    if (keyDescriptor.getKeyInfo() != null) {
                        extractCredentials(credentials, keyDescriptor, entityID, mdUsage);
                    }
                }
            }

        }

        return credentials;
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
            @Nonnull final KeyDescriptor keyDescriptor, @Nonnull final String entityID, 
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
    protected boolean matchUsage(UsageType metadataUsage, UsageType criteriaUsage) {
        if (metadataUsage == UsageType.UNSPECIFIED || criteriaUsage == UsageType.UNSPECIFIED) {
            return true;
        }
        return metadataUsage == criteriaUsage;
    }

    /**
     * Get the list of entityDescriptorResolver role descriptors which match the given entityID, role and protocol.
     * 
     * @param criteriaSet criteria set being processed
     * @param entityID entity ID of the credential owner
     * @param role role in which the entity is operating
     * @param protocol protocol over which the entity is operating (may be null)
     * @return a list of role descriptors matching the given parameters, or null
     * @throws ResolverException thrown if there is an error retrieving role descriptors 
     *          from the entityDescriptorResolver provider
     */
    protected Iterable<RoleDescriptor> getRoleDescriptors(CriteriaSet criteriaSet, String entityID, QName role, 
            String protocol) throws ResolverException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Retrieving role descriptor metadata for entity '{}' in role '{}' for protocol '{}'", 
                        new Object[] {entityID, role, protocol});
            }
            
            return getRoleDescriptorResolver().resolve(criteriaSet);

        } catch (ResolverException e) {
            log.error("Unable to resolve information from metadata", e);
            throw new ResolverException("Unable to resolve unformation from metadata", e);
        }
    }

}