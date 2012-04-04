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

package org.opensaml.core.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.QNameSupport;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Base test case class for tests that operate on XMLObjects.
 */
public abstract class XMLObjectBaseTestCase extends OpenSAMLInitBaseTestCase {

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
    protected static QName simpleXMLObjectQName = new QName(SimpleXMLObject.NAMESPACE, SimpleXMLObject.LOCAL_NAME);

    /** {@inheritDoc} */
    @BeforeClass
	protected void initXMLObjectSupport() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        
        InitializationService.initialize();
        
        try {
            parserPool = XMLObjectProviderRegistrySupport.getParserPool();
            builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
            marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
            unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
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
    protected void assertXMLEquals(Document expectedDOM, XMLObject xmlObject) {
        assertXMLEquals("Marshalled DOM was not the same as the expected DOM", expectedDOM, xmlObject);
    }

    /**
     * Asserts a given XMLObject is equal to an expected DOM. The XMLObject is marshalled and the resulting DOM object
     * is compared against the expected DOM object for equality.
     * 
     * @param failMessage the message to display if the DOMs are not equal
     * @param expectedDOM the expected DOM
     * @param xmlObject the XMLObject to be marshalled and compared against the expected DOM
     */
    protected void assertXMLEquals(String failMessage, Document expectedDOM, XMLObject xmlObject) {
        Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
        if (marshaller == null) {
            Assert.fail("Unable to locate marshaller for " + xmlObject.getElementQName()
                    + " can not perform equality check assertion");
        }

        try {
            Element generatedDOM = marshaller.marshall(xmlObject, parserPool.newDocument());
            if (log.isDebugEnabled()) {
                log.debug("Marshalled DOM was " + SerializeSupport.nodeToString(generatedDOM));
            }
            XMLAssert.assertXMLIdentical(failMessage, new Diff(expectedDOM, generatedDOM.getOwnerDocument()), true);
        } catch (Exception e) {
            Assert.fail("Marshalling failed with the following error: " + e);
        }
    }

    /**
     * Builds the requested XMLObject.
     * 
     * @param objectQName name of the XMLObject
     * 
     * @return the build XMLObject
     */
    protected <T extends XMLObject> T buildXMLObject(QName name) {
        XMLObjectBuilder<T> builder = getBuilder(name);
        if (builder == null) {
            Assert.fail("no builder registered for: " + name);
        }
        T wsObj = builder.buildObject(name);
        Assert.assertNotNull(wsObj);
        return wsObj;
    }
    
    /**
     * Unmarshalls an element file into its XMLObject.
     * 
     * @return the XMLObject from the file
     */
    protected <T extends XMLObject> T  unmarshallElement(String elementFile) {
        try {
            Document doc = parseXMLDocument(elementFile);
            Element element = doc.getDocumentElement();
            Unmarshaller unmarshaller = getUnmarshaller(element);
            T object = (T) unmarshaller.unmarshall(element);
            Assert.assertNotNull(object);
            return object;
        } catch (XMLParserException e) {
            Assert.fail("Unable to parse element file " + elementFile);
        } catch (UnmarshallingException e) {
            Assert.fail("Unmarshalling failed when parsing element file " + elementFile + ": " + e);
        }

        return null;
    }
    
    /**
     * For convenience when testing, pretty-print the specified DOM node to a file, or to 
     * the console if filename is null.
     * 
     */
    protected void printXML(Node node, String filename) {
        try {
            SerializeSupport.writeNode(node, new FileOutputStream(new File(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * For convenience when testing, pretty-print the specified XMLObject to a file, or to 
     * the console if filename is null.
     * 
     */
    protected void printXML(XMLObject xmlObject, String filename) {
        Element elem = null;
        try {
            elem = marshallerFactory.getMarshaller(xmlObject).marshall(xmlObject);
        } catch (MarshallingException e) {
            e.printStackTrace();
        }
        printXML(elem, filename);
    }

    /**
     * Lookup the XMLObjectBuilder for a QName.
     * @param qname the QName for which to find the builder
     * @return the XMLObjectBuilder
     */
    protected XMLObjectBuilder getBuilder(QName qname) {
        XMLObjectBuilder builder = builderFactory.getBuilder(qname);
        if (builder == null) {
            Assert.fail("no builder registered for " + qname);
        }
        return builder;
    }

    /**
     * Lookup the marshaller for a QName
     * 
     * @param qname the QName for which to find the marshaller
     * @return the marshaller
     */
    protected Marshaller getMarshaller(QName qname) {
        Marshaller marshaller = marshallerFactory.getMarshaller(qname);
        if (marshaller == null) {
            Assert.fail("no marshaller registered for " + qname);
        }
        return marshaller;
    }

    /**
     * Lookup the marshaller for an XMLObject.
     * 
     * @param xmlObject the XMLObject for which to find the marshaller
     * @return the marshaller
     */
    protected Marshaller getMarshaller(XMLObject xmlObject) {
        Marshaller marshaller = marshallerFactory.getMarshaller(xmlObject);
        if (marshaller == null) {
            Assert.fail("no marshaller registered for " + xmlObject.getClass().getName());
        }
        return marshaller;
    }

    /**
     * Lookup the unmarshaller for a QName.
     * 
     * @param qname the QName for which to find the unmarshaller
     * @return the unmarshaller
     */
    protected Unmarshaller getUnmarshaller(QName qname) {
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(qname);
        if (unmarshaller == null) {
            Assert.fail("no unmarshaller registered for " + qname);
        }
        return unmarshaller;
    }

    /**
     * Lookup the unmarshaller for an XMLObject.
     * 
     * @param xmlObject the XMLObject for which to find the unmarshaller
     * @return the unmarshaller
     */
    protected Unmarshaller getUnmarshaller(XMLObject xmlObject) {
        return getUnmarshaller(xmlObject.getElementQName());
    }

    /**
     * Lookup the unmarshaller for a DOM Element.
     * 
     * @param element the Element for which to find the unmarshaller
     * @return the unmarshaller
     */
    protected Unmarshaller getUnmarshaller(Element element) {
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        if (unmarshaller == null) {
            Assert.fail("no unmarshaller registered for " + QNameSupport.getNodeQName(element));
        }
        return unmarshaller;
    }

    /**
     * Parse an XML file as a classpath resource.
     * 
     * @param xmlFilename the file to parse 
     * @return the parsed Document
     * @throws XMLParserException if parsing did not succeed
     */
    protected Document parseXMLDocument(String xmlFilename) throws XMLParserException {
        InputStream is = getClass().getResourceAsStream(xmlFilename);
        Document doc = parserPool.parse(is);
        return doc;
    }
    
}