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

import java.security.Key;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.saml2.common.Extensions;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.impl.BasicSignatureSigningParametersResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * A specialization of {@link BasicSignatureSigningParametersResolver} which also supports input of SAML metadata, 
 * specifically the {@link SigningMethod} and {@link DigestMethod} extension elements.
 * 
 * <p>
 * In addition to the {@link net.shibboleth.utilities.java.support.resolver.Criterion} inputs documented in 
 * {@link BasicSignatureSigningParametersResolver}, the following inputs are also supported:
 * <ul>
 * <li>{@link RoleDescriptorCriterion} - optional</li> 
 * </ul>
 * </p>
 */
public class SAMLMetadataSignatureSigningParametersResolver extends BasicSignatureSigningParametersResolver {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(SAMLMetadataSignatureSigningParametersResolver.class);

    /** {@inheritDoc} */
    protected void resolveAndPopulateCredentialAndSignatureAlgorithm(@Nonnull final SignatureSigningParameters params, 
            @Nonnull final CriteriaSet criteria, @Nonnull final Predicate<String> whitelistBlacklistPredicate) {
        
        if (!criteria.contains(RoleDescriptorCriterion.class)) {
            super.resolveAndPopulateCredentialAndSignatureAlgorithm(params, criteria, whitelistBlacklistPredicate);
            return;
        }
        
        List<XMLObject> signingMethods = getExtensions(criteria.get(RoleDescriptorCriterion.class).getRole(),
                SigningMethod.DEFAULT_ELEMENT_NAME);
        
        if (signingMethods == null || signingMethods.isEmpty()) {
            super.resolveAndPopulateCredentialAndSignatureAlgorithm(params, criteria, whitelistBlacklistPredicate);
            return;
        }
        
        List<Credential> credentials = getEffectiveSigningCredentials(criteria);
        
        for (XMLObject xmlObject : signingMethods) {
            SigningMethod signingMethod = (SigningMethod) xmlObject;
            
            log.trace("Evaluating SAML metadata SigningMethod with algorithm: {}, minKeySize: {}, maxKeySize: {}", 
                    signingMethod.getAlgorithm(), signingMethod.getMinKeySize(), signingMethod.getMaxKeySize());
            
            if (signingMethod.getAlgorithm() == null 
                    || !whitelistBlacklistPredicate.apply(signingMethod.getAlgorithm())) {
                continue;
            }
            
            for (Credential credential : credentials) {
                
                if (log.isTraceEnabled()) {
                    Key key = CredentialSupport.extractSigningKey(credential);
                    log.trace("Evaluating credential of type: {}, with length: {}", 
                            key != null ? key.getAlgorithm() : "n/a",
                            KeySupport.getKeyLength(key));
                }
                
                if (credentialSupportsSigningMethod(credential, signingMethod)) {
                    log.trace("Credential passed eval against SigningMethod");
                    log.debug("Resolved signature algorithm URI from SAML metadata SigningMethod: {}",
                            signingMethod.getAlgorithm());
                    params.setSigningCredential(credential);
                    params.setSignatureAlgorithmURI(signingMethod.getAlgorithm());
                    return;
                } else {
                    log.trace("Credential failed eval against SigningMethod");
                }
            }
        }
        
        log.debug("Could not resolve signing credential and algorithm based on SAML metadata, " 
                + "falling back to locally configured algorithms");
        
        super.resolveAndPopulateCredentialAndSignatureAlgorithm(params, criteria, whitelistBlacklistPredicate);
    }

