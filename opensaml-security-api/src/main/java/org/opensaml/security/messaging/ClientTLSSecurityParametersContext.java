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
import org.opensaml.security.x509.tls.ClientTLSValidationParameters;

/**
 * Messaging context implementation for holding parameters related to validating client TLS 
 * {@link org.opensaml.security.x509.X509Credential} instances.
 */
public class ClientTLSSecurityParametersContext extends BaseContext {
    
    /** A {@link ClientTLSValidationParameters} instance. */
    @Nullable private ClientTLSValidationParameters validationParameters;
    
    /** Flag indicating whether to evaluate the certificate presented by the TLS client. */
    private boolean evaluateClientCertificate = true;

    /**
     * Determine whether to evaluate the certificate presented by the TLS client.
     * 
     * <p>Defaults to: <code>true</code></p>
     * 
     * @return true if should evaluate, false otherwise
     */
    public boolean isEvaluateClientCertificate() {
        return evaluateClientCertificate;
    }

    /**
     * Set whether to evaluate the certificate presented by the TLS client.
     * 
     * <p>Defaults to: <code>true</code></p>
     * 
     * @param flag true if should evaluate, false otherwise
     */
    public void setEvaluateClientCertificate(boolean flag) {
        evaluateClientCertificate = flag;
    }

    /**
     * Get an instance of {@link ClientTLSValidationParameters}.
     * 
     * @return return the parameters instance, may be null
     */
    @Nullable public ClientTLSValidationParameters getValidationParameters() {
        return validationParameters;
    }

    /**
     * Set an instance of {@link ClientTLSValidationParameters}.
     * 
     * @param params the parameters instance to set, may be null
     */
    public void setValidationParameters(@Nullable final ClientTLSValidationParameters params) {
        this.validationParameters = params;
    }

}
