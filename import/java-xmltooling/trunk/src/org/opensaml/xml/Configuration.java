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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLConstants;
import org.opensaml.xml.util.XMLHelper;
import org.opensaml.xml.validation.Validator;
import org.opensaml.xml.validation.ValidatorSuite;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for loading library configuration files and retrieving the configured components.
 */
public class Configuration {

    /** Logger */
    private final static Logger log = Logger.getLogger(Configuration.class);

    /** Schema for validating a configuration file */
    private static Schema configurationSchema;

    /** Whether to ignore unknown attributes when they are encountered */
    private static boolean ignoreUnknownAttributes = true;

    /** Whether to ignore unknown elements when they are ecnountered */
    private static boolean ignoreUnknownElements = true;

    /** Object provider configuration elements indexed by QName */
    private static HashMap<QName, Element> configuredObjectProviders = new HashMap<QName, Element>();

    /** Validator suite configuration elements indexed by suite IDs */
    private static HashMap<String, Element> validatorSuiteConfigurations = new HashMap<String, Element>();

    /** Configured XMLObject builder factory */
    private static XMLObjectBuilderFactory builderFactory = new XMLObjectBuilderFactory();

    /** Configured XMLObject marshaller factory */
    private static MarshallerFactory marshallerFactory = new MarshallerFactory();

    /** Configured XMLObject unmarshaller factory */
    private static UnmarshallerFactory unmarshallerFactory = new UnmarshallerFactory();

    /** Configured ValidatorSuites */
    private static HashMap<String, ValidatorSuite> validatorSuites = new HashMap<String, ValidatorSuite>();

    /**
     * Constructor
     */
    private Configuration() {

    }

    /**
     * Loads the configurtion file(s) from the given file. If the file is a directory each file within the directory is
     * loaded.
     * 
     * @param configurationFile the configuration file(s) to be loaded
     * 
     * @throws ConfigurationException thrown if the configuration file(s) can not be be read or invalid
     */
    public static void load(File configurationFile) throws ConfigurationException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document configuration;

            if (configurationFile.isDirectory()) {
                File[] configurations = configurationFile.listFiles();
                for (int i = 0; i < configurations.length; i++) {
                    if (log.isDebugEnabled()) {
                        log.debug("Parsing configuration file " + configurations[i].getAbsolutePath());
                    }
                    configuration = documentBuilder.parse(new FileInputStream(configurations[i]));
                    load(configuration);
                }
            }

