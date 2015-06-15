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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactBuilderFactory;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;

/**
 * SAML-related configuration information.
 * 
 * <p>
 * The configuration instance to use would typically be retrieved from the
 * {@link org.opensaml.core.config.ConfigurationService}.
 * </p>
 * 
 */
public class SAMLConfiguration {

    /** Date format in SAML object, default is yyyy-MM-dd'T'HH:mm:ss.SSS'Z'. */
    private static String defaultDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /** Formatter used to write dates. */
    private DateTimeFormatter dateFormatter;

    /** SAML 1 Artifact factory. */
    private SAML1ArtifactBuilderFactory saml1ArtifactBuilderFactory;

    /** SAML 2 Artifact factory. */
    private SAML2ArtifactBuilderFactory saml2ArtifactBuilderFactory;
    
    /** The list of schemes allowed to appear in binding URLs when encoding a message. 
     * Defaults to 'http' and 'https'. */
    private List<String> allowedBindingURLSchemes;
    

    /**
     * Constructor.
     *
     */
    public SAMLConfiguration() {
        ArrayList<String> schemes = new ArrayList<>();
        schemes.add("http");
        schemes.add("https");
        setAllowedBindingURLSchemes(schemes);
    }

    /**
     * Gets the date format used to string'ify SAML's {@link org.joda.time.DateTime} objects.
     * 
     * @return date format used to string'ify date objects
     */
    public DateTimeFormatter getSAMLDateFormatter() {
        if (dateFormatter == null) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(defaultDateFormat);
            dateFormatter = formatter.withChronology(ISOChronology.getInstanceUTC());
        }
        
        return dateFormatter;
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
    public void setSAMLDateFormat(String format) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        dateFormatter = formatter.withChronology(ISOChronology.getInstanceUTC());
    }

    /**
     * Gets the artifact factory for the library.
     * 
     * @return artifact factory for the library
     */
    public SAML1ArtifactBuilderFactory getSAML1ArtifactBuilderFactory() {
        return saml1ArtifactBuilderFactory;
    }

    /**
     * Sets the artifact factory for the library.
     * 
     * @param factory artifact factory for the library
     */
    public void setSAML1ArtifactBuilderFactory(SAML1ArtifactBuilderFactory factory) {
        saml1ArtifactBuilderFactory = factory;
    }

    /**
     * Gets the artifact factory for the library.
     * 
     * @return artifact factory for the library
     */
    public SAML2ArtifactBuilderFactory getSAML2ArtifactBuilderFactory() {
        return saml2ArtifactBuilderFactory;
    }

    /**
     * Sets the artifact factory for the library.
     * 
     * @param factory artifact factory for the library
     */
    public void setSAML2ArtifactBuilderFactory(SAML2ArtifactBuilderFactory factory) {
        saml2ArtifactBuilderFactory = factory;
    }

    /**
     * Gets the unmodifiable list of schemes allowed to appear in binding URLs when encoding a message. 
     * Defaults to 'http' and 'https'.
     * 
     * @return list of URL schemes allowed to appear in a message
     */
    public List<String> getAllowedBindingURLSchemes() {
        return Collections.unmodifiableList(allowedBindingURLSchemes);
    }

    /**
     * Sets the list of schemes allowed to appear in binding URLs when encoding a message. 
     * The list will be copied.
     * 
     * <p>Note, the appearance of schemes such as 'javascript' may open the system up to attacks 
     * (e.g. cross-site scripting attacks).
     * </p>
     * 
     * @param schemes URL schemes allowed to appear in a message
     */
    public void setAllowedBindingURLSchemes(List<String> schemes) {
        if (schemes == null || schemes.isEmpty()) {
            allowedBindingURLSchemes = Collections.emptyList();
        } else {
            allowedBindingURLSchemes = new ArrayList<>(schemes);
        }
    }
}