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

package org.opensaml.security.config;

import java.util.Arrays;
import java.util.HashSet;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.security.x509.X509Support;
import org.opensaml.security.x509.tls.CertificateNameOptions;
import org.opensaml.security.x509.tls.ClientTLSValidationConfiguration;
import org.opensaml.security.x509.tls.impl.BasicClientTLSValidationConfiguration;

/**
 * An initializer which initializes the global configuration instance of 
 * {@link ClientTLSValidationConfiguration}.
 */
public class ClientTLSValidationConfiguratonInitializer implements Initializer {

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        CertificateNameOptions nameOptions = new CertificateNameOptions();
        nameOptions.setEvaluateSubjectCommonName(true);
        nameOptions.setSubjectAltNames(
                new HashSet<>(Arrays.asList(X509Support.DNS_ALT_NAME, X509Support.URI_ALT_NAME)));
        
        BasicClientTLSValidationConfiguration config = new BasicClientTLSValidationConfiguration();
        config.setCertificateNameOptions(nameOptions);
        
        ConfigurationService.register(ClientTLSValidationConfiguration.class, config);
    }

}