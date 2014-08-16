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

package org.opensaml.security.messaging;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509CredentialValidationParameters;

/**
 * Messaging context implementation for holding parameters related to validating {@link X509Credential} instances.
 */
public class X509CredentialSecurityParametersContext extends BaseContext {
    
    /** A {@link X509CredentialValidationParameters} instance. */
    @Nullable private X509CredentialValidationParameters validationParameters;

    /**
     * Get an instance of {@link X509CredentialValidationParameters}.
     * 
     * @return return the parameters instance, may be null
     */
    @Nullable public X509CredentialValidationParameters getValidationParameters() {
        return validationParameters;
    }

    /**
     * Set an instance of {@link X509CredentialValidationParameters}.
     * 
     * @param params the parameters instance to set, may be null
     */
    public void setValidationParameters(@Nullable final X509CredentialValidationParameters params) {
        this.validationParameters = params;
    }

}
