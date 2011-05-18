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

package org.opensaml.core;

import java.util.Properties;

import junit.framework.TestCase;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.provider.ThreadLocalConfigurationPropertiesHolder;
import org.opensaml.xml.XMLObjectProviderRegistry;
import org.opensaml.xml.schema.XSString;

/**
 * Test XMLObject provider initializer for module "core".
 */
public class XMLProviderInitializerTest extends TestCase {
    
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
    
    /**
     * Test basic provider registration.
     * 
     * @throws InitializationException if there is an error during provider init
     */
    public void testProviderInit() throws InitializationException {
        XMLObjectProviderRegistry registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        assertNull("Registry was non-null", registry);
        
        XMLProviderInitializer initializer = new XMLProviderInitializer();
        initializer.init();
        
        registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        assertNotNull("Registry was null", registry);
        assertNotNull("Builder was null", registry.getBuilderFactory().getBuilder(XSString.TYPE_NAME));
        assertNotNull("Unmarshaller was null", registry.getUnmarshallerFactory().getUnmarshaller(XSString.TYPE_NAME));
        assertNotNull("Marshaller was null", registry.getMarshallerFactory().getMarshaller(XSString.TYPE_NAME));
    }

}
