/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.log4j.Logger;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.util.XMLConstants;
import org.opensaml.xml.validation.ValidatorSuite;
import org.w3c.dom.Element;

/**
 * Class for loading library configuration files and retrieving the configured components.
 */
public class Configuration {

    /** Class logger. */
    private static Logger log = Logger.getLogger(Configuration.class);

    /** Default object provider. */
    private static QName defaultProvider = new QName(XMLConstants.XMLTOOLING_CONFIG_NS,
            XMLConstants.XMLTOOLING_DEFAULT_OBJECT_PROVIDER);

    /** Object provider configuration elements indexed by QName. */
    private static FastMap<QName, Element> configuredObjectProviders = new FastMap<QName, Element>();

    /** Validator suite configuration elements indexed by suite IDs. */
    private static FastMap<String, Element> validatorSuiteConfigurations = new FastMap<String, Element>();

    /** Configured XMLObject builder factory. */
    private static XMLObjectBuilderFactory builderFactory = new XMLObjectBuilderFactory();

    /** Configured XMLObject marshaller factory. */
    private static MarshallerFactory marshallerFactory = new MarshallerFactory();

    /** Configured XMLObject unmarshaller factory. */
    private static UnmarshallerFactory unmarshallerFactory = new UnmarshallerFactory();

    /** Configured ValidatorSuites. */
    private static FastMap<String, ValidatorSuite> validatorSuites = new FastMap<String, ValidatorSuite>();
    
    /** Configured set of attribute QNames which have been globally registered as having an ID type. */
    private static FastSet<QName> idAttributeNames = new FastSet<QName>();
    
    /** Constructor. */
    protected Configuration(){
        
    }

    /**
     * Gets the QName for the object provider that will be used for XMLObjects that do not have a registered object
     * provider.
     * 
     * @return the QName for the default object provider
     */
    public static QName getDefaultProviderQName() {
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
     * @param configuration optional XML configuration snippet
     */
    public static void registerObjectProvider(QName providerName, XMLObjectBuilder builder, Marshaller marshaller,
            Unmarshaller unmarshaller, Element configuration) {
        if (log.isDebugEnabled()) {
            log.debug("Registering new builder, marshaller, and unmarshaller for " + providerName);
        }

        configuredObjectProviders.put(providerName, configuration);
        builderFactory.registerBuilder(providerName, builder);
        marshallerFactory.registerMarshaller(providerName, marshaller);
        unmarshallerFactory.registerUnmarshaller(providerName, unmarshaller);
    }

    /**
     * Removes the builder, marshaller, and unmarshaller registered to the given key.
     * 
     * @param key the key of the builder, marshaller, and unmarshaller to be removed
     */
    public static void deregisterObjectProvider(QName key) {
        if (log.isDebugEnabled()) {
            log.debug("Unregistering builder, marshaller, and unmarshaller for " + key);
        }
        configuredObjectProviders.remove(key);
        builderFactory.deregisterBuilder(key);
        marshallerFactory.deregisterMarshaller(key);
        unmarshallerFactory.deregisterUnmarshaller(key);
    }

    /**
     * Gets a clone of the configuration element for a qualified element. Note that this configuration reflects the
     * state of things as they were when the configuration was loaded, applications may have programmatically removed
     * builder, marshallers, and unmarshallers during runtime.
     * 
     * @param qualifedName the namespace qualifed element name of the schema type of the object provider
     * 
     * @return the object provider configuration element or null if no object provider is configured with that name
     */
    public static Element getObjectProviderConfiguration(QName qualifedName) {
        return (Element) configuredObjectProviders.get(qualifedName).cloneNode(true);
    }

    /**
     * Gets the XMLObject builder factory that has been configured with information from loaded configuration files.
     * 
     * @return the XMLObject builder factory
     */
    public static XMLObjectBuilderFactory getBuilderFactory() {
        return builderFactory;
    }

    /**
     * Gets the XMLObject marshaller factory that has been configured with information from loaded configuration files.
     * 
     * @return the XMLObject marshaller factory
     */
    public static MarshallerFactory getMarshallerFactory() {
        return marshallerFactory;
    }

    /**
     * Gets the XMLObject unmarshaller factory that has been configured with information from loaded configuration
     * files.
     * 
     * @return the XMLObject unmarshaller factory
     */
    public static UnmarshallerFactory getUnmarshallerFactory() {
        return unmarshallerFactory;
    }
    
    /**
     * Registers a configured validator suite.
     * 
     * @param suiteId the ID of the suite
     * @param suite the configured suite
     * @param configuration optional XML configuration information
     */
    public static void registerValidatorSuite(String suiteId, ValidatorSuite suite, Element configuration){
        validatorSuiteConfigurations.put(suiteId, configuration);
        validatorSuites.put(suiteId, suite);
    }
    
    /**
     * Removes a registered validator suite.
     * 
     * @param suiteId the ID of the suite
     */
    public static void deregisterValidatorSuite(String suiteId){
        validatorSuiteConfigurations.remove(suiteId);
        validatorSuites.remove(suiteId);
    }
    

    /**
     * Gets a clone of the ValidatorSuite configuration element for the ID. Note that this configuration reflects the
     * state of things as they were when the configuration was loaded, applications may have programmatically removed
     * altered the suite during runtime.
     * 
     * @param suiteId the ID of the ValidatorSuite whose configuration is to be retrieved
     * 
     * @return the validator suite configuration element or null if no suite is configured with that ID
     */
    public static Element getValidatorSuiteConfiguration(String suiteId) {
        return (Element) validatorSuiteConfigurations.get(suiteId).cloneNode(true);
    }
    
    /**
     * Gets a configured ValidatorSuite by its ID.
     * 
     * @param suiteId the suite's ID
     * 
     * @return the ValidatorSuite or null if no suite was registered under that ID
     */
    public static ValidatorSuite getValidatorSuite(String suiteId) {
        return validatorSuites.get(suiteId);
    }
    
    /**
     * Register an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be registered
     */
    public static void registerIDAttribute(QName attributeName) {
        if (! idAttributeNames.contains(attributeName)) {
            idAttributeNames.add(attributeName);
        }
    }
    
    /**
     * Deregister an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be de-registered
     */
    public static void deregisterIDAttribute(QName attributeName) {
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
    public static boolean isIDAttribute(QName attributeName) {
        return idAttributeNames.contains(attributeName);
    }
    
    static {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            if(log.isDebugEnabled()){
                log.debug("VM using JAXP parser " + docBuilder.getClass().getName());
            }

            if (docBuilder.getClass().getName().startsWith("com.sun")) {
                String errorMsg = "\n\n\nOpenSAML requires an xml parser that supports DOM3 calls.\n"
                    + "The JVM is currently configured to use the Sun XML parser, which is known\n"
                    + "to be buggy and can not be used with OpenSAML.  Please endorse a\n"
                    + "a functional JAXP parser such as Xerces.  For instructions on how to endorse\n"
                    + "a new parser see http://java.sun.com/j2se/1.5.0/docs/guide/standards/index.html\n\n\n";

                log.fatal(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        } catch (ParserConfigurationException e) {
            String errorMsg = "Unable to determine XML parser class that will be used.  Unable to proceed.";
            log.fatal(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }
}