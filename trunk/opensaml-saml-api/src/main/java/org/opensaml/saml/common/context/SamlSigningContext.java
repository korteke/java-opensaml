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

package org.opensaml.saml.common.context;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.xmlsec.SignatureSigningConfiguration;

/**
 * Context that carries information about a SAML signature operations.
 */
public class SamlSigningContext extends BaseContext {

    /** The signing configuration. */
    private SignatureSigningConfiguration signingConfiguration;

    /**
     * Gets the signing configuration.
     * 
     * @return the signing configuration
     */
    @Nullable public SignatureSigningConfiguration getSigningConfiguration() {
        return signingConfiguration;
    }

    /**
     * Sets the signing configuration.
     * 
     * @param configuration the new signing configuration
     */
    public void setSigningConfiguration(@Nullable final SignatureSigningConfiguration configuration) {
        signingConfiguration = configuration;
    }

}