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
import org.opensaml.xml.io.Unmarshaller;
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

public class XMLConfigurator {

    /** Logger */
    private static final Logger LOG = Logger.getLogger(XMLConfigurator.class);

    /** Schema used to validate configruation files */
    private Schema configurationSchema;

    /**
     * Constructor
     * 
     * @throws ConfigurationException thrown if the validation schema for configuration files can not be created
     */
    public XMLConfigurator() throws ConfigurationException {
        SchemaFactory factory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaSource = new StreamSource(XMLConfigurator.class
                .getResourceAsStream(XMLConstants.XMLTOOLING_SCHEMA_LOCATION));
        try {
            configurationSchema = factory.newSchema(schemaSource);
        } catch (SAXException e) {
            throw new ConfigurationException("Unable to read XMLTooling configuration schema", e);
        }
    }

    /**
     * Loads the configurtion file(s) from the given file. If the file is a directory each file within the directory is
     * loaded.
     * 
     * @param configurationFile the configuration file(s) to be loaded
     * 
     * @throws ConfigurationException thrown if the configuration file(s) can not be be read or invalid
     */
    public void load(File configurationFile) throws ConfigurationException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document configuration;

            if (configurationFile.isDirectory()) {
                File[] configurations = configurationFile.listFiles();
                for (int i = 0; i < configurations.length; i++) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Parsing configuration file " + configurations[i].getAbsolutePath());
                    }
                    configuration = documentBuilder.parse(new FileInputStream(configurations[i]));
                    load(configuration);
                }
            } else {
                // Given file is not a directory so try to load it directly
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Parsing configuration file " + configurationFile.getAbsolutePath());
                }
                configuration = documentBuilder.parse(new FileInputStream(configurationFile));
                load(configuration);
            }
        } catch (Exception e) {
            LOG.fatal("Unable to parse configuration file(s) in " + configurationFile.getAbsolutePath(), e);
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
    public void load(Document configuration) throws ConfigurationException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading configuration from XML Document");
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("\n" + XMLHelper.nodeToString(configuration.getDocumentElement()));
        }

        // Schema validation
        if (LOG.isDebugEnabled()) {
            LOG.debug("Schema validating configuration Document");
        }
        validateConfiguration(configuration);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Configuration document validated");
        }

        // Initialize object providers
        NodeList objectProviders = configuration.getDocumentElement().getElementsByTagNameNS(
                XMLConstants.XMLTOOLING_CONFIG_NS, "ObjectProviders");
        if (objectProviders.getLength() > 0) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Preparing to load ObjectProviders");
            }
            initializeObjectProviders((Element) objectProviders.item(0));
            if (LOG.isInfoEnabled()) {
                LOG.info("ObjectProviders load complete");
            }
        }

        // Initialize validator suites
        NodeList validatorSuitesNodes = configuration.getDocumentElement().getElementsByTagNameNS(
                XMLConstants.XMLTOOLING_CONFIG_NS, "ValidatorSuites");
        if (validatorSuitesNodes.getLength() > 0) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Preparing to load ValidatorSuites");
            }
            initializeValidatorSuites((Element) validatorSuitesNodes.item(0));
            if (LOG.isInfoEnabled()) {
                LOG.info("ValidatorSuites load complete");
            }
        }
    }

    /**
     * Intializes the object providers defined in the configuration file.
     * 
     * @param objectProviders the configuration for the various object providers
     * 
     * @throws ConfigurationException thrown if the configuration elements are invalid
     */
    protected void initializeObjectProviders(Element objectProviders) throws ConfigurationException {
        // Process ObjectProvider child elements
        Element objectProvider;
        Attr qNameAttrib;
        QName objectProviderName;
        Element configuration;
        XMLObjectBuilder builder;
        Marshaller marshaller;
        Unmarshaller unmarshaller;

        NodeList providerList = objectProviders.getElementsByTagNameNS(XMLConstants.XMLTOOLING_CONFIG_NS,
                "ObjectProvider");
        for (int i = 0; i < providerList.getLength(); i++) {
            objectProvider = (Element) providerList.item(i);

            // Get the element name of type this object provider is for
            qNameAttrib = objectProvider.getAttributeNodeNS(null, "qualifiedName");
            objectProviderName = XMLHelper.getAttributeValueAsQName(qNameAttrib);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Initializing object provider " + objectProviderName);
            }

            try {
                configuration = (Element) objectProvider.getElementsByTagNameNS(XMLConstants.XMLTOOLING_CONFIG_NS,
                        "BuilderClass").item(0);
                builder = (XMLObjectBuilder) createClassInstance(configuration);

                configuration = (Element) objectProvider.getElementsByTagNameNS(XMLConstants.XMLTOOLING_CONFIG_NS,
                        "MarshallingClass").item(0);
                marshaller = (Marshaller) createClassInstance(configuration);

                configuration = (Element) objectProvider.getElementsByTagNameNS(XMLConstants.XMLTOOLING_CONFIG_NS,
                        "UnmarshallingClass").item(0);
                unmarshaller = (Unmarshaller) createClassInstance(configuration);

                Configuration.registerObjectProvider(objectProviderName, builder, marshaller, unmarshaller,
                        objectProvider);

                if (LOG.isDebugEnabled()) {
                    LOG.debug(objectProviderName + " intialized and configuration cached");
                }
            } catch (ConfigurationException e) {
                LOG.fatal("Error initializing object provier " + objectProvider, e);
                // clean up any parts of the object provider that might have been registered before the failure
                Configuration.deregisterObjectProvider(objectProviderName);
                throw e;
            }
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
    protected void initializeValidatorSuites(Element validatorSuitesElement) throws ConfigurationException {
        ValidatorSuite validatorSuite;
        Validator validator;
        Element validatorSuiteElement;
        String validatorSuiteId;
        Element validatorElement;
        QName validatorQName;

        NodeList validatorSuiteList = validatorSuitesElement.getElementsByTagNameNS(XMLConstants.XMLTOOLING_CONFIG_NS,
                "ValidatorSuite");
        for (int i = 0; i < validatorSuiteList.getLength(); i++) {
            validatorSuiteElement = (Element) validatorSuiteList.item(i);
            validatorSuiteId = validatorSuiteElement.getAttributeNS(null, "id");
            validatorSuite = new ValidatorSuite(validatorSuiteId);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Initializing ValidatorSuite " + validatorSuiteId);
            }

            if (LOG.isTraceEnabled()) {
                LOG.trace(XMLHelper.nodeToString(validatorSuiteElement));
            }

            NodeList validatorList = validatorSuiteElement.getElementsByTagNameNS(XMLConstants.XMLTOOLING_CONFIG_NS,
                    "Validator");
            for (int j = 0; j < validatorList.getLength(); j++) {
                validatorElement = (Element) validatorList.item(j);
                validatorQName = XMLHelper.getAttributeValueAsQName(validatorElement.getAttributeNodeNS(null,
                        "qualifiedName"));

                validator = (Validator) createClassInstance(validatorElement);
                validatorSuite.registerValidator(validatorQName, validator);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("ValidtorSuite " + validatorSuiteId + " has been initialized");
            }
            Configuration.registerValidatorSuite(validatorSuiteId, validatorSuite, validatorSuiteElement);
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
    protected Object createClassInstance(Element configuration) throws ConfigurationException {
        String className = configuration.getAttributeNS(null, "className");
        className = DatatypeHelper.safeTrimOrNullString(className);

        if (className == null) {
            return null;
        }

        try {
            if(LOG.isDebugEnabled()){
                LOG.debug("Creating instance of " + className);
            }
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            LOG.error("Can not create instance of " + className, e);
            throw new ConfigurationException("Can not create instance of " + className, e);
        }
    }

    /**
     * Schema validates the given configuration.
     * 
     * @param configuration the configuration to validate
     * 
     * @throws ConfigurationException thrown if the configuration is not schema-valid
     */
    protected void validateConfiguration(Document configuration) throws ConfigurationException {
        try {
            javax.xml.validation.Validator schemaValidator = configurationSchema.newValidator();
            schemaValidator.validate(new DOMSource(configuration));
        } catch (IOException e) {
            // Should never get here as the DOM is already in memory
            String errorMsg = "Unable to read configuration file DOM";
            LOG.error(errorMsg, e);
            throw new ConfigurationException(errorMsg, e);
        } catch (SAXException e) {
            String errorMsg = "Configuration file does not validate against schema";
            LOG.error(errorMsg, e);
            throw new ConfigurationException(errorMsg, e);
        }
    }
}