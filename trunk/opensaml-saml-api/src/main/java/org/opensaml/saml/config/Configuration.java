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

package org.opensaml.saml.config;

import org.joda.time.format.DateTimeFormatter;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactBuilderFactory;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;

/**
 * OpenSAML configuration singleton.
 * 
 * The library must be initialized with a set of configurations prior to usage. This is often done by invoking
 * {@link DefaultBootstrap#bootstrap()} but may done in any manner so long as all the needed object providers and
 * artifact factory are created and registered with the configuration.
 */
public class Configuration extends org.opensaml.core.xml.XMLObjectProviderRegistrySupport {

    /**
     * Gets the date format used to string'ify SAML's {@link org.joda.time.DateTime} objects.
     * 
     * @return date format used to string'ify date objects
     */
    public static DateTimeFormatter getSAMLDateFormatter() {
        return ConfigurationService.get(SAMLConfiguration.class).getSAMLDateFormatter();
    }

    /**
     * Sets the date format used to string'ify SAML's date/time objects.
     * 
     * See the
     * {@link <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/text/SimpleDateFormat.html">SimpleDateFormat</a>}
     * documentation for format syntax.
     * 
     * @param format date format used to string'ify date objects
     */
    public static void setSAMLDateFormat(String format) {
        ConfigurationService.get(SAMLConfiguration.class).setSAMLDateFormat(format);
    }

    /**
     * Gets the artifact factory for the library.
     * 
     * @return artifact factory for the library
     */
    public static SAML1ArtifactBuilderFactory getSAML1ArtifactBuilderFactory() {
        return ConfigurationService.get(SAMLConfiguration.class).getSAML1ArtifactBuilderFactory();
    }

    /**
     * Sets the artifact factory for the library.
     * 
     * @param factory artifact factory for the library
     */
    public static void setSAML1ArtifactBuilderFactory(SAML1ArtifactBuilderFactory factory) {
        ConfigurationService.get(SAMLConfiguration.class).setSAML1ArtifactBuilderFactory(factory);
    }

    /**
     * Gets the artifact factory for the library.
     * 
     * @return artifact factory for the library
     */
    public static SAML2ArtifactBuilderFactory getSAML2ArtifactBuilderFactory() {
        return ConfigurationService.get(SAMLConfiguration.class).getSAML2ArtifactBuilderFactory();
    }

    /**
     * Sets the artifact factory for the library.
     * 
     * @param factory artifact factory for the library
     */
    public static void setSAML2ArtifactBuilderFactory(SAML2ArtifactBuilderFactory factory) {
        ConfigurationService.get(SAMLConfiguration.class).setSAML2ArtifactBuilderFactory(factory);
    }
}