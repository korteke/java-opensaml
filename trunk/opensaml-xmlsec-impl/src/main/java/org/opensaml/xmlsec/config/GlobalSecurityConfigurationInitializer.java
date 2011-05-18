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
package org.opensaml.xmlsec.config;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.xml.security.BasicSecurityConfiguration;
import org.opensaml.xml.security.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xml.security.SecurityConfiguration;

/**
 * An initializer which initializes the global security configuration.
 */
public class GlobalSecurityConfigurationInitializer implements Initializer {

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        BasicSecurityConfiguration secConfig = DefaultSecurityConfigurationBootstrap.buildDefaultConfig();
        ConfigurationService.register(SecurityConfiguration.class, secConfig);
    }

}
