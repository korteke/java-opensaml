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

import org.opensaml.core.config.ConfigurationPropertiesSource;

/**
 * A configuration properties source implementation which obtains the properties set
 * from a {@link ThreadLocal} variable.
 * 
 * <p>
 * This supports use cases where applications with different configuration needs are sharing a classloader,
 * making usage of {@link ClasspathConfigurationPropertiesSource} inappropriate.
 * </p>
 * 
 * <p>
 * The thread-local properties instance is retrieved from {@link ThreadLocalConfigurationPropertiesHolder}.
 * An application-specific means must be used to populate this appropriately for each thread, taking care
 * to also clear it at the appropriate time (e.g. before the thread will be returned to a thread-pool
 * and re-used).
 * </p>
 * 
 * <p>
 * For example, in a web application environment which uses Spring dependency injection, a servlet filter
 * would be used to obtain the Properties bean from the Spring web application context (via the servlet context),
 * and then populate the ThreadLocal via {@link ThreadLocalConfigurationPropertiesHolder#setProperties(Properties)},
 * making sure to then also clear it in a <code>finally</code> block after the filter chain has run via
 * {@link ThreadLocalConfigurationPropertiesHolder#clear()}.
 * </p>
 */
public class ThreadLocalConfigurationPropertiesSource implements ConfigurationPropertiesSource {

    /** {@inheritDoc} */
    public Properties getProperties() {
        return ThreadLocalConfigurationPropertiesHolder.getProperties();
    }

}
