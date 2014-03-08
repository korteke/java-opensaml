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

package org.opensaml.xmlsec;

import org.opensaml.core.config.ConfigurationService;

/**
 * Helper methods for working with security configuration.
 */
public final class SecurityConfigurationSupport {
    
    /** Constructor. */
    private SecurityConfigurationSupport() { }

    /**
     * Get the global XML security configuration instance.
     * 
     * @return the global XML security configuration
     */
    public static SecurityConfiguration getGlobalXMLSecurityConfiguration() {
        //TODO remove when refactoring complete
        return ConfigurationService.get(SecurityConfiguration.class);
    }
    
    /**
     * Get the global {@link DecryptionConfiguration}  instance.
     * 
     * @return the global decryption configuration
     */
    public static DecryptionConfiguration getGlobalDecryptionConfiguration() {
        return ConfigurationService.get(DecryptionConfiguration.class);
    }

    /**
     * Get the global {@link EncryptionConfiguration}  instance.
     * 
     * @return the global encryption configuration
     */
    public static EncryptionConfiguration getGlobalEncryptionConfiguration() {
        return ConfigurationService.get(EncryptionConfiguration.class);
    }
    
    /**
     * Get the global {@link SignatureSigningConfiguration }  instance.
     * 
     * @return the global signature signing configuration
     */
    public static SignatureSigningConfiguration getGlobalSignatureSigningConfiguration() {
        return ConfigurationService.get(SignatureSigningConfiguration.class);
    }
    
    /**
     * Get the global {@link SignatureValidationConfiguration}  instance.
     * 
     * @return the global signature validation configuration
     */
    public static SignatureValidationConfiguration getGlobalSignatureValidationConfiguration() {
        return ConfigurationService.get(SignatureValidationConfiguration.class);
    }
    
}
