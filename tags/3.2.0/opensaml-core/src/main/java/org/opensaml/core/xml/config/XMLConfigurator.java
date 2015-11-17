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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
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
    @Nonnull @NotEmpty public static final String XMLTOOLING_CONFIG_NS = "http://www.opensaml.org/xmltooling-config";

    /** Configuration namespace prefix. */
    @Nonnull @NotEmpty public static final String XMLTOOLING_CONFIG_PREFIX = "xt";

    /** Name of the object provider used for objects that don't have a registered object provider. */
    @Nonnull @NotEmpty public static final String XMLTOOLING_DEFAULT_OBJECT_PROVIDER = "DEFAULT";

    /** Location, on the classpath, of the XMLTooling configuration schema. */
    @Nonnull @NotEmpty public static final String XMLTOOLING_SCHEMA_LOCATION = "/schema/xmltooling-config.xsd";

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(XMLConfigurator.class);

    /** Pool of parsers used to read and validate configurations. */
    private BasicParserPool parserPool;

    /** Schema used to validate configruation files. */
    private Schema configurationSchema;

    /** The provider registry instance to use. */
    @Nonnull private final XMLObjectProviderRegistry registry;

    /**
     * Constructor.
     * 
     * @throws XMLConfigurationException thrown if the validation schema for configuration files cannot be created
     */
    public XMLConfigurator() throws XMLConfigurationException {
        parserPool = new BasicParserPool();
        final SchemaFactory factory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Source schemaSource =
                new StreamSource(XMLConfigurator.class.getResourceAsStream(XMLTOOLING_SCHEMA_LOCATION));
        try {
            configurationSchema = factory.newSchema(schemaSource);

            parserPool.setIgnoreComments(true);
            parserPool.setIgnoreElementContentWhitespace(true);
            parserPool.setSchema(configurationSchema);
            parserPool.initialize();
        } catch (final SAXException e) {
            throw new XMLConfigurationException("Unable to read XMLTooling configuration schema", e);
        } catch (final ComponentInitializationException e) {
            throw new XMLConfigurationException("Unable to initialize parser pool", e);
        }

        synchronized (ConfigurationService.class) {
            XMLObjectProviderRegistry reg = ConfigurationService.get(XMLObjectProviderRegistry.class);
            if (reg == null) {
                log.debug("XMLObjectProviderRegistry did not exist in ConfigurationService, will be created");
                reg = new XMLObjectProviderRegistry();
                ConfigurationService.register(XMLObjectProviderRegistry.class, reg);
            }
            registry = reg;
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
    public void load(@Nullable final File configurationFile) throws XMLConfigurationException {
        if (configurationFile == null || !configurationFile.canRead()) {
            log.error("Unable to read configuration file {}", configurationFile);
            return;
        }

        try {
            if (configurationFile.isDirectory()) {
                final File[] configurations = configurationFile.listFiles();
                for (int i = 0; i < configurations.length; i++) {
                    log.debug("Parsing configuration file {}", configurations[i].getAbsolutePath());
                    load(new FileInputStream(configurations[i]));
                }
            } else {
                // Given file is not a directory so try to load it directly
                log.debug("Parsing configuration file {}", configurationFile.getAbsolutePath());
                load(new FileInputStream(configurationFile));
            }
        } catch (final FileNotFoundException e) {
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
    public void load(@Nonnull final InputStream configurationStream) throws XMLConfigurationException {
        try {
            Document configuration = parserPool.parse(configurationStream);
            load(configuration);
        } catch (final XMLParserException e) {
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
    public void load(@Nonnull final Document configuration) throws XMLConfigurationException {
        final Element root = Constraint.isNotNull(configuration.getDocumentElement(),
                "Document element cannot be null");
        
        log.debug("Loading configuration from XML Document");
        log.trace("{}", SerializeSupport.nodeToString(root));

        // Schema validation
        log.debug("Schema validating configuration Document");
        validateConfiguration(configuration);
        log.debug("Configuration document validated");

        load(root);
    }

    /**
     * Loads a configuration after it's been schema validated.
     * 
     * @param configurationRoot root of the configuration
     * 
     * @throws XMLConfigurationException thrown if there is a problem processing the configuration
     */
    protected void load(@Nonnull final Element configurationRoot) throws XMLConfigurationException {
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

        final NodeList providerList = objectProviders.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "ObjectProvider");
        for (int i = 0; i < providerList.getLength(); i++) {
            final Element objectProvider = (Element) providerList.item(i);

            // Get the element name of type this object provider is for
            final Attr qNameAttrib = objectProvider.getAttributeNodeNS(null, "qualifiedName");
            final QName objectProviderName = AttributeSupport.getAttributeValueAsQName(qNameAttrib);

            log.debug("Initializing object provider {}", objectProviderName);

            try {
                Element configuration =
                        (Element) objectProvider.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "BuilderClass").item(0);
                final XMLObjectBuilder<?> builder = (XMLObjectBuilder<?>) createClassInstance(configuration);

                configuration =
                        (Element) objectProvider.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "MarshallingClass").item(
                                0);
                final Marshaller marshaller = (Marshaller) createClassInstance(configuration);

                configuration =
                        (Element) objectProvider.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "UnmarshallingClass")
                                .item(0);
                final Unmarshaller unmarshaller = (Unmarshaller) createClassInstance(configuration);

                getRegistry().registerObjectProvider(objectProviderName, builder, marshaller, unmarshaller);

                log.debug("{} intialized and configuration cached", objectProviderName);
            } catch (final XMLConfigurationException e) {
                log.error("Error initializing object provier {}", objectProvider, e);
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
     * @throws XMLConfigurationException thrown if there is a problem with a parsing or registering the ID attribute
     */
    protected void initializeIDAttributes(Element idAttributesElement) throws XMLConfigurationException {
        Element idAttributeElement;
        QName attributeQName;

        final NodeList idAttributeList =
                idAttributesElement.getElementsByTagNameNS(XMLTOOLING_CONFIG_NS, "IDAttribute");

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
     * @throws XMLConfigurationException thrown if the class can not be instantiated
     */
    protected Object createClassInstance(Element configuration) throws XMLConfigurationException {
        final String className = StringSupport.trimOrNull(configuration.getAttributeNS(null, "className"));

        if (className == null) {
            return null;
        }

        try {
            log.trace("Creating instance of {}", className);
            // TODO switch to thread context class loader, this seems more correct. Need to test and verify.
            // ClassLoader classLoader = this.getClass().getClassLoader();
            // if (classLoader == null) {
            //    classLoader = ClassLoader.getSystemClassLoader();
            // }
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final Class<?> clazz = classLoader.loadClass(className);
            final Constructor<?> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (final Exception e) {
            final String errorMsg = "Cannot create instance of " + className;
            log.error(errorMsg, e);
            throw new XMLConfigurationException(errorMsg, e);
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
        } catch (final IOException e) {
            // Should never get here as the DOM is already in memory
            String errorMsg = "Unable to read configuration file DOM";
            log.error(errorMsg, e);
            throw new XMLConfigurationException(errorMsg, e);
        } catch (final SAXException e) {
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