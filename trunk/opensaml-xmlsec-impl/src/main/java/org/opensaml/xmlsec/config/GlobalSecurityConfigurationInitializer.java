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

package org.opensaml.xmlsec.config;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.SecurityConfiguration;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureValidationConfiguration;

/**
 * An initializer which initializes the global security configuration.
 */
public class GlobalSecurityConfigurationInitializer implements Initializer {

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        //TODO remove when refactoring done
        ConfigurationService.register(SecurityConfiguration.class, 
                DefaultSecurityConfigurationBootstrap.buildDefaultConfig());
        
        ConfigurationService.register(EncryptionConfiguration.class, 
                DefaultSecurityConfigurationBootstrap.buildDefaultEncryptionConfiguration());
        
        ConfigurationService.register(DecryptionConfiguration.class, 
                DefaultSecurityConfigurationBootstrap.buildDefaultDecryptionConfiguration());
        
        ConfigurationService.register(SignatureSigningConfiguration.class, 
                DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration());
        
        ConfigurationService.register(SignatureValidationConfiguration.class, 
                DefaultSecurityConfigurationBootstrap.buildDefaultSignatureValidationConfiguration());
    }

}
