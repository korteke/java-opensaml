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

package org.opensaml.core.xml.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.xml.AttributeSupport;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.ElementSupport;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads in an XML configuration and configures the XMLTooling library accordingly.
 */
public class XMLConfigurator {

    /** Configuration namespace. */
    public static final String XMLTOOLING_CONFIG_NS = "http://www.opensaml.org/xmltooling-config";

    /** Configuration namespace prefix. */
    public static final String XMLTOOLING_CONFIG_PREFIX = "xt";

    /** Name of the object provider used for objects that don't have a registered object provider. */
    public static final String XMLTOOLING_DEFAULT_OBJECT_PROVIDER = "DEFAULT";

    /** Location, on the classpath, of the XMLTooling configuration schema. */
    public static final String XMLTOOLING_SCHEMA_LOCATION = "/schema/xmltooling-config.xsd";

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(XMLConfigurator.class);

    /** Pool of parsers used to read and validate configurations. */
    private BasicParserPool parserPool;

    /** Schema used to validate configruation files. */
    private Schema configurationSchema;

    /** The provider registry instance to use. */
    private XMLObjectProviderRegistry registry;

    /**
     * Constructor.
     * 
     * @throws XMLConfigurationException thrown if the validation schema for configuration files cannot be created
     */
    public XMLConfigurator() throws XMLConfigurationException {
        parserPool = new BasicParserPool();
        SchemaFactory factory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaSource = new StreamSource(XMLConfigurator.class.getResourceAsStream(XMLTOOLING_SCHEMA_LOCATION));
        try {
            configurationSchema = factory.newSchema(schemaSource);

            parserPool.setIgnoreComments(true);
            parserPool.setIgnoreElementContentWhitespace(true);
            parserPool.setSchema(configurationSchema);
            parserPool.initialize();
        } catch (SAXException e) {
            throw new XMLConfigurationException("Unable to read XMLTooling configuration schema", e);
        } catch (ComponentInitializationException e) {
            throw new XMLConfigurationException("Unable to initialize parser pool", e);
        }

        synchronized (ConfigurationService.class) {
            registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
            if (registry == null) {
                log.debug("XMLObjectProviderRegistry did not exist in ConfigurationService, will be created");
                registry = new XMLObjectProviderRegistry();
                ConfigurationService.register(XMLObjectProviderRegistry.class, registry);
            }
        }
    }

    /**
     * Loads the configuration file(s) from the given file. If the file is a directory each file within the directory is
     * loaded.
     * 
     * @param configurationFile the configuration file(s) to be loaded
     * 
     * @throws XMLConfigurationException thrown if the configuration file(s) cannot be read or invalid
     */
    public void load(File configurationFile) throws XMLConfigurationException {
        if (configurationFile == null || !configurationFile.canRead()) {
            log.error("Unable to read configuration file {}", configurationFile);
        }

        try {
            if (configurationFile.isDirectory()) {
                File[] configurations = configurationFile.listFiles();
                for (int i = 0; i < configurations.length; i++) {
                    log.debug("Parsing configuration file {}", configurations[i].getAbsolutePath());
                    load(new FileInputStream(configurations[i]));
                }
            } else {
                // Given file is not a directory so try to load it directly
                log.debug("Parsing configuration file {}", configurationFile.getAbsolutePath());
                load(new FileInputStream(configurationFile));
            }
        } catch (FileNotFoundException e) {
            // ignore, we already have the files
        }
    }

    /**
     * Loads a configuration file from an input stream.
     * 
     * @param configurationStream configuration stream
     * 
     * @throws XMLConfigurationException thrown if the given configuration is invalid or cannot be read
     */
    public void load(InputStream configurationStream) throws XMLConfigurationException {
        try {
            Document configuration = parserPool.parse(configurationStream);
            load(configuration);
        } catch (XMLParserException e) {
            log.error("Invalid configuration file", e);
            throw new XMLConfigurationException("Unable to create DocumentBuilder", e);
        }

    }

    /**
     * Loads the configuration document.
     * 
     * @param configuration the configurationd document
     * @throws XMLConfigurationException thrown if the configuration file(s) cannot be read or invalid
     */
    public void load(Document configuration) throws XMLConfigurationException {
        log.debug("Loading configuration from XML Document");
        log.trace("{}", SerializeSupport.nodeToString(configuration.getDocumentElement()));

        // Schema validation
        log.debug("Schema validating configuration Document");
        validateConfiguration(configuration);
        log.debug("Configuration document validated");

        load(configuration.getDocumentElement());
    }

    /**
     * Loads a configuration after it's been schema validated.
     * 
     * @param configurationRoot root of the configuration
     * 
     * @throws XMLConfigurationException thrown if there is a problem processing the configuration
     */
    protected void load(Element configurationRoot) throws XMLConfigurationException {
        // Initialize object providers
        NodeList objectProviders = configurationRoot.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "ObjectProviders");
        if (objectProviders.getLength() > 0) {
            log.debug("Preparing to load ObjectProviders");
            initializeObjectProviders((Element) objectProviders.item(0));
            log.debug("ObjectProviders load complete");
        }

