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

package org.opensaml.soap;

import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base test case for SOAP XMLObject unit tests.
 */
public abstract class BaseTestCase extends XMLTestCase {

    /** Logger */
    private static Logger log = Logger.getLogger(BaseTestCase.class);

    /** Parser pool */
    protected static ParserPool parserPool;

    /** XMLObject builder factory */
    protected static XMLObjectBuilderFactory builderFactory;

    /** XMLObject marshaller factory */
    protected static MarshallerFactory marshallerFactory;

    /** XMLObject unmarshaller factory */
    protected static UnmarshallerFactory unmarshallerFactory;
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        XMLUnit.setIgnoreWhitespace(true);
    }

    /**
     * Asserts a given XMLObject is equal to an expected DOM. The XMLObject is marshalled and the resulting DOM object
     * is compared against the expected DOM object for equality.
     * 
     * @param expectedDOM the expected DOM
     * @param xmlObject the XMLObject to be marshalled and compared against the expected DOM
     */
    public void assertEquals(Document expectedDOM, XMLObject xmlObject) {
        assertEquals("Marshalled DOM was not the same as the expected DOM", expectedDOM, xmlObject);
    }

    /**
     * Asserts a given XMLObject is equal to an expected DOM. The XMLObject is marshalled and the resulting DOM object
     * is compared against the expected DOM object for equality.
     * 
     * @param failMessage the message to display if the DOMs are not equal
     * @param expectedDOM the expected DOM
     * @param xmlObject the XMLObject to be marshalled and compared against the expected DOM
     */
    public void assertEquals(String failMessage, Document expectedDOM, XMLObject xmlObject) {
        Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
        if (marshaller == null) {
            fail("Unable to locate marshaller for " + xmlObject.getElementQName()
                    + " can not perform equality check assertion");
        }

        try {
            Element generatedDOM = marshaller.marshall(xmlObject, parserPool.newDocument());
            if (log.isDebugEnabled()) {
                log.debug("Marshalled DOM was " + XMLHelper.nodeToString(generatedDOM));
            }
            assertXMLEqual(failMessage, expectedDOM, generatedDOM.getOwnerDocument());
        } catch (Exception e) {
            fail("Marshalling failed with the following error: " + e);
        }
    }

    /**
     * Builds the requested XMLObject.
     * 
     * @param objectQName name of the XMLObject
     * 
     * @return the build XMLObject
     */
    public XMLObject buildXMLObject(QName objectQName) {
        XMLObjectBuilder builder = Configuration.getBuilderFactory().getBuilder(objectQName);
        if (builder == null) {
            fail("Unable to retrieve builder for object QName " + objectQName);
        }
        return builder.buildObject(objectQName.getNamespaceURI(), objectQName.getLocalPart(), objectQName.getPrefix());
    }

    /*
     * Initialize the library
     */
    static {
        HashMap<String, Boolean> features = new HashMap<String, Boolean>();
        features.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
        features.put("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.FALSE);
        parserPool = new ParserPool(true, null, features);

        Class clazz = BaseTestCase.class;
        try {

            // SOAP 1.1 Configuration
            Document soap11Config = parserPool.parse(clazz.getResourceAsStream("/soap11-config.xml"));
            Configuration.load(soap11Config);

            builderFactory = Configuration.getBuilderFactory();
            marshallerFactory = Configuration.getMarshallerFactory();
            unmarshallerFactory = Configuration.getUnmarshallerFactory();
        } catch (Exception e) {
            System.err.println("Can not initialize test suite, " + e.getMessage());
        }
    }
}