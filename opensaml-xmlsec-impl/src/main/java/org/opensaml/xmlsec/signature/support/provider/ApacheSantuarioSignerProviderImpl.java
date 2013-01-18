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

package org.opensaml.xmlsec.signature.support.provider;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureImpl;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link SignerProvider} which is based on the Apache Santuario library
 * and is used with {@link Signature} instances which are instances of {@link SignatureImpl}. 
 */
public class ApacheSantuarioSignerProviderImpl implements SignerProvider {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(ApacheSantuarioSignerProviderImpl.class);

    /** {@inheritDoc} */
    public void signObject(@Nonnull final Signature signature) throws SignatureException {
        Constraint.isNotNull(signature, "Signature cannot be null");
        Constraint.isTrue(Init.isInitialized(), "Apache XML security library is not initialized");
        
        try {
            XMLSignature xmlSignature = ((SignatureImpl) signature).getXMLSignature();

            if (xmlSignature == null) {
                log.error("Unable to compute signature, Signature XMLObject does not have the XMLSignature "
                        + "created during marshalling.");
                throw new SignatureException(
                        "XMLObject does not have XMLSignature instance, unable to compute signature");
            }
            log.debug("Computing signature over XMLSignature object");
            xmlSignature.sign(CredentialSupport.extractSigningKey(signature.getSigningCredential()));
        } catch (XMLSecurityException e) {
            log.error("An error occured computing the digital signature", e);
            throw new SignatureException("Signature computation error", e);
        }
    }

}