        // Initialize ID attributes
        NodeList idAttributesNodes = configurationRoot.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "IDAttributes");
        if (idAttributesNodes.getLength() > 0) {
            log.debug("Preparing to load IDAttributes");
            initializeIDAttributes((Element) idAttributesNodes.item(0));
            log.debug("IDAttributes load complete");
        }
    }

    /**
     * Intializes the object providers defined in the configuration file.
     * 
     * @param objectProviders the configuration for the various object providers
     * 
     * @throws XMLConfigurationException thrown if the configuration elements are invalid
     */
    protected void initializeObjectProviders(Element objectProviders) throws XMLConfigurationException {
        // Process ObjectProvider child elements
        Element objectProvider;
        Attr qNameAttrib;
        QName objectProviderName;
        Element configuration;
        XMLObjectBuilder builder;
        Marshaller marshaller;
        Unmarshaller unmarshaller;

        NodeList providerList = objectProviders.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "ObjectProvider");
        for (int i = 0; i < providerList.getLength(); i++) {
            objectProvider = (Element) providerList.item(i);

            // Get the element name of type this object provider is for
            qNameAttrib = objectProvider.getAttributeNodeNS(null, "qualifiedName");
            objectProviderName = AttributeSupport.getAttributeValueAsQName(qNameAttrib);

            log.debug("Initializing object provider {}", objectProviderName);

            try {
                configuration =
                        (Element) objectProvider.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "BuilderClass").item(0);
                builder = (XMLObjectBuilder) createClassInstance(configuration);

                configuration =
                        (Element) objectProvider.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "MarshallingClass").item(
                                0);
                marshaller = (Marshaller) createClassInstance(configuration);

                configuration =
                        (Element) objectProvider.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "UnmarshallingClass")
                                .item(0);
                unmarshaller = (Unmarshaller) createClassInstance(configuration);

                getRegistry().registerObjectProvider(objectProviderName, builder, marshaller, unmarshaller);

                log.debug("{} intialized and configuration cached", objectProviderName);
            } catch (XMLConfigurationException e) {
                log.error("Error initializing object provier " + objectProvider, e);
                // clean up any parts of the object provider that might have been registered before the failure
                getRegistry().deregisterObjectProvider(objectProviderName);
                throw e;
            }
        }
    }

    /**
     * Registers the global ID attributes specified in the configuration file.
     * 
     * @param idAttributesElement the IDAttributes element from the configuration file
     * 
     * @throws XMLConfigurationException thrown if there is a problem with a parsing or registering the the ID attribute
     */
    protected void initializeIDAttributes(Element idAttributesElement) throws XMLConfigurationException {
        Element idAttributeElement;
        QName attributeQName;

        NodeList idAttributeList = idAttributesElement.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "IDAttribute");

        for (int i = 0; i < idAttributeList.getLength(); i++) {
            idAttributeElement = (Element) idAttributeList.item(i);
            attributeQName = ElementSupport.getElementContentAsQName(idAttributeElement);
            if (attributeQName == null) {
                log.debug("IDAttribute element was empty, no registration performed");
            } else {
                getRegistry().registerIDAttribute(attributeQName);
                log.debug("IDAttribute {} has been registered", attributeQName);
            }
        }
    }

    /**
     * Constructs an instance of the given class.
     * 
     * @param configuration the current configuration element
     * 
     * @return an instance of the given class
     * 
     * @throws XMLConfigurationException thrown if the class can not be instaniated
     */
    protected Object createClassInstance(Element configuration) throws XMLConfigurationException {
        String className = configuration.getAttributeNS(null, "className");
        className = StringSupport.trimOrNull(className);

        if (className == null) {
            return null;
        }

        try {
            log.trace("Creating instance of {}", className);
            // TODO switch to thread context class loader, this seems more correct. Need to test and verify.
            // ClassLoader classLoader = this.getClass().getClassLoader();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class clazz = classLoader.loadClass(className);
            Constructor constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            log.error("Can not create instance of " + className, e);
            throw new XMLConfigurationException("Can not create instance of " + className, e);
        }
    }

    /**
     * Schema validates the given configuration.
     * 
     * @param configuration the configuration to validate
     * 
     * @throws XMLConfigurationException thrown if the configuration is not schema-valid
     */
    protected void validateConfiguration(Document configuration) throws XMLConfigurationException {
        try {
            javax.xml.validation.Validator schemaValidator = configurationSchema.newValidator();
            schemaValidator.validate(new DOMSource(configuration));
        } catch (IOException e) {
            // Should never get here as the DOM is already in memory
            String errorMsg = "Unable to read configuration file DOM";
            log.error(errorMsg, e);
            throw new XMLConfigurationException(errorMsg, e);
        } catch (SAXException e) {
            String errorMsg = "Configuration file does not validate against schema";
            log.error(errorMsg, e);
            throw new XMLConfigurationException(errorMsg, e);
        }
    }

    /**
     * Get the XMLObject provider registry instance to use.
     * 
     * @return the registry instance
     */
    protected XMLObjectProviderRegistry getRegistry() {
        return registry;
    }
}