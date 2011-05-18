/*
 * Copyright 2006 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.xml;

import javax.xml.namespace.QName;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.validation.ValidatorSuite;

/** Class for loading library configuration files and retrieving the configured components. */
public class Configuration {

    /** Constructor. */
    protected Configuration() {

    }
    
    /**
     * Get the currently configured ParserPool instance.
     * 
     * @return the currently ParserPool
     */
    public static ParserPool getParserPool() {
        return ConfigurationService.get(XMLObjectProviderRegistry.class).getParserPool();
    }

    /**
     * Set the currently configured ParserPool instance.
     * 
     * @param newParserPool the new ParserPool instance to configure
     */
    public static void setParserPool(ParserPool newParserPool) {
        ConfigurationService.get(XMLObjectProviderRegistry.class).setParserPool(newParserPool);
    }
    
    /**
     * Gets the QName for the object provider that will be used for XMLObjects that do not have a registered object
     * provider.
     * 
     * @return the QName for the default object provider
     */
    public static QName getDefaultProviderQName() {
        return ConfigurationService.get(XMLObjectProviderRegistry.class).getDefaultProviderQName();
    }

    /**
     * Adds an object provider to this configuration.
     * 
     * @param providerName the name of the object provider, corresponding to the element name or type name that the
     *            builder, marshaller, and unmarshaller operate on
     * @param builder the builder for that given provider
     * @param marshaller the marshaller for the provider
     * @param unmarshaller the unmarshaller for the provider
     */
    public static void registerObjectProvider(QName providerName, XMLObjectBuilder builder, Marshaller marshaller,
            Unmarshaller unmarshaller) {
        XMLObjectProviderRegistry registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        
        registry.getBuilderFactory().registerBuilder(providerName, builder);
        registry.getMarshallerFactory().registerMarshaller(providerName, marshaller);
        registry.getUnmarshallerFactory().registerUnmarshaller(providerName, unmarshaller);
    }

    /**
     * Removes the builder, marshaller, and unmarshaller registered to the given key.
     * 
     * @param key the key of the builder, marshaller, and unmarshaller to be removed
     */
    public static void deregisterObjectProvider(QName key) {
        XMLObjectProviderRegistry registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        registry.getBuilderFactory().deregisterBuilder(key);
        registry.getMarshallerFactory().deregisterMarshaller(key);
        registry.getUnmarshallerFactory().deregisterUnmarshaller(key);
    }

    /**
     * Gets the XMLObject builder factory that has been configured with information from loaded configuration files.
     * 
     * @return the XMLObject builder factory
     */
    public static XMLObjectBuilderFactory getBuilderFactory() {
        return ConfigurationService.get(XMLObjectProviderRegistry.class).getBuilderFactory();
    }

    /**
     * Gets the XMLObject marshaller factory that has been configured with information from loaded configuration files.
     * 
     * @return the XMLObject marshaller factory
     */
    public static MarshallerFactory getMarshallerFactory() {
        return ConfigurationService.get(XMLObjectProviderRegistry.class).getMarshallerFactory();
    }

    /**
     * Gets the XMLObject unmarshaller factory that has been configured with information from loaded configuration
     * files.
     * 
     * @return the XMLObject unmarshaller factory
     */
    public static UnmarshallerFactory getUnmarshallerFactory() {
        return ConfigurationService.get(XMLObjectProviderRegistry.class).getUnmarshallerFactory();
    }

    /**
     * Registers a configured validator suite.
     * 
     * @param suiteId the ID of the suite
     * @param suite the configured suite
     */
    public static void registerValidatorSuite(String suiteId, ValidatorSuite suite) {
        ConfigurationService.get(XMLObjectProviderRegistry.class).registerValidatorSuite(suiteId, suite);
    }

    /**
     * Removes a registered validator suite.
     * 
     * @param suiteId the ID of the suite
     */
    public static void deregisterValidatorSuite(String suiteId) {
        ConfigurationService.get(XMLObjectProviderRegistry.class).deregisterValidatorSuite(suiteId);
    }

    /**
     * Gets a configured ValidatorSuite by its ID.
     * 
     * @param suiteId the suite's ID
     * 
     * @return the ValidatorSuite or null if no suite was registered under that ID
     */
    public static ValidatorSuite getValidatorSuite(String suiteId) {
        return ConfigurationService.get(XMLObjectProviderRegistry.class).getValidatorSuite(suiteId);
    }

    /**
     * Register an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be registered
     */
    public static void registerIDAttribute(QName attributeName) {
        ConfigurationService.get(XMLObjectProviderRegistry.class).registerIDAttribute(attributeName);
    }

    /**
     * Deregister an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be de-registered
     */
    public static void deregisterIDAttribute(QName attributeName) {
        ConfigurationService.get(XMLObjectProviderRegistry.class).deregisterIDAttribute(attributeName);
    }

    /**
     * Determine whether a given attribute is registered as having an ID type.
     * 
     * @param attributeName the QName of the attribute to be checked for ID type.
     * @return true if attribute is registered as having an ID type.
     */
    public static boolean isIDAttribute(QName attributeName) {
        return ConfigurationService.get(XMLObjectProviderRegistry.class).isIDAttribute(attributeName);
    }

}