            // Given file is not a directory so try to load it directly
            if (log.isDebugEnabled()) {
                log.debug("Parsing configuration file " + configurationFile.getAbsolutePath());
            }
            configuration = documentBuilder.parse(new FileInputStream(configurationFile));
            load(configuration);
        } catch (Exception e) {
            log.fatal("Unable to parse configuration file(s) in " + configurationFile.getAbsolutePath(), e);
            throw new ConfigurationException("Unable to parse configuration file(s) in "
                    + configurationFile.getAbsolutePath(), e);
        }
    }

    /**
     * Loads the configuration docuement.
     * 
     * @param configuration the configurationd document
     * @throws ConfigurationException thrown if the configuration file(s) can not be be read or invalid
     */
    public static void load(Document configuration) throws ConfigurationException {
        if (log.isDebugEnabled()) {
            log.debug("Loading configuration from XML Document");
        }

        if (log.isTraceEnabled()) {
            log.trace("\n" + XMLHelper.nodeToString(configuration.getDocumentElement()));
        }

        // Schema validation
        if (log.isDebugEnabled()) {
            log.debug("Schema validating configuration Document");
        }
        validateConfiguration(configuration);
        if (log.isDebugEnabled()) {
            log.debug("Configuration document validated");
        }

        // Initialize object providers
        NodeList objectProviders = configuration.getDocumentElement().getElementsByTagNameNS(
                XMLConstants.XMLTOOLING_CONFIG_NS, "ObjectProviders");
        if (objectProviders.getLength() > 0) {
            if (log.isInfoEnabled()) {
                log.info("Preparing to load ObjectProviders");
            }
            initializeObjectProviders((Element) objectProviders.item(0));
            if (log.isInfoEnabled()) {
                log.info("ObjectProviders load complete");
            }
        }

        // Initialize validator suites
        NodeList validatorSuites = configuration.getDocumentElement().getElementsByTagNameNS(
                XMLConstants.XMLTOOLING_CONFIG_NS, "ValidatorSuites");
        if (validatorSuites.getLength() > 0) {
            if (log.isInfoEnabled()) {
                log.info("Preparing to load ValidatorSuites");
            }
            initializeValidatorSuites((Element) validatorSuites.item(0));
            if (log.isInfoEnabled()) {
                log.info("ValidatorSuites load complete");
            }
        }
    }

    /**
     * Gets whether unknown attributes should be ignored during unmarshalling. If this is false and an unknown attribute
     * is encountered an {@link UnknownAttributeException} is thrown.
     * 
     * @return whether unknown attributes should be ignored during unmarshalling
     */
    public static boolean ignoreUnknownAttributes() {
        return ignoreUnknownAttributes;
    }

    /**
     * Gets whether unknown elements should be ignored during unmarshalling. If this is false and an unknown element is
     * encountered an {@link UnknownElementException} is thrown.
     * 
     * @return whether unknown elements should be ignored during unmarshalling
     */
    public static boolean ignoreUnknownElements() {
        return ignoreUnknownElements;
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
     * Removes the builder, marshaller, and unmarshaller registered to the given key.
     * 
     * @param key the key of the builder, marshaller, and unmarshaller to be removed
     */
    public static void unregisterObjectProvider(QName key) {
        if (log.isDebugEnabled()) {
            log.debug("Unregistering builder, marshaller, and unmarshaller for " + key);
        }
        builderFactory.unregisterBuilder(key);
        marshallerFactory.deregisterMarshaller(key);
        unmarshallerFactory.deregisterUnmarshaller(key);
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
     * Intializes the object providers defined in the configuration file.
     * 
     * @param objectProviders the configuration for the various object providers
     * 
     * @throws ConfigurationException thrown if the configuration elements are invalid
     */
    private static void initializeObjectProviders(Element objectProviders) throws ConfigurationException {

        String ignoreAttributesAttr = objectProviders.getAttributeNS(null, "ignoreUnknownAttributes");
        if (!DatatypeHelper.isEmpty(ignoreAttributesAttr)) {
            ignoreUnknownAttributes = Boolean.parseBoolean(ignoreAttributesAttr);
            if (log.isDebugEnabled()) {
                log.debug("ingoreUnknownAttributes set to " + ignoreUnknownAttributes);
            }
        }

        String ignoreElementsAttr = objectProviders.getAttributeNS(null, "ignoreUnknownElements");
        if (!DatatypeHelper.isEmpty(ignoreElementsAttr)) {
            ignoreUnknownElements = Boolean.parseBoolean(ignoreElementsAttr);
            if (log.isDebugEnabled()) {
                log.debug("ingoreUnknownElements set to " + ignoreUnknownElements);
            }
        }

        // Process ObjectProvider child elements
        Element objectProvider;
        Attr qNameAttrib;
        QName objectProviderName;
        Element builderConfiguration;
        Element marshallerConfiguration;
        Element unmarshallerConfiguration;

        NodeList providerList = objectProviders.getElementsByTagNameNS(XMLConstants.XMLTOOLING_CONFIG_NS,
                "ObjectProvider");
        for (int i = 0; i < providerList.getLength(); i++) {
            objectProvider = (Element) providerList.item(i);

            // Get the element name of type this object provider is for
            qNameAttrib = objectProvider.getAttributeNodeNS(null, "qualifiedName");
            objectProviderName = XMLHelper.getAttributeValueAsQName(qNameAttrib);

            if (log.isDebugEnabled()) {
                log.debug("Initializing object provider " + objectProviderName);
            }

            try {
                builderConfiguration = (Element) objectProvider.getElementsByTagNameNS(
                        XMLConstants.XMLTOOLING_CONFIG_NS, "BuilderClass").item(0);
                initalizeObjectProviderBuilderClass(objectProviderName, builderConfiguration);

                marshallerConfiguration = (Element) objectProvider.getElementsByTagNameNS(
                        XMLConstants.XMLTOOLING_CONFIG_NS, "MarshallingClass").item(0);
                initalizeObjectProviderMarshallerClass(objectProviderName, marshallerConfiguration);

                unmarshallerConfiguration = (Element) objectProvider.getElementsByTagNameNS(
                        XMLConstants.XMLTOOLING_CONFIG_NS, "UnmarshallingClass").item(0);
                initalizeObjectProviderUnmarshallerClass(objectProviderName, unmarshallerConfiguration);

                configuredObjectProviders.put(objectProviderName, objectProvider);

                if (log.isDebugEnabled()) {
                    log.debug(objectProviderName + " intialized and configuration cached");
                }
            } catch (ConfigurationException e) {
                log.fatal("Error initializing object provier " + objectProvider, e);
                // clean up any parts of the object provider that might have been registered before the failure
                unregisterObjectProvider(objectProviderName);
                throw e;
            }
        }
    }

    /**
     * Intializes the builder class for the given object provider.
     * 
     * @param objectProviderName the name of the object provider
     * @param builderConfiguration the BuilderClass configuration element
     * 
     * @throws ConfigurationException thrown if the configuration elements are invalid
     */
    private static void initalizeObjectProviderBuilderClass(QName objectProviderName, Element builderConfiguration)
            throws ConfigurationException {
        String builderClassName = builderConfiguration.getAttributeNS(null, "className");
        builderClassName = DatatypeHelper.safeTrimOrNullString(builderClassName);

        if (builderClassName == null) {
            if (log.isDebugEnabled()) {
                log.debug("No builder class provided for object provider " + objectProviderName);
            }
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Initializing builder " + builderClassName + " for object provider" + objectProviderName);
        }

        if (log.isTraceEnabled()) {
            log.trace("\n" + XMLHelper.nodeToString(builderConfiguration));
        }

        try {
            XMLObjectBuilder objectBuilder = (XMLObjectBuilder) createClassInstance(builderClassName);
            builderFactory.registerBuilder(objectProviderName, objectBuilder);
        } catch (InstantiationException e) {
            log.fatal("Unable to create instance of builder class " + builderClassName + " for object provider "
                    + objectProviderName);
            throw new ConfigurationException("Unable to create instance of builder class " + builderClassName
                    + " for object provider " + objectProviderName, e);
        }
    }

    /**
     * Intializes the marshaller class for the given object provider.
     * 
     * @param objectProviderName the name of the object provider
     * @param marshallerConfiguration the Marshaller configuration element
     * 
     * @throws ConfigurationException thrown if the configuration elements are invalid
     */
    private static void initalizeObjectProviderMarshallerClass(QName objectProviderName, Element marshallerConfiguration)
            throws ConfigurationException {
        String marshallerClassName = marshallerConfiguration.getAttributeNS(null, "className");
        marshallerClassName = DatatypeHelper.safeTrimOrNullString(marshallerClassName);

        if (marshallerClassName == null) {
            if (log.isDebugEnabled()) {
                log.debug("No marshaller class provided for object provider " + objectProviderName);
            }
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Initializing marshaller " + marshallerClassName + " for Object " + objectProviderName);
        }

        if (log.isTraceEnabled()) {
            log.trace("\n" + XMLHelper.nodeToString(marshallerConfiguration));
        }

        try {
            Marshaller objectMarshaller = (Marshaller) createClassInstance(marshallerClassName);
            marshallerFactory.registerMarshaller(objectProviderName, objectMarshaller);
        } catch (InstantiationException e) {
            log.fatal("Unable to create instance of marshaller class " + marshallerClassName + " for object provider "
                    + objectProviderName);
            throw new ConfigurationException("Unable to create instance of marshaller class " + marshallerClassName
                    + " for object provider " + objectProviderName, e);
        }
    }

    /**
     * Intializes the unmarshaller class for the given object provider.
     * 
     * @param objectProviderName the name of the object provider
     * @param marshallerClass the Marshaller configuration element
     * 
     * @throws ConfigurationException thrown if the configuration elements are invalid
     */
    private static void initalizeObjectProviderUnmarshallerClass(QName objectProviderName,
            Element unmarshallerConfiguration) throws ConfigurationException {
        String unmarshallerClassName = unmarshallerConfiguration.getAttributeNS(null, "className");
        unmarshallerClassName = DatatypeHelper.safeTrimOrNullString(unmarshallerClassName);

        if (unmarshallerClassName == null) {
            if (log.isDebugEnabled()) {
                log.debug("No unmarshaller class provided for object provider " + objectProviderName);
            }
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Initializing unmarshaller " + unmarshallerClassName + " for Object " + objectProviderName);
        }

        if (log.isTraceEnabled()) {
            log.trace("\n" + XMLHelper.nodeToString(unmarshallerConfiguration));
        }

        try {
            Unmarshaller objectUnmarshaller = (Unmarshaller) createClassInstance(unmarshallerClassName);
            unmarshallerFactory.registerUnmarshaller(objectProviderName, objectUnmarshaller);
        } catch (InstantiationException e) {
            log.fatal("Unable to create instance of unmarshaller class " + unmarshallerClassName
                    + " for object provider " + objectProviderName);
            throw new ConfigurationException("Unable to create instance of unmarshaller class " + unmarshallerClassName
                    + " for object provider " + objectProviderName, e);
        }
    }

    /**
     * Initializes the validator suites specified in the configuration file.
     * 
     * @param validatorSuitesElement the ValidatorSuites element from the configuration file
     * 
     * @throws ConfigurationException thrown if there is a problem initializing the validator suites, usually because of
     *             malformed elements
     */
    private static void initializeValidatorSuites(Element validatorSuitesElement) throws ConfigurationException {
        ValidatorSuite validatorSuite;
        Validator validator;
        Element validatorSuiteElement;
        String validatorSuiteId;
        Element validatorElement;
        String validatorClassName;
        QName validatorQName;

        NodeList validatorSuiteList = validatorSuitesElement.getElementsByTagNameNS(XMLConstants.XMLTOOLING_CONFIG_NS,
                "ValidatorSuite");
        for (int i = 0; i < validatorSuiteList.getLength(); i++) {
            validatorSuiteElement = (Element) validatorSuiteList.item(i);
            validatorSuiteId = validatorSuiteElement.getAttributeNS(null, "id");
            validatorSuite = new ValidatorSuite(validatorSuiteId);

            if (log.isDebugEnabled()) {
                log.debug("Initializing ValidatorSuite " + validatorSuiteId);
            }

            if (log.isTraceEnabled()) {
                log.trace(XMLHelper.nodeToString(validatorSuiteElement));
            }

            NodeList validatorList = validatorSuiteElement.getElementsByTagNameNS(XMLConstants.XMLTOOLING_CONFIG_NS,
                    "Validator");
            for (int j = 0; j < validatorList.getLength(); j++) {
                validatorElement = (Element) validatorList.item(j);
                validatorClassName = validatorElement.getAttributeNS(null, "className");
                validatorQName = XMLHelper.getAttributeValueAsQName(validatorElement.getAttributeNodeNS(null,
                        "qualifiedName"));

                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Creating instance of validator " + validatorClassName + " for ValidatorSuite "
                                + validatorSuiteId);
                    }
                    validator = (Validator) createClassInstance(validatorClassName);

                    if (log.isDebugEnabled()) {
                        log.debug("Registering validator " + validatorClassName + " to ValidatorSuite "
                                + validatorSuiteId + " under QName " + validatorQName);
                    }
                    validatorSuite.registerValidator(validatorQName, validator);
                } catch (InstantiationException e) {
                    String errorMsg = "Unable to create Validator " + validatorClassName + " for ValidatorSuite "
                            + validatorSuiteId;
                    log.error(errorMsg, e);
                    throw new ConfigurationException(errorMsg, e);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("ValidtorSuite " + validatorSuiteId + " has been initialized");
            }
            validatorSuiteConfigurations.put(validatorSuiteId, validatorSuiteElement);
            validatorSuites.put(validatorSuiteId, validatorSuite);
        }
    }

    /**
     * Constructs an instance of the given class.
     * 
     * @param className the class's name
     * 
     * @return an instance of the given class
     * 
     * @throws InstantiationException thrown if the class can not be instaniated
     */
    private static Object createClassInstance(String className) throws InstantiationException {
        try {
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (ClassNotFoundException e) {
            log.error("Can not create instance of " + className + ", class not found on classpath", e);
            throw new InstantiationException("Can not create instance of " + className
                    + ", class not found on classpath");
        } catch (NoSuchMethodException e) {
            log.error(className + " does not have a default constructor, can not create instance", e);
            throw new InstantiationException(className
                    + " does not have a default constructor, can not create instance");
        } catch (IllegalArgumentException e) {
            // No arguments in default constructor
        } catch (IllegalAccessException e) {
            log.error("Unable to execute constructor for " + className + " do to security restriction", e);
            throw new InstantiationException("Unable to execute constructor for " + className
                    + " do to security restriction");
        } catch (InvocationTargetException e) {
            log.error("Constructor for " + className + " through the following error when executed", e);
            throw new InstantiationException("Constructor for " + className
                    + " through the following error when executed" + e);
        }

        return null;
    }

    /**
     * Schema validates the given configuration.
     * 
     * @param configuration the configuration to validate
     * 
     * @throws ConfigurationException thrown if the configuration is not schema-valid
     */
    private static void validateConfiguration(Document configuration) throws ConfigurationException {
        try {
            if (configurationSchema == null) {
                SchemaFactory factory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Source schemaSource = new StreamSource(Configuration.class
                        .getResourceAsStream("/schema/xmltooling-config.xsd"));
                configurationSchema = factory.newSchema(schemaSource);
            }

            javax.xml.validation.Validator schemaValidator = configurationSchema.newValidator();
            schemaValidator.validate(new DOMSource(configuration));
        } catch (IOException e) {
            // Should never get here as the DOM is already in memory
            String errorMsg = "Unable to read configuration file DOM";
            log.error(errorMsg, e);
            throw new ConfigurationException(errorMsg, e);
        } catch (SAXException e) {
            String errorMsg = "Configuration file does not validate against schema";
            log.error(errorMsg, e);
            throw new ConfigurationException(errorMsg, e);
        }
    }
}