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

package org.opensaml.core.xml.schema;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSURI;
import org.opensaml.core.xml.schema.impl.XSURIBuilder;
import org.w3c.dom.Document;

/**
 * Unit test for {@link XSURI}
 */
public class XSURITest extends XMLObjectBaseTestCase {
    
    private String testDocumentLocation;
    private QName expectedXMLObjectQName;
    private String expectedValue;
    
    @BeforeMethod
    protected void setUp() throws Exception{
        testDocumentLocation = "/data/org/opensaml/core/xml/schema/xsURI.xml";
        expectedXMLObjectQName = new QName("urn:oasis:names:tc:SAML:2.0:assertion", "AttributeValue", "saml");
        expectedValue = "urn:test:foo:bar:baz";
    }

    /**
     * Tests Marshalling a URI type.
     * @throws MarshallingException 
     * @throws XMLParserException 
     */
    @Test
    public void testMarshall() throws MarshallingException, XMLParserException{
        XSURIBuilder uriBuilder = (XSURIBuilder) builderFactory.getBuilder(XSURI.TYPE_NAME);
        XSURI xsURI = uriBuilder.buildObject(expectedXMLObjectQName, XSURI.TYPE_NAME);
        xsURI.setValue(expectedValue);
        
        Marshaller marshaller = marshallerFactory.getMarshaller(xsURI);
        marshaller.marshall(xsURI);
        
        Document document = parserPool.parse(XSURITest.class.getResourceAsStream(testDocumentLocation));
        assertXMLEquals("Marshalled XSURI does not match example document", document, xsURI);
    }
    
    /**
     * Tests Marshalling a URI type.
     * 
     * @throws XMLParserException 
     * @throws UnmarshallingException 
     */
    @Test
    public void testUnmarshall() throws XMLParserException, UnmarshallingException{
        Document document = parserPool.parse(XSURITest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        XSURI xsURI = (XSURI) unmarshaller.unmarshall(document.getDocumentElement());
        
        Assert.assertEquals(xsURI.getElementQName(), expectedXMLObjectQName, "Unexpected XSURI QName");
        Assert.assertEquals(xsURI.getSchemaType(), XSURI.TYPE_NAME, "Unexpected XSURI schema type");
        Assert.assertEquals(expectedValue, xsURI.getValue(), "Unexpected value of XSURI");
    }
}