    /**
     * Evaluate whether the specified credential is supported for use with the specified {@link SigningMethod}.
     * 
     * @param credential the credential to evaluate
     * @param signingMethod the signing method to evaluate
     * @return true if credential may be used with the supplied algorithm URI, false otherwise
     */
    protected boolean credentialSupportsSigningMethod(@Nonnull final Credential credential, 
            @Nonnull @NotEmpty final SigningMethod signingMethod) {
        if (!credentialSupportsAlgorithm(credential, signingMethod.getAlgorithm())) {
            return false;
        }
        
        if (signingMethod.getMinKeySize() != null  || signingMethod.getMaxKeySize() != null) {
            Key signingKey = CredentialSupport.extractSigningKey(credential);
            if (signingKey == null) {
                log.warn("Could not extract signing key from credential. Failing evaluation");
                return false;
            }
            
            Integer keyLength = KeySupport.getKeyLength(signingKey);
            if (keyLength == null) {
                log.warn("Could not determine key length of candidate signing credential. Failing evaluation");
                return false;
            }
            
            if (signingMethod.getMinKeySize() != null && keyLength < signingMethod.getMinKeySize()) {
                log.trace("Candidate signing credential does not meet minKeySize requirement");
                return false;
            }
            
            if (signingMethod.getMaxKeySize() != null && keyLength > signingMethod.getMaxKeySize()) {
                log.trace("Candidate signing credential does not meet maxKeySize requirement");
                return false;
            }
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Nullable protected String resolveReferenceDigestMethod(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Predicate<String> whitelistBlacklistPredicate) {
        if (!criteria.contains(RoleDescriptorCriterion.class)) {
            return super.resolveReferenceDigestMethod(criteria, whitelistBlacklistPredicate);
        }
        
        List<XMLObject> digestMethods = getExtensions(criteria.get(RoleDescriptorCriterion.class).getRole(),
                DigestMethod.DEFAULT_ELEMENT_NAME);
        
        if (digestMethods == null || digestMethods.isEmpty()) {
            return super.resolveReferenceDigestMethod(criteria, whitelistBlacklistPredicate);
        }
        
        for (XMLObject xmlObject : digestMethods) {
            DigestMethod digestMethod = (DigestMethod) xmlObject;
            
            log.trace("Evaluating SAML metadata DigestMethod with algorithm: {}", digestMethod.getAlgorithm());
            
            if (digestMethod.getAlgorithm() != null && whitelistBlacklistPredicate.apply(digestMethod.getAlgorithm())) {
                log.debug("Resolved reference digest method algorithm URI from SAML metadata DigestMethod: {}",
                        digestMethod.getAlgorithm());
                return digestMethod.getAlgorithm();
            }
        }
        
        log.debug("Could not resolve signature reference digest method algorithm based on SAML metadata, " 
                + "falling back to locally configured algorithms");
        
        return super.resolveReferenceDigestMethod(criteria, whitelistBlacklistPredicate);
    }
    
    /**
     * Get the extensions indicated by the passed QName.  The passed RoleDescriptor's Extensions element
     * is examined first. If at least 1 such extension is found there, that list is returned.
     * If no such extensions are found on the RoleDescriptor, then the RoleDescriptor's parent EntityDescriptor 
     * will be examined, if it exists.
     * 
     * @param roleDescriptor the role descriptor instance to examine
     * @param extensionName the extension name for which to search
     * @return the list of extension XMLObjects found, or null
     */
    @Nullable protected List<XMLObject> getExtensions(@Nonnull final RoleDescriptor roleDescriptor, 
            @Nonnull final QName extensionName) {
        List<XMLObject> result;
        Extensions extensions = roleDescriptor.getExtensions();
        if (extensions != null) {
            result = extensions.getUnknownXMLObjects(extensionName);
            if (!result.isEmpty()) {
                log.trace("Resolved extensions from RoleDescriptor: {}", extensionName);
                return result;
            }
        }
        
        if (roleDescriptor.getParent() instanceof EntityDescriptor) {
            extensions = ((EntityDescriptor)roleDescriptor.getParent()).getExtensions();
            if (extensions != null) {
                result = extensions.getUnknownXMLObjects(extensionName);
                if (!result.isEmpty()) {
                    log.trace("Resolved extensions from parent EntityDescriptor: {}", extensionName);
                    return result;
                }
            }
        }
        return null;
    }

}
