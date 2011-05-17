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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.opensaml.core.config.ConfigurationPropertiesSource;
import org.opensaml.util.StringSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A configuration properties source implementation which obtains the properties set
 * from a resource on the filesystem.
 * 
 * <p>
 * This is an abstract implementation.  A concrete implementation must supply the 
 * filesystem path to use via the {@link #getFilename()} method.
 * </p>
 */
public abstract class AbstractFilesystemConfigurationPropertiesSource implements ConfigurationPropertiesSource {
    /** Cache of properties. */
    private Properties cachedProperties;
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(AbstractFilesystemConfigurationPropertiesSource.class);
    
    /** {@inheritDoc} */
    public Properties getProperties() {
        String fileName = StringSupport.trimOrNull(getFilename());
        if (fileName == null) {
            log.warn("No filename was supplied, unable to load properties");
            return null;
        }
        synchronized (this) {
            if (cachedProperties == null) {
                InputStream is = null;
                try {
                    // NOTE: in this invocation style via class loader, resource should NOT have a leading slash
                    // because all names are absolute. This is unlike Class.getResourceAsStream 
                    // where a leading slash is required for absolute names.
                    File file = new File(fileName);
                    if (file.exists()) {
                        is = new FileInputStream(fileName);
                        if (is != null) {
                            Properties props = new Properties();
                            props.load(is);
                            cachedProperties = props;
                        }
                    }
                } catch (FileNotFoundException e) {
                    log.warn("File not found attempting to load configuration properties '" 
                            + fileName + "' from filesystem");
                } catch (IOException e) {
                    log.warn("I/O problem attempting to load configuration properties '" 
                            + fileName + "' from filesystem", e);
                } finally{
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

    /**
     * Get the configuration properties filename.
     * 
     * @return the absolute filename
     */
    protected abstract String getFilename();
}
