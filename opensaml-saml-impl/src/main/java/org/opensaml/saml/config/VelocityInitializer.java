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

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An initializer which initializes the Velocity template engine environment.
 */
public class VelocityInitializer implements Initializer {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(VelocityInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        try {
            log.debug("Initializing Velocity template engine");
            Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                    "org.apache.velocity.runtime.log.NullLogChute");
            Velocity.setProperty(RuntimeConstants.ENCODING_DEFAULT, "UTF-8");
            Velocity.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
            Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            Velocity.setProperty("classpath.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init();
        } catch (Exception e) {
            throw new InitializationException("Unable to initialize Velocity template engine", e);
        }
    }

}
