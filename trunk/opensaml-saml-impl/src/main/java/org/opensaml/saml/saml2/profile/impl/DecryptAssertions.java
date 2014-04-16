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

package org.opensaml.saml.saml2.profile.impl;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.collection.Pair;

import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Action to decrypt an {@link EncryptedAssertion} element and replace it with the decrypted
 * {@link Assertion} in situ.
 * 
 * <p>All of the built-in SAML message types that may include an {@link EncryptedAssertion} are
 * potentially handled, but the actual message to handle is obtained via strategy function, by
 * default the inbound message.</p> 
 * 
 * <p>The {@link SecurityParametersContext} governing the decryption process is located by a lookup
 * strategy, by default a child of the inbound message context.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link SAMLEventIds#DECRYPT_ASSERTION_FAILED}
 */
public class DecryptAssertions extends AbstractDecryptAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DecryptAssertions.class);
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final SAMLObject message = getSAMLObject();
        
        try {
            if (message instanceof Response) {
                processResponse(profileRequestContext, (Response) message);
            } else {
                log.debug("{} Message was of unrecognized type {}, nothing to do", getLogPrefix(),
                        message.getClass().getName());
                return;
            }
        } catch (final DecryptionException e) {
            log.warn(getLogPrefix() + "Failure performing decryption", e);
            if (isErrorFatal()) {
                ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.DECRYPT_ASSERTION_FAILED);
            }
        }
    }
    
    /**
     * Decrypt an {@link EncryptedAssertion} and return the result.
     * 
     * @param profileRequestContext current profile request context
     * @param encAssert the encrypted object
     * 
     * @return the decrypted assertion, or null if the object did not need decryption
     * @throws DecryptionException if an error occurs during decryption
     */
    @Nullable private Assertion processEncryptedAssertion(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final EncryptedAssertion encAssert) throws DecryptionException {
        
        if (!getDecryptionPredicate().apply(
                new Pair<ProfileRequestContext,EncryptedElementType>(profileRequestContext, encAssert))) {
            return null;
        }
        
        if (getDecrypter() == null) {
            throw new DecryptionException("No decryption parameters, unable to decrypt EncryptedAssertion");
        }
        
        return getDecrypter().decrypt(encAssert);
    }

    /**
     * Decrypt any {@link EncryptedAssertion} found in a response and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param response   response to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processResponse(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final Response response) throws DecryptionException {

        final Collection<Assertion> decrypteds = Lists.newArrayList();
        
        final Iterator<EncryptedAssertion> i = response.getEncryptedAssertions().iterator();
        while (i.hasNext()) {
            log.debug("{} Decrypting EncryptedAssertion in Response", getLogPrefix());
            try {
                final Assertion decrypted = processEncryptedAssertion(profileRequestContext, i.next());
                if (decrypted != null) {
                    decrypteds.add(decrypted);
                    i.remove();
                }
            } catch (final DecryptionException e) {
                if (isErrorFatal()) {
                    throw e;
                }
                log.warn(getLogPrefix() + "Trapped failure decrypting EncryptedAttribute in AttributeStatement", e);
            }
        }
        
        response.getAssertions().addAll(decrypteds); 
    }
    
}