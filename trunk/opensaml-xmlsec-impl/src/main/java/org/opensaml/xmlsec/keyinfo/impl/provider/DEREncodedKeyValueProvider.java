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

package org.opensaml.xmlsec.keyinfo.impl.provider;

import java.security.KeyException;
import java.security.PublicKey;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialContext;
import org.opensaml.security.criteria.KeyAlgorithmCriterion;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoResolutionContext;
import org.opensaml.xmlsec.signature.DEREncodedKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider} which supports {@link DEREncodedKeyValue}.
 */
public class DEREncodedKeyValueProvider extends AbstractKeyInfoProvider {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(DEREncodedKeyValueProvider.class);

    /** {@inheritDoc} */
    public boolean handles(@Nonnull final XMLObject keyInfoChild) {
        return getDEREncodedKeyValue(keyInfoChild) != null;
    }

    /** {@inheritDoc} */
    @Nullable public Collection<Credential> process(@Nonnull final KeyInfoCredentialResolver resolver,
            @Nonnull final XMLObject keyInfoChild, @Nullable final CriteriaSet criteriaSet,
            @Nonnull final KeyInfoResolutionContext kiContext) throws SecurityException {

        DEREncodedKeyValue keyValue = getDEREncodedKeyValue(keyInfoChild);
        if (keyValue == null) {
            return null;
        }

        log.debug("Attempting to extract credential from a DEREncodedKeyValue");
        
        PublicKey pubKey = null;
        try {
            pubKey = KeyInfoSupport.getKey(keyValue);
        } catch (KeyException e) {
            log.error("Error extracting DER-encoded key value", e);
            throw new SecurityException("Error extracting DER-encoded key value", e);
        }
        
        KeyAlgorithmCriterion algorithmCriteria = criteriaSet.get(KeyAlgorithmCriterion.class);
        if (algorithmCriteria != null && algorithmCriteria.getKeyAlgorithm() != null
                && !algorithmCriteria.getKeyAlgorithm().equals(pubKey.getAlgorithm())) {
            log.debug("Criteria specified key algorithm {}, actually {}, skipping",
                    algorithmCriteria.getKeyAlgorithm(), pubKey.getAlgorithm());
            return null;
        }

        BasicCredential cred = new BasicCredential(pubKey);
        if (kiContext != null) {
            cred.getKeyNames().addAll(kiContext.getKeyNames());
        }
        
        CredentialContext credContext = buildCredentialContext(kiContext);
        if (credContext != null) {
            cred.getCredentialContextSet().add(credContext);
        }

        log.debug("Credential successfully extracted from DEREncodedKeyValue");
        LazySet<Credential> credentialSet = new LazySet<Credential>();
        credentialSet.add(cred);
        return credentialSet;
    }

    /**
     * Get the DEREncodedKeyValue from the passed XML object.
     * 
     * @param xmlObject an XML object, presumably a {@link DEREncodedKeyValue}
     * @return the DEREncodedKeyValue which was found, or null if none
     */
    @Nullable protected DEREncodedKeyValue getDEREncodedKeyValue(@Nonnull final XMLObject xmlObject) {
        if (xmlObject instanceof DEREncodedKeyValue) {
            return (DEREncodedKeyValue) xmlObject;
        }
        return null;
    }
}