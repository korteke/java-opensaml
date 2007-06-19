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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Base test case class for tests that operate on XMLObjects.
 */
public class XMLObjectBaseTestCase extends XMLTestCase {

    /** Logger */
    private static Logger log = Logger.getLogger(XMLObjectBaseTestCase.class);

    /** Parser pool */
    protected static BasicParserPool parserPool;

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
        Transformer tr = null;
        try {
            tr = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        }
        tr.setOutputProperty(OutputKeys.METHOD,"xml");
        tr.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

        try {
            if (filename == null || filename.equals("")) {
                tr.transform( new DOMSource(node),new StreamResult(System.out));
            } else {
                tr.transform( new DOMSource(node),new StreamResult(new FileOutputStream(filename)));
            }
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
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
    
    static {
        try {
            XMLConfigurator configurator = new XMLConfigurator();;

            parserPool = new BasicParserPool();
            parserPool.setNamespaceAware(true);

            Class clazz = XMLObjectBaseTestCase.class;

            Document generalConfig = parserPool.parse(clazz.getResourceAsStream("/xmltooling-config.xml"));
            configurator.load(generalConfig);

            Document schemaConfig = parserPool.parse(clazz.getResourceAsStream("/schema-config.xml"));
            configurator.load(schemaConfig);
            
            Document encryptionConfig = parserPool.parse(clazz.getResourceAsStream("/encryption-config.xml"));
            configurator.load(encryptionConfig);

            Document signatureConfig = parserPool.parse(clazz.getResourceAsStream("/signature-config.xml"));
            configurator.load(signatureConfig);

            builderFactory = Configuration.getBuilderFactory();
            marshallerFactory = Configuration.getMarshallerFactory();
            unmarshallerFactory = Configuration.getUnmarshallerFactory();
        } catch (Exception e) {
            System.err.println("Can not initialize XMLObjectBaseTestCase" + e);
        }
    }
}