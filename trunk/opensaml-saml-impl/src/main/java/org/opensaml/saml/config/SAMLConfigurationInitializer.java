/*
 * Copyright 2011 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensaml.saml.config;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.saml1.binding.artifact.SAML1ArtifactBuilderFactory;
import org.opensaml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An initializer which initializes the {@link SAMLConfiguration} instance held
 * by the {@link ConfigurationService}.
 * 
 * <p>
 * This includes the artifact factories for SAML 1 and SAML 2 artifacts.
 * </p>
 */
public class SAMLConfigurationInitializer implements Initializer {
    
    private Logger log = LoggerFactory.getLogger(SAMLConfigurationInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        log.debug("Initializing SAML Artifact builder factories");
        SAMLConfiguration config = null;
        
        synchronized (ConfigurationService.class) {
            config = ConfigurationService.get(SAMLConfiguration.class);
            if (config == null) {
                config = new SAMLConfiguration();
                ConfigurationService.register(SAMLConfiguration.class, config);
            }
        }
        
        config.setSAML1ArtifactBuilderFactory(new SAML1ArtifactBuilderFactory());
        config.setSAML2ArtifactBuilderFactory(new SAML2ArtifactBuilderFactory());
    }

}
