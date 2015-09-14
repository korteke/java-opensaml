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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.x509.TrustedNamesCriterion;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * A metadata filter that validates XML signatures.
 */
public class SignatureValidationFilter implements MetadataFilter {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SignatureValidationFilter.class);

    /** Trust engine used to validate a signature. */
    @Nonnull private SignatureTrustEngine signatureTrustEngine;

    /** Indicates whether signed metadata is required. */
    private boolean requireSignature;
    
    /** Set of externally specified default criteria for input to the trust engine. */
    @Nullable private CriteriaSet defaultCriteria;
    
    /** Prevalidator for XML Signature instances. */
    @Nullable private SignaturePrevalidator signaturePrevalidator;
    
    /** Strategy function for extracting dynamic trusted names from signed metadata elements. */
    @Nullable private Function<XMLObject, Set<String>> dynamicTrustedNamesStrategy;

    /**
     * Constructor.
     * 
     * <p>
     * Signature pre-validator defaults to {@link SAMLSignatureProfileValidator}.
     * </p>
     * 
     * <p>
     * Dynamic trusted names strategy defaults to {@link BasicDynamicTrustedNamesStrategy}.
     * </p>
     * 
     * @param engine the trust engine used to validate signatures on incoming metadata.
     */
    public SignatureValidationFilter(@Nonnull final SignatureTrustEngine engine) {
        Constraint.isNotNull(engine, "SignatureTrustEngine cannot be null");
        
        requireSignature = true;

        signatureTrustEngine = engine;
        signaturePrevalidator = new SAMLSignatureProfileValidator();
        dynamicTrustedNamesStrategy = new BasicDynamicTrustedNamesStrategy();
    }

    /**
     * Get the strategy function for extracting dynamic trusted names from signed metadata elements.
     * 
     * <p>Defaults to: {@link BasicDynamicTrustedNamesStrategy}.</p>
     * 
     * @return the function, or null
     */
    @Nullable public Function<XMLObject, Set<String>> getDynamicTrustedNamesStrategy() {
        return dynamicTrustedNamesStrategy;
    }

    /**
     * Get the strategy function for extracting dynamic trusted names from signed metadata elements.
     * 
     * <p>Defaults to: {@link BasicDynamicTrustedNamesStrategy}.</p>
     * 
     * @param strategy the function, may be null
     */
    public void setDynamicTrustedNamesStrategy(@Nullable final Function<XMLObject, Set<String>> strategy) {
        dynamicTrustedNamesStrategy = strategy;
    }

    /**
     * Gets the trust engine used to validate signatures on incoming metadata.
     * 
     * @return trust engine used to validate signatures on incoming metadata
     */
    @Nonnull public SignatureTrustEngine getSignatureTrustEngine() {
        return signatureTrustEngine;
    }
    
    /**
     * Get the validator used to perform pre-validation on Signature tokens.
     * 
     * <p>Defaults to: {@link SAMLSignatureProfileValidator}.</p>
     * 
     * @return the configured Signature validator, or null
     */
    @Nullable public SignaturePrevalidator getSignaturePrevalidator() {
        return signaturePrevalidator;
    }
    
    /**
     * Set the validator used to perform pre-validation on Signature tokens.
     * 
     * <p>Defaults to: {@link SAMLSignatureProfileValidator}.</p>
     * 
     * @param validator the signature prevalidator to use
     */
    public void setSignaturePrevalidator(@Nullable final SignaturePrevalidator validator) {
        signaturePrevalidator = validator;
    }

    /**
     * Get whether incoming metadata's root element is required to be signed.
     * 
     * <p>Defaults to <code>true</code>.</p>
     * 
     * @return whether incoming metadata is required to be signed
     */
    public boolean getRequireSignature() {
        return requireSignature;
    }

    /**
     * Set whether incoming metadata's root element is required to be signed.
     * 
     * <p>Defaults to <code>true</code>.</p>
     * 
     * @param require whether incoming metadata is required to be signed
     */
    public void setRequireSignature(final boolean require) {
        requireSignature = require;
    }
 
    /**
     * Get the optional set of default criteria used as input to the trust engine.
     * 
     * @return the criteria set
     */
    @Nullable public CriteriaSet getDefaultCriteria() {
        return defaultCriteria;
    }
    
    /**
     * Set the optional set of default criteria used as input to the trust engine.
     * 
     * @param newCriteria the new criteria set to use
     */
    public void setDefaultCriteria(@Nullable final CriteriaSet newCriteria) {
        defaultCriteria = newCriteria;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public XMLObject filter(@Nullable final XMLObject metadata) throws FilterException {
        if (metadata == null) {
            return null;
        }
        
        if (!(metadata instanceof SignableXMLObject)) {
            log.warn("Input was not a SignableXMLObject, skipping filtering: {}", metadata.getClass().getName());
            return metadata;
        }
        final SignableXMLObject signableMetadata = (SignableXMLObject) metadata;

        if (!signableMetadata.isSigned()){
            if (getRequireSignature()) {
                throw new FilterException("Metadata root element was unsigned and signatures are required.");
            }
        }
        
        if (signableMetadata instanceof EntityDescriptor) {
            processEntityDescriptor((EntityDescriptor) signableMetadata);
        } else if (signableMetadata instanceof EntitiesDescriptor) {
            processEntityGroup((EntitiesDescriptor) signableMetadata);
        } else {
            log.error("Internal error, metadata object was of an unsupported type: {}", metadata.getClass().getName());
        }
        
        return metadata;
    }
    
    /**
     * Process the signatures on the specified EntityDescriptor and any signed children.
     * 
     * If signature verification fails on a child, it will be removed from the entity descriptor.
     * 
     * @param entityDescriptor the EntityDescriptor to be processed
     * @throws FilterException thrown if an error occurs during the signature verification process
     *                          on the root EntityDescriptor specified
     */
    protected void processEntityDescriptor(@Nonnull final EntityDescriptor entityDescriptor) throws FilterException {
        final String entityID = entityDescriptor.getEntityID();
        log.trace("Processing EntityDescriptor: {}", entityID);
        
        if (entityDescriptor.isSigned()) {
            verifySignature(entityDescriptor, entityID, false);
        }
        
        final Iterator<RoleDescriptor> roleIter = entityDescriptor.getRoleDescriptors().iterator();
        while (roleIter.hasNext()) {
           final RoleDescriptor roleChild = roleIter.next(); 
            if (!roleChild.isSigned()) {
                log.trace("RoleDescriptor member '{}' was not signed, skipping signature processing...",
                        roleChild.getElementQName());
                continue;
            } else {
                log.trace("Processing signed RoleDescriptor member: {}", roleChild.getElementQName());
            }
            
            try {
                final String roleID = getRoleIDToken(entityID, roleChild);
                verifySignature(roleChild, roleID, false);
            } catch (final FilterException e) {
                log.error("RoleDescriptor '{}' subordinate to entity '{}' failed signature verification, " 
                       + "removing from metadata provider", 
                       roleChild.getElementQName(), entityID); 
                // Note that this is ok since we're iterating over an IndexedXMLObjectChildrenList directly,
                // rather than a sublist like in processEntityGroup, and iterator remove() is supported there.
                roleIter.remove();
            }
        }
        
        if (entityDescriptor.getAffiliationDescriptor() != null) {
            final AffiliationDescriptor affiliationDescriptor = entityDescriptor.getAffiliationDescriptor();
            if (!affiliationDescriptor.isSigned()) {
                log.trace("AffiliationDescriptor member was not signed, skipping signature processing...");
            } else {
                log.trace("Processing signed AffiliationDescriptor member with owner ID: {}", 
                        affiliationDescriptor.getOwnerID());
                
                try {
                    verifySignature(affiliationDescriptor, affiliationDescriptor.getOwnerID(), false);
                } catch (final FilterException e) {
                    log.error("AffiliationDescriptor with owner ID '{}' subordinate to entity '{}' " + 
                            "failed signature verification, removing from metadata provider", 
                            affiliationDescriptor.getOwnerID(), entityID); 
                    entityDescriptor.setAffiliationDescriptor(null);
                }
            }
        }
    }
 
    
    /**
     * Process the signatures on the specified EntitiesDescriptor and any signed children.
     * 
     * If signature verification fails on a child, it will be removed from the entities descriptor group.
     * 
     * @param entitiesDescriptor the EntitiesDescriptor to be processed
     * @throws FilterException thrown if an error occurs during the signature verification process
     *                          on the root EntitiesDescriptor specified
     */
    protected void processEntityGroup(@Nonnull final EntitiesDescriptor entitiesDescriptor) throws FilterException {
        final String name = getGroupName(entitiesDescriptor);
        log.trace("Processing EntitiesDescriptor group: {}", name);
        
        if (entitiesDescriptor.isSigned()) {
            verifySignature(entitiesDescriptor, name, true);
        }
        
        // Can't use IndexedXMLObjectChildrenList sublist iterator remove() to remove members,
        // so just note them in a set and then remove after iteration has completed.
        final HashSet<XMLObject> toRemove = new HashSet<>();
        
        final Iterator<EntityDescriptor> entityIter = entitiesDescriptor.getEntityDescriptors().iterator();
        while (entityIter.hasNext()) {
            final EntityDescriptor entityChild = entityIter.next();
            if (!entityChild.isSigned()) {
                log.trace("EntityDescriptor member '{}' was not signed, skipping signature processing...",
                        entityChild.getEntityID());
                continue;
            } else {
                log.trace("Processing signed EntityDescriptor member: {}", entityChild.getEntityID());
            }
            
            try {
                processEntityDescriptor(entityChild);
            } catch (final FilterException e) {
               log.error("EntityDescriptor '{}' failed signature verification, removing from metadata provider", 
                       entityChild.getEntityID()); 
               toRemove.add(entityChild);
            }
        }

        if (!toRemove.isEmpty()) {
            entitiesDescriptor.getEntityDescriptors().removeAll(toRemove);
            toRemove.clear();
        }
        
        final Iterator<EntitiesDescriptor> entitiesIter = entitiesDescriptor.getEntitiesDescriptors().iterator();
        while(entitiesIter.hasNext()) {
            final EntitiesDescriptor entitiesChild = entitiesIter.next();
            final String childName = getGroupName(entitiesChild);
            log.trace("Processing EntitiesDescriptor member: {}", childName);
            try {
                processEntityGroup(entitiesChild);
            } catch (final FilterException e) {
               log.error("EntitiesDescriptor '{}' failed signature verification, removing from metadata provider", 
                       childName); 
               toRemove.add(entitiesChild);
            }
        }
        
        if (!toRemove.isEmpty()) {
            entitiesDescriptor.getEntitiesDescriptors().removeAll(toRemove);
        }
    }
    
    /**
     * Evaluate the signature on the signed metadata instance.
     * 
     * @param signedMetadata the metadata object whose signature is to be verified
     * @param metadataEntryName the EntityDescriptor entityID, EntitiesDescriptor Name,
     *                          AffiliationDescriptor affiliationOwnerID, 
     *                          or RoleDescriptor {@link #getRoleIDToken(String, RoleDescriptor)}
     *                          corresponding to the element whose signature is being evaluated.
     *                          This is used exclusively for logging/debugging purposes and
     *                          should not be used operationally (e.g. for building a criteria set).
     * @param isEntityGroup flag indicating whether the signed object is a metadata group (EntitiesDescriptor),
     *                      primarily useful for constructing a criteria set for the trust engine
     * @throws FilterException thrown if the metadata entry's signature can not be established as trusted,
     *                         or if an error occurs during the signature verification process
     */
    protected void verifySignature(@Nonnull final SignableXMLObject signedMetadata,
            @Nonnull @NotEmpty final String metadataEntryName, final boolean isEntityGroup) throws FilterException {
        
        log.debug("Verifying signature on metadata entry: {}", metadataEntryName);
        
        final Signature signature = signedMetadata.getSignature();
        if (signature == null) {
            // We shouldn't ever be calling this on things that aren't actually signed, but just to be safe...
            log.warn("Signature was null, skipping processing on metadata entry: {}", metadataEntryName);
            return;
        }
        
        performPreValidation(signature, metadataEntryName);
        
        final CriteriaSet criteriaSet = buildCriteriaSet(signedMetadata, metadataEntryName, isEntityGroup);
        
        try {
            if (getSignatureTrustEngine().validate(signature, criteriaSet)) {
                log.trace("Signature trust establishment succeeded for metadata entry {}", metadataEntryName);
                return;
            } else {
                log.error("Signature trust establishment failed for metadata entry {}", metadataEntryName);
                throw new FilterException("Signature trust establishment failed for metadata entry");
            }
        } catch (final SecurityException e) {
            // Treat evaluation errors as fatal
            log.error("Error processing signature verification for metadata entry '{}': {} ",
                    metadataEntryName, e.getMessage());
            throw new FilterException("Error processing signature verification for metadata entry", e);
        }
    }

    /**
     * Perform pre-validation on the Signature token.
     * 
     * @param signature the signature to evaluate
     * @param metadataEntryName the EntityDescriptor entityID, EntitiesDescriptor Name,
     *                          AffiliationDescriptor affiliationOwnerID, 
     *                          or RoleDescriptor {@link #getRoleIDToken(String, RoleDescriptor)}
     *                          corresponding to the element whose signature is being evaluated.
     *                          This is used exclusively for logging/debugging purposes and
     *                          should not be used operationally (e.g. for building a criteria set).
     * @throws FilterException thrown if the signature element fails pre-validation
     */
    protected void performPreValidation(@Nonnull final Signature signature,
            @Nonnull @NotEmpty final String metadataEntryName) throws FilterException {
        if (getSignaturePrevalidator() != null) {
            try {
                getSignaturePrevalidator().validate(signature);
            } catch (final SignatureException e) {
                log.error("Signature on metadata entry '{}' failed signature pre-validation", metadataEntryName);
                throw new FilterException("Metadata instance signature failed signature pre-validation", e);
            }
        }
    }
    
    /**
     * Build the criteria set which will be used as input to the configured trust engine.
     * 
     * @param signedMetadata the metadata element whose signature is being verified
     * @param metadataEntryName the EntityDescriptor entityID, EntitiesDescriptor Name,
     *                          AffiliationDescriptor affiliationOwnerID, 
     *                          or RoleDescriptor {@link #getRoleIDToken(String, RoleDescriptor)}
     *                          corresponding to the element whose signature is being evaluated.
     *                          This is used exclusively for logging/debugging purposes and
     *                          should not be used operationally (e.g. for building the criteria set).
     * @param isEntityGroup flag indicating whether the signed object is a metadata group (EntitiesDescriptor)
     * @return the newly constructed criteria set
     */
    @Nonnull protected CriteriaSet buildCriteriaSet(@Nonnull final SignableXMLObject signedMetadata,
            @Nonnull @NotEmpty final String metadataEntryName, final boolean isEntityGroup) {
        
        final CriteriaSet newCriteriaSet = new CriteriaSet();
        
        if (getDefaultCriteria() != null) {
            newCriteriaSet.addAll( getDefaultCriteria() );
        }
        
        if (!newCriteriaSet.contains(UsageCriterion.class)) {
            newCriteriaSet.add( new UsageCriterion(UsageType.SIGNING) );
        }
        
        // If configured, add dynamic trusted names computed from metadata via strategy function.
        if (getDynamicTrustedNamesStrategy() != null) {
            final Set<String> dynamicTrustedNames = getDynamicTrustedNamesStrategy().apply(signedMetadata);
            if (dynamicTrustedNames != null && !dynamicTrustedNames.isEmpty()) {
                newCriteriaSet.add(new TrustedNamesCriterion(dynamicTrustedNames));
            }
        }
        
        return newCriteriaSet;
    }
    
    /**
     * Get a string token for logging/debugging purposes that contains role information and containing entityID.
     * 
     * @param entityID the containing entityID
     * @param role the role descriptor
     * 
     * @return the constructed role ID token.
     */
    protected String getRoleIDToken(@Nonnull @NotEmpty final String entityID, @Nonnull final RoleDescriptor role) {
        final String roleName = role.getElementQName().getLocalPart();
        return "[Role: " + entityID + "::" + roleName + "]";
    }

    /**
     * Get the group's name, or a suitable facsimile if not named.
     * 
     * @param group the {@link EntitiesDescriptor}
     * 
     * @return a suitable name to use for logging
     */
    @Nonnull @NotEmpty protected String getGroupName(@Nonnull final EntitiesDescriptor group) {
        String name = group.getName();
        if (name != null) {
            return name;
        }
        name = group.getID();
        if (name != null) {
            return name;
        }
        return "(unnamed)";
    }
    
}