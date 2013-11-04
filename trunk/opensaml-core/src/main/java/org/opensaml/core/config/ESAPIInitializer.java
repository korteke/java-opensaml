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

package org.opensaml.core.config;

import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An initializer which initializes the OWASP ESAPI security library.
 */
public class ESAPIInitializer implements Initializer {

    /** Logger. */
    private Logger log = LoggerFactory.getLogger(ESAPIInitializer.class);
    
    /** {@inheritDoc} */
    public void init() throws InitializationException {
        String systemPropertyKey = "org.owasp.esapi.SecurityConfiguration";
        String opensamlConfigImpl = ESAPISecurityConfig.class.getName();
        
        String currentValue = System.getProperty(systemPropertyKey);
        if (currentValue == null || currentValue.isEmpty()) {
            log.debug("Setting ESAPI SecurityConfiguration impl to OpenSAML internal class: {}", opensamlConfigImpl);
            System.setProperty(systemPropertyKey, opensamlConfigImpl);
            // We still need to call ESAPI.initialize() despite setting the system property, b/c within the ESAPI class
            // the property is only evaluated once in a static initializer and stored. The initialize method however
            // does overwrite the statically-set value from the system property. But still set the system property for 
            // consistency, so other callers can see what has been set.
            ESAPI.initialize(opensamlConfigImpl);
        } else {
            log.debug("ESAPI SecurityConfiguration impl was already set non-null and non-empty via system property, "
                    + "leaving existing value in place: {}", currentValue);
        }
    }

}