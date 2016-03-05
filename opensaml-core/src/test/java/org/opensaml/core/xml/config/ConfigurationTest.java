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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.provider.ThreadLocalConfigurationPropertiesHolder;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLConfigurationException;
import org.opensaml.core.xml.config.XMLConfigurator;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;

/**
 * Test case for the library configuration mechanism.
 */
public class ConfigurationTest {

    /** Parser pool used to parse example config files */
    private BasicParserPool parserPool;

    /** SimpleElement QName */
    private QName simpleXMLObjectQName;
    
    /**
     * Tests that a schema invalid configuration file is properly identified as such.
     */
    @Test
    public void testInvalidConfiguration() throws Exception {
        XMLConfigurator configurator = new XMLConfigurator();
        try {
            InputStream sxConfig = XMLObjectProviderRegistrySupport.class
                    .getResourceAsStream("/org/opensaml/core/xml/config/InvalidConfiguration.xml");
            configurator.load(sxConfig);
        } catch (XMLConfigurationException e) {
            return;
        }

        Assert.fail("Invalid configuration file passed schema validation");
    }

    /**
     * Tests loading of multiple configuration files.
     */
    @Test
    public void testObjectProviderConfiguration() throws Exception {
        XMLConfigurator configurator = new XMLConfigurator();

        // Test loading the SimpleXMLObject configuration where builder contains additional children
        InputStream sxConfig = ConfigurationTest.class
                .getResourceAsStream("/org/opensaml/core/xml/config/SimpleXMLObjectConfiguration.xml");
        configurator.load(sxConfig);

        XMLObjectBuilder sxBuilder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(simpleXMLObjectQName);
        Assert.assertNotNull(sxBuilder, "SimpleXMLObject did not have a registered builder");

        Marshaller sxMarshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(simpleXMLObjectQName);
        Assert.assertNotNull(sxMarshaller, "SimpleXMLObject did not have a registered marshaller");

        Unmarshaller sxUnmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(simpleXMLObjectQName);
        Assert.assertNotNull(sxUnmarshaller, "SimpleXMLObject did not have a registered unmarshaller");

        // Test loading a configuration with bogus classes
        InputStream nonConfig = XMLObjectProviderRegistrySupport.class
                .getResourceAsStream("/org/opensaml/core/xml/config/NonexistantClassConfiguration.xml");
        try {
            configurator.load(nonConfig);
            Assert.fail("Configuration loaded file that contained invalid classes");
        } catch (XMLConfigurationException e) {
            // this is supposed to fail
        }
    }

    /**
     * Tests that global ID attribute registration/deregistration is functioning properly.
     */
    @Test
    public void testIDAttributeRegistration() {
        QName attribQname = new QName("http://example.org", "someIDAttribName", "test");

        Assert.assertFalse(XMLObjectProviderRegistrySupport.isIDAttribute(attribQname), "Non-registered ID attribute check returned true");

        XMLObjectProviderRegistrySupport.registerIDAttribute(attribQname);
        Assert.assertTrue(XMLObjectProviderRegistrySupport.isIDAttribute(attribQname), "Registered ID attribute check returned false");

        XMLObjectProviderRegistrySupport.deregisterIDAttribute(attribQname);
        Assert.assertFalse(XMLObjectProviderRegistrySupport.isIDAttribute(attribQname), "Non-registered ID attribute check returned true");

        // Check xml:id, which is hardcoded in the Configuration static initializer
        QName xmlIDQName = new QName(XMLConstants.XML_NS_URI, "id");
        Assert.assertTrue(XMLObjectProviderRegistrySupport.isIDAttribute(xmlIDQName), "Registered ID attribute check returned false");
    }

    /**
     * Tests that global ID attribute registration/deregistration via the XMLTooling config file is functioning
     * properly.
     * 
     * @throws XMLParserException thrown if the XML config file can not be read
     * @throws XMLConfigurationException thrown if the ID attributes can not be registered
     */
    @Test
    public void testIDAttributeConfiguration() throws XMLParserException, XMLConfigurationException {
        XMLConfigurator configurator = new XMLConfigurator();
        
        QName fooQName = new QName("http://www.example.org/testObjects", "foo", "test");
        QName barQName = new QName("http://www.example.org/testObjects", "bar", "test");
        QName bazQName = new QName("http://www.example.org/testObjects", "baz", "test");

        InputStream idAttributeConfig = XMLObjectProviderRegistrySupport.class
                .getResourceAsStream("/org/opensaml/core/xml/config/IDAttributeConfiguration.xml");
        configurator.load(idAttributeConfig);

        Assert.assertTrue(XMLObjectProviderRegistrySupport.isIDAttribute(fooQName), "Registered ID attribute check returned false");
        Assert.assertTrue(XMLObjectProviderRegistrySupport.isIDAttribute(barQName), "Registered ID attribute check returned false");
        Assert.assertTrue(XMLObjectProviderRegistrySupport.isIDAttribute(bazQName), "Registered ID attribute check returned false");

        XMLObjectProviderRegistrySupport.deregisterIDAttribute(fooQName);
        XMLObjectProviderRegistrySupport.deregisterIDAttribute(barQName);
        XMLObjectProviderRegistrySupport.deregisterIDAttribute(bazQName);
    }
    
    @BeforeClass
    protected void initClass() throws ComponentInitializationException {
        parserPool = new BasicParserPool();
        parserPool.setNamespaceAware(true);
        parserPool.initialize();
        
        simpleXMLObjectQName = new QName("http://www.example.org/testObjects", "SimpleElement");
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        Properties props = new Properties();
        props.setProperty(ConfigurationService.PROPERTY_PARTITION_NAME, this.getClass().getName());
        ThreadLocalConfigurationPropertiesHolder.setProperties(props);
        
        ConfigurationService.register(XMLObjectProviderRegistry.class, new XMLObjectProviderRegistry());
    }

    @AfterMethod
    protected void tearDown() throws Exception {
        ConfigurationService.deregister(XMLObjectProviderRegistry.class);
        ThreadLocalConfigurationPropertiesHolder.clear();
    }
    
}