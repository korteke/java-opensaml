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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.xml.namespace.QName;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.util.XMLConstants;
import org.opensaml.xml.validation.ValidatorSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/** Configuration registry component for registering and retrieving implementation instances 
 * and related configuration relevant to working with XMLObjects, 
 * including builders, marshallers and unmarshallers.
 * 
 * <p>
 * The registry instance to use would typically be retrieved from the {@link ConfigurationService}.
 * </p>
 * 
 */
public class XMLObjectProviderRegistry {

    /** Default object provider. */
    private static QName defaultProvider = new QName(XMLConstants.XMLTOOLING_CONFIG_NS,
            XMLConstants.XMLTOOLING_DEFAULT_OBJECT_PROVIDER);
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(XMLObjectProviderRegistry.class);

    /** Object provider configuration elements indexed by QName. */
    private Map<QName, Element> configuredObjectProviders;

    /** Validator suite configuration elements indexed by suite IDs. */
    private Map<String, Element> validatorSuiteConfigurations;

    /** Configured XMLObject builder factory. */
    private XMLObjectBuilderFactory builderFactory;

    /** Configured XMLObject marshaller factory. */
    private MarshallerFactory marshallerFactory;

    /** Configured XMLObject unmarshaller factory. */
    private UnmarshallerFactory unmarshallerFactory;

    /** Configured ValidatorSuites. */
    private Map<String, ValidatorSuite> validatorSuites;

    /** Configured set of attribute QNames which have been globally registered as having an ID type. */
    private Set<QName> idAttributeNames;

    /** Configured parser pool. */
    private ParserPool parserPool;

    /** Constructor. */
    public XMLObjectProviderRegistry() {
        configuredObjectProviders = new ConcurrentHashMap<QName, Element>(0);
        validatorSuiteConfigurations = new ConcurrentHashMap<String, Element>(0);
        builderFactory = new XMLObjectBuilderFactory();
        marshallerFactory = new MarshallerFactory();
        unmarshallerFactory = new UnmarshallerFactory();
        validatorSuites = new ConcurrentHashMap<String, ValidatorSuite>(5);
        idAttributeNames = new CopyOnWriteArraySet<QName>();
        
        registerIDAttribute(new QName(javax.xml.XMLConstants.XML_NS_URI, "id"));
    }
    
    /**
     * Get the currently configured ParserPool instance.
     * 
     * @return the currently ParserPool
     */
    public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Set the currently configured ParserPool instance.
     * 
     * @param newParserPool the new ParserPool instance to configure
     */
    public void setParserPool(ParserPool newParserPool) {
        parserPool = newParserPool;
    }
    
    /**
     * Gets the QName for the object provider that will be used for XMLObjects that do not have a registered object
     * provider.
     * 
     * @return the QName for the default object provider
     */
    public QName getDefaultProviderQName() {
        return defaultProvider;
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
    public void registerObjectProvider(QName providerName, XMLObjectBuilder builder, Marshaller marshaller,
            Unmarshaller unmarshaller) {
        log.debug("Registering new builder, marshaller, and unmarshaller for {}", providerName);
        builderFactory.registerBuilder(providerName, builder);
        marshallerFactory.registerMarshaller(providerName, marshaller);
        unmarshallerFactory.registerUnmarshaller(providerName, unmarshaller);
    }

    /**
     * Removes the builder, marshaller, and unmarshaller registered to the given key.
     * 
     * @param key the key of the builder, marshaller, and unmarshaller to be removed
     */
    public void deregisterObjectProvider(QName key) {
        log.debug("Unregistering builder, marshaller, and unmarshaller for {}", key);
        configuredObjectProviders.remove(key);
        builderFactory.deregisterBuilder(key);
        marshallerFactory.deregisterMarshaller(key);
        unmarshallerFactory.deregisterUnmarshaller(key);
    }

    /**
     * Gets the XMLObject builder factory that has been configured with information from loaded configuration files.
     * 
     * @return the XMLObject builder factory
     */
    public XMLObjectBuilderFactory getBuilderFactory() {
        return builderFactory;
    }

    /**
     * Gets the XMLObject marshaller factory that has been configured with information from loaded configuration files.
     * 
     * @return the XMLObject marshaller factory
     */
    public MarshallerFactory getMarshallerFactory() {
        return marshallerFactory;
    }

    /**
     * Gets the XMLObject unmarshaller factory that has been configured with information from loaded configuration
     * files.
     * 
     * @return the XMLObject unmarshaller factory
     */
    public UnmarshallerFactory getUnmarshallerFactory() {
        return unmarshallerFactory;
    }

    /**
     * Registers a configured validator suite.
     * 
     * @param suiteId the ID of the suite
     * @param suite the configured suite
     */
    public void registerValidatorSuite(String suiteId, ValidatorSuite suite) {
        validatorSuites.put(suiteId, suite);
    }

    /**
     * Removes a registered validator suite.
     * 
     * @param suiteId the ID of the suite
     */
    public void deregisterValidatorSuite(String suiteId) {
        validatorSuiteConfigurations.remove(suiteId);
        validatorSuites.remove(suiteId);
    }

    /**
     * Gets a configured ValidatorSuite by its ID.
     * 
     * @param suiteId the suite's ID
     * 
     * @return the ValidatorSuite or null if no suite was registered under that ID
     */
    public ValidatorSuite getValidatorSuite(String suiteId) {
        return validatorSuites.get(suiteId);
    }

    /**
     * Register an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be registered
     */
    public void registerIDAttribute(QName attributeName) {
        if (!idAttributeNames.contains(attributeName)) {
            idAttributeNames.add(attributeName);
        }
    }

    /**
     * Deregister an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be de-registered
     */
    public void deregisterIDAttribute(QName attributeName) {
        if (idAttributeNames.contains(attributeName)) {
            idAttributeNames.remove(attributeName);
        }
    }

    /**
     * Determine whether a given attribute is registered as having an ID type.
     * 
     * @param attributeName the QName of the attribute to be checked for ID type.
     * @return true if attribute is registered as having an ID type.
     */
    public boolean isIDAttribute(QName attributeName) {
        return idAttributeNames.contains(attributeName);
    }
    
}