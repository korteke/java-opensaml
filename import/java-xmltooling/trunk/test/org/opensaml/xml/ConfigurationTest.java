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

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.parse.ParserPool;
import org.w3c.dom.Document;

/**
 * Test case for the library configuration mechanism.
 */
public class ConfigurationTest extends TestCase {

    /** Parser pool used to parse example config files */
    private ParserPool parserPool;
    
    private QName simpleXMLObjectQName;
    
    private QName signatureQName;
    
    /**
     * Constructor
     */
    public ConfigurationTest() {
        HashMap<String, Boolean> features = new HashMap<String, Boolean>();
        features.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
        features.put("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.FALSE);

        parserPool = new ParserPool(true, null, features);
        simpleXMLObjectQName = new QName("http://www.example.org/testObjects", "SimpleElement");
        signatureQName = new QName("http://www.w3.org/2000/09/xmldsig#", "Signature");
    }

    /**
     * Tests loading of multiple configuration files.
     */
    public void testObjectProviderConfiguration() throws Exception {
        
        // Test loading the SimpleXMLObject configuration where builder contains additional children
        InputStream sxConfig = Configuration.class.getResourceAsStream("/data/org/opensaml/xml/SimpleXMLObjectConfiguration.xml");
        Document sxConfigDoc = parserPool.parse(sxConfig);
        Configuration.load(sxConfigDoc);
        
        XMLObjectBuilder sxBuilder = Configuration.getBuilderFactory().getBuilder(simpleXMLObjectQName);
        assertNotNull("SimpleXMLObject did not have a registered builder", sxBuilder);
        
        Marshaller sxMarshaller = Configuration.getMarshallerFactory().getMarshaller(simpleXMLObjectQName);
        assertNotNull("SimpleXMLObject did not have a registered marshaller", sxMarshaller);
        
        Unmarshaller sxUnmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(simpleXMLObjectQName);
        assertNotNull("SimpleXMLObject did not have a registered unmarshaller", sxUnmarshaller);
        
        // Test loading the Signature configuration
        InputStream sigConfig = Configuration.class.getResourceAsStream("/data/org/opensaml/xml/SignatureConfiguration.xml");
        Document sigConfigDoc = parserPool.parse(sigConfig);
        Configuration.load(sigConfigDoc);
        
        XMLObjectBuilder sigBuilder = Configuration.getBuilderFactory().getBuilder(signatureQName);
        assertNotNull("Signature did not have a registered builder", sigBuilder);
        
        Marshaller sigMarshaller = Configuration.getMarshallerFactory().getMarshaller(signatureQName);
        assertNotNull("Signature did not have a registered marshaller", sigMarshaller);
        
        Unmarshaller sigUnmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(signatureQName);
        assertNotNull("Signature did not have a registered unmarshaller", sigUnmarshaller);
        
        // Test loading a configuration with bogus classes
        InputStream nonConfig = Configuration.class.getResourceAsStream("/data/org/opensaml/xml/NonexistantClassConfiguration.xml");
        Document nonConfigDoc = parserPool.parse(nonConfig);
        try {
            Configuration.load(nonConfigDoc);
            fail("Configuration loaded file that contained invalid classes");
        }catch(ConfigurationException e){
            // this is supposed to fail
        }
    }
    
    /**
     * Tests the load of validator suite information.
     */
    public void testValidatorSuiteConfiguration() {
        
    }
}
