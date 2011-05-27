/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensaml.ws;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * WSBaseTestCase is the base test case for the WS-* packages.
 * 
 */
public abstract class WSBaseTestCase extends TestCase {

    Logger log= LoggerFactory.getLogger(WSBaseTestCase.class);

    /** Map of builders, marshallers and unmarshallers */
    @SuppressWarnings("unchecked")
    private Map<QName, XMLObjectBuilder> builders_= null;

    private Map<QName, Marshaller> marshallers_= null;

    private Map<QName, Unmarshaller> unmarshallers_= null;

    /** DOM parser pool */
    private BasicParserPool parser_= null;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        XMLUnit.setIgnoreWhitespace(true);

        configureWS();

        registerBuilders();
        registerMarshallers();
        registerUnmarshallers();
        createParser();

    }

    abstract protected void configureWS() throws Exception;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected void registerBuilders() {
        builders_= Configuration.getBuilderFactory().getBuilders();
        Set<QName> keys= builders_.keySet();
        if (log.isTraceEnabled()) {
            for (QName name : keys) {
                log.trace("builder for {}:{}",
                          name.getPrefix(),
                          name.getLocalPart());
            }
        }
    }

    protected void registerMarshallers() {
        marshallers_= Configuration.getMarshallerFactory().getMarshallers();
    }

    protected void registerUnmarshallers() {
        unmarshallers_= Configuration.getUnmarshallerFactory().getUnmarshallers();
    }

    protected void createParser() {
        parser_= new BasicParserPool();
        parser_.setNamespaceAware(true);
    }

    @SuppressWarnings("unchecked")
    protected XMLObjectBuilder getBuilder(QName qname) throws Exception {
        if (!builders_.containsKey(qname))
            throw new Exception("no builder registered for " + qname);
        return (XMLObjectBuilder) builders_.get(qname);
    }

    protected Marshaller getMarshaller(QName qname) throws Exception {
        if (!marshallers_.containsKey(qname)) {
            throw new Exception("no marshaller registered for " + qname);
        }
        return marshallers_.get(qname);
    }

    protected Marshaller getMarshaller(XMLObject obj) throws Exception {
        return getMarshaller(obj.getElementQName());
    }

    protected Unmarshaller getUnmarshaller(QName qname) throws Exception {
        if (!unmarshallers_.containsKey(qname)) {
            throw new Exception("no unmarshaller registered for " + qname);

        }
        return unmarshallers_.get(qname);
    }

    protected Unmarshaller getUnmarshaller(XMLObject obj) throws Exception {
        return getUnmarshaller(obj.getElementQName());
    }

    protected Unmarshaller getUnmarshaller(Element element) throws Exception {
        QName qname= new QName(element.getNamespaceURI(),
                               element.getLocalName(),
                               element.getPrefix());
        return getUnmarshaller(qname);
    }

    @SuppressWarnings("unchecked")
    public <T extends XMLObject> T marshallAndUnmarshall(T object)
            throws Exception {
        QName name= object.getElementQName();

        Marshaller marshaller= getMarshaller(name);
        Unmarshaller unmarshaller= getUnmarshaller(name);

        // Go ahead and release the cached DOM, just for good measure
        object.releaseDOM();
        object.releaseChildrenDOM(true);
        Element element= marshaller.marshall(object);
        assertNotNull(element);

        System.out.println(XMLHelper.nodeToString(element));

        T object2= (T) unmarshaller.unmarshall(element);
        assertNotNull(object2);

        // Have to release the DOM before re-marshalling, otherwise the already cached
        // Element just gets adopted into a new Document, and the test below
        // is comparing the same Element/Document and is therefore always true
        // and therefore an invalid test.
        object2.releaseDOM();
        object2.releaseChildrenDOM(true);
        Element element2= marshaller.marshall(object2);
        assertNotNull(element2);

        System.out.println(XMLHelper.nodeToString(element2));

        // These need to be false, otherwise the test below is invalid
        //System.out.println("Element equals: " + element.isSameNode(element2)); 
        //System.out.println("Document equals: " + element.getOwnerDocument().isSameNode(element2.getOwnerDocument())); 
        
        // compare XML content
        XMLAssert.assertXMLIdentical(new Diff(element.getOwnerDocument(), element2.getOwnerDocument()), true);

        return object2;

    }

    /**
     * Unmarshalls an element file into its XMLObject.
     * 
     * @return the XMLObject from the file
     */
    @SuppressWarnings("unchecked")
    public <T extends XMLObject> T unmarshallXML(String xmlFilename) {
        try {
            Document doc= parseXMLDocument(xmlFilename);
            Element element= doc.getDocumentElement();
            Unmarshaller unmarshaller= getUnmarshaller(element);
            T object= (T) unmarshaller.unmarshall(element);
            assertNotNull(object);
            return object;
        } catch (XMLParserException e) {
            e.printStackTrace();
            fail("Unable to parse XML file: " + xmlFilename + ": " + e);
        } catch (UnmarshallingException e) {
            e.printStackTrace();
            fail("Unmarshalling failed when parsing element file "
                    + xmlFilename + ": " + e);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurs: " + e);
        }

        return null;
    }

    public Document parseXMLDocument(String xmlFilename)
            throws XMLParserException {
        Document doc= null;
        InputStream is= getClass().getResourceAsStream(xmlFilename);
        doc= parser_.parse(is);
        return doc;
    }

    @SuppressWarnings("unchecked")
    public <T extends XMLObject> T buildXMLObject(QName name) throws Exception {
        XMLObjectBuilder<T> builder= (XMLObjectBuilder<T>) getBuilder(name);
        T wsObj= builder.buildObject(name);
        assertNotNull(wsObj);
        return wsObj;
    }

}
