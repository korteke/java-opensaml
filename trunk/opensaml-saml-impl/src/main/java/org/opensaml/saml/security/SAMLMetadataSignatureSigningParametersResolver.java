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

package org.opensaml.saml.security;

import java.security.Key;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        
        List<XMLObject> signingMethods = null;
        
        Extensions extensions = getExtensions(criteria.get(RoleDescriptorCriterion.class).getRole());
        
        if (extensions != null) {
            signingMethods = extensions.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME);
        }
        
        if (signingMethods == null || signingMethods.isEmpty()) {
            super.resolveAndPopulateCredentialAndSignatureAlgorithm(params, criteria, whitelistBlacklistPredicate);
            return;
        }
        
        List<Credential> credentials = getEffectiveSigningCredentials(criteria);
        
        for (XMLObject xmlObject : signingMethods) {
            SigningMethod signingMethod = (SigningMethod) xmlObject;
            if (signingMethod.getAlgorithm() == null 
                    || !whitelistBlacklistPredicate.apply(signingMethod.getAlgorithm())) {
                continue;
            }
            for (Credential credential : credentials) {
                if (credentialSupportsSigningMethod(credential, signingMethod)) {
                    params.setSigningCredential(credential);
                    params.setSignatureAlgorithmURI(signingMethod.getAlgorithm());
                    return;
                }
            }
        }
        
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
                return false;
            }
            
            if (signingMethod.getMaxKeySize() != null && keyLength > signingMethod.getMaxKeySize()) {
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
        
        List<XMLObject> digestMethods = null;
        
        Extensions extensions = getExtensions(criteria.get(RoleDescriptorCriterion.class).getRole());
        
        if (extensions != null) {
            digestMethods = extensions.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME);
        }
        
        if (digestMethods == null || digestMethods.isEmpty()) {
            return super.resolveReferenceDigestMethod(criteria, whitelistBlacklistPredicate);
        }
        
        for (XMLObject xmlObject : digestMethods) {
            DigestMethod digestMethod = (DigestMethod) xmlObject;
            if (digestMethod.getAlgorithm() != null && whitelistBlacklistPredicate.apply(digestMethod.getAlgorithm())) {
                return digestMethod.getAlgorithm();
            }
        }
        
        return super.resolveReferenceDigestMethod(criteria, whitelistBlacklistPredicate);
    }
    
    /**
     * Get the effective {@link Extensions} instance to consider.
     * <p>
     * Note that per the SAML metadata algorithm support extension specification, the parent 
     * EntityDescriptor's Extensions should only be considered if the RoleDescriptor's Extensions
     * contains neither a SigningMethod nor a DigestMethod.
     * </p>
     * 
     * @param roleDescriptor the role descriptor to evaluate
     * @return the extensions instance to use, or null
     */
    @Nullable protected Extensions getExtensions(@Nonnull final RoleDescriptor roleDescriptor) {
        Extensions extensions = roleDescriptor.getExtensions();
        if (extensions != null) {
            if (extensions.getUnknownXMLObjects(SigningMethod.DEFAULT_ELEMENT_NAME).size() > 0
                    || extensions.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME).size() > 0) {
                return extensions;
            }
        }
        
        if (roleDescriptor.getParent() instanceof EntityDescriptor) {
            return ((EntityDescriptor) roleDescriptor.getParent()).getExtensions();
        }
        
        return null;
    }

}
