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
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationService;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Base test case class for tests that operate on XMLObjects.
 */
public abstract class XMLObjectBaseTestCase extends TestCase {

    /** Logger */
    private final Logger log = LoggerFactory.getLogger(XMLObjectBaseTestCase.class);

    /** Parser pool */
    protected static ParserPool parserPool;

    /** XMLObject builder factory */
    protected static XMLObjectBuilderFactory builderFactory;

    /** XMLObject marshaller factory */
    protected static MarshallerFactory marshallerFactory;

    /** XMLObject unmarshaller factory */
    protected static UnmarshallerFactory unmarshallerFactory;

    /** QName for SimpleXMLObject */
    protected static QName simpleXMLObjectQName;

    /**
     * Constructor
     */
    public XMLObjectBaseTestCase() {
        simpleXMLObjectQName = new QName(SimpleXMLObject.NAMESPACE, SimpleXMLObject.LOCAL_NAME);
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        XMLUnit.setIgnoreWhitespace(true);
        
        InitializationService.initialize();
        
        try {
            XMLObjectProviderRegistry registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
            parserPool = registry.getParserPool();
            builderFactory = registry.getBuilderFactory();
            marshallerFactory = registry.getMarshallerFactory();
            unmarshallerFactory = registry.getUnmarshallerFactory();
        } catch (Exception e) {
            log.error("Can not initialize XMLObjectBaseTestCase", e);
            throw e;
        }
        
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
            XMLAssert.assertXMLIdentical(failMessage, new Diff(expectedDOM, generatedDOM.getOwnerDocument()), true);
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
    
    /**
     * Unmarshalls an element file into its XMLObject.
     * 
     * @return the XMLObject from the file
     */
    protected XMLObject unmarshallElement(String elementFile) {
        try {
            Document doc = parserPool.parse(XMLObjectBaseTestCase.class.getResourceAsStream(elementFile));
            Element samlElement = doc.getDocumentElement();

            Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(samlElement);
            if (unmarshaller == null) {
                fail("Unable to retrieve unmarshaller by DOM Element");
            }

            return unmarshaller.unmarshall(samlElement);
        } catch (XMLParserException e) {
            fail("Unable to parse element file " + elementFile);
        } catch (UnmarshallingException e) {
            fail("Unmarshalling failed when parsing element file " + elementFile + ": " + e);
        }

        return null;
    }
    
    /**
     * For convenience when testing, pretty-print the specified DOM node to a file, or to 
     * the console if filename is null.
     * 
     */
    public void printXML(Node node, String filename) {
        try {
            XMLHelper.writeNode(node, new FileWriter(new File(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * For convenience when testing, pretty-print the specified XMLObject to a file, or to 
     * the console if filename is null.
     * 
     */
    public void printXML(XMLObject xmlObject, String filename) {
        Element elem = null;
        try {
            elem = marshallerFactory.getMarshaller(xmlObject).marshall(xmlObject);
        } catch (MarshallingException e) {
            e.printStackTrace();
        }
        printXML(elem, filename);
    }
}