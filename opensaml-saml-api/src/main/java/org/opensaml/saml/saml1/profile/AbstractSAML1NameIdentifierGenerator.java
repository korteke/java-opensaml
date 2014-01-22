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

package org.opensaml.saml.saml1.profile;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.profile.AbstractNameIdentifierGenerator;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for simple implementations of {@link SAML1NameIdentifierGenerator}.
 *
 * <p>This class is suitable for implementing generators that produce simple kinds of identifiers.
 * It supports various options controlling the inclusion of qualifier attributes.</p>
 * 
 * <p>Subclasses must override one of {@link #doGenerate(ProfileRequestContext)} or
 * {@link #getIdentifier(ProfileRequestContext)}.</p>
 */
public abstract class AbstractSAML1NameIdentifierGenerator extends AbstractNameIdentifierGenerator<NameIdentifier>
        implements SAML1NameIdentifierGenerator {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractSAML1NameIdentifierGenerator.class);
    
    /** Builder for objects. */
    @Nonnull private final SAMLObjectBuilder<NameIdentifier> nameBuilder;
    
    /** Constructor. */
    protected AbstractSAML1NameIdentifierGenerator() {
        nameBuilder = (SAMLObjectBuilder<NameIdentifier>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameIdentifier>getBuilderOrThrow(
                        NameIdentifier.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull protected NameIdentifier doGenerate(@Nonnull final ProfileRequestContext profileRequestContext)
            throws ProfileException {
        
        final String identifier = getIdentifier(profileRequestContext);
        
        log.debug("Generating NameIdentifier {} with Format {}", identifier, getFormat());
        
        final NameIdentifier nameIdentifier = nameBuilder.buildObject();
        nameIdentifier.setNameIdentifier(identifier);
        nameIdentifier.setFormat(getFormat());
        nameIdentifier.setNameQualifier(getEffectiveIdPNameQualifier(profileRequestContext));
        
        if (getSPNameQualifier() != null) {
            log.warn("SPNameQualifier not supported for SAML 1 NameIdentifiers, omitting it");
        }

        if (getSPProvidedID() != null) {
            log.warn("SPProvidedID not supported for SAML 1 NameIdentifiers, omitting it");
        }
        
        return nameIdentifier;
    }

}