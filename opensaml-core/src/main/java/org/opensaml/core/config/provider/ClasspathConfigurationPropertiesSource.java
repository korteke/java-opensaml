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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.opensaml.core.config.ConfigurationPropertiesSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A configuration properties source implementation which obtains the properties set
 * from a resource on the class path.
 */
public class ClasspathConfigurationPropertiesSource implements ConfigurationPropertiesSource {
    
    /** Configuration properties resource name. */
    private static final String RESOURCE_NAME = "opensaml-config.properties";
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(ClasspathConfigurationPropertiesSource.class);

    /** Cache of properties. */
    private Properties cachedProperties;
    
    /** {@inheritDoc} */
    public Properties getProperties() {
        synchronized (this) {
            if (cachedProperties == null) {
                InputStream is = null;
                try {
                    // NOTE: in this invocation style via class loader, resource should NOT have a leading slash
                    // because all names are absolute. This is unlike Class.getResourceAsStream 
                    // where a leading slash is required for absolute names.
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream(RESOURCE_NAME);
                    if (is != null) {
                        Properties props = new Properties();
                        props.load(is);
                        cachedProperties = props;
                    }
                } catch (IOException e) {
                    log.warn("Problem attempting to load configuration properties '" 
                            + RESOURCE_NAME + "' from classpath", e);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
            return cachedProperties;
        }
    }

}
