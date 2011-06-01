/*
 * Licensed to the University Corporation for Advanced Internet Development, Inc.
 * under one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache 
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

import java.util.Properties;

import org.opensaml.core.config.provider.ThreadLocalConfigurationPropertiesHolder;
import org.opensaml.xml.XMLObjectProviderRegistry;

import junit.framework.TestCase;

/**
 * A class which provides basic testing for the InitializationService.
 */
public class InitializationServiceTest extends TestCase {
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        Properties props = new Properties();
        
        props.setProperty(ConfigurationService.PROPERTY_PARTITION_NAME, this.getClass().getName());
        
        ThreadLocalConfigurationPropertiesHolder.setProperties(props);
    }

    /** {@inheritDoc} */
    protected void tearDown() throws Exception {
        super.tearDown();
        ThreadLocalConfigurationPropertiesHolder.clear();
    }

    public void testProviderInit() throws InitializationException {
        XMLObjectProviderRegistry registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        assertNull("Registry was non-null", registry);        
        
        InitializationService.initialize();
        
        registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        assertNotNull("Registry was null", registry);        
    }

}
