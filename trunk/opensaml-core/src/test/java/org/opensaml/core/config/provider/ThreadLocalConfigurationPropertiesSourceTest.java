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
package org.opensaml.core.config.provider;

import java.util.Properties;

import junit.framework.TestCase;

import org.opensaml.core.config.ConfigurationPropertiesSource;

/**
 * Test {@link ThreadLocalConfigurationPropertiesSource}.
 */
public class ThreadLocalConfigurationPropertiesSourceTest extends TestCase {
    
    /** The source test to test. */
    private ConfigurationPropertiesSource source;
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        Properties props = new Properties();
        
        props.setProperty("opensaml.config.partitionName", "myapp");
        props.setProperty("opensaml.initializer.foo.flag", "true");
        
        ThreadLocalConfigurationPropertiesHolder.setProperties(props);
    }

    /** {@inheritDoc} */
    protected void tearDown() throws Exception {
        super.tearDown();
        ThreadLocalConfigurationPropertiesHolder.clear();
    }
    
    /**
     *  Test basic retrieval of properties from properties source.
     */
    public void testSource() {
        source = new ThreadLocalConfigurationPropertiesSource();
        Properties props = source.getProperties();
        assertNotNull("Properties was null", props);
        
        assertEquals("Incorrect property value", "myapp", props.getProperty("opensaml.config.partitionName"));
        assertEquals("Incorrect property value", "true", props.getProperty("opensaml.initializer.foo.flag"));
    }

}
