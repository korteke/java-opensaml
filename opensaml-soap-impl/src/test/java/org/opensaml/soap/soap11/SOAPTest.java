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

package org.opensaml.soap.soap11;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.soap.soap11.impl.DetailBuilder;
import org.opensaml.soap.soap11.impl.FaultActorBuilder;
import org.opensaml.soap.soap11.impl.FaultCodeBuilder;
import org.opensaml.soap.soap11.impl.FaultStringBuilder;
import org.opensaml.soap.util.SOAPConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * Tests marshalling and unmarshalling SOAP messages.
 */
public class SOAPTest extends XMLObjectBaseTestCase {
    
    /** Path, on classpath, to SOAP message test document. */
    private String soapMessage;
    
    /** Path, on classpath, to SOAP fault test document. */
    private String soapFault;
    
    /** Path, on classpath, to SOAP fault test document. */
    private String soapFaultMarshall;
    
    private QName expectedFaultCode;
    
    private String expectedFaultString;
    
    private String expectedFaultActor;

    @BeforeMethod
    protected void setUp() throws Exception {
        soapMessage = "/data/org/opensaml/soap/soap11/SOAP.xml";
        soapFault = "/data/org/opensaml/soap/soap11/SOAPFault.xml";
        soapFaultMarshall = "/data/org/opensaml/soap/soap11/SOAPFaultMarshall.xml";
        
        expectedFaultCode = new QName(SOAPConstants.SOAP11_NS, "Server", SOAPConstants.SOAP11_PREFIX);
        expectedFaultString = "Server Error";
        expectedFaultActor = "http://ws.example.org/someActor";
    }
    
    /**
     * Test unmarshalling a SOAP message, dropping its DOM representation and then remarshalling it.
     * 
     * @throws XMLParserException thrown if the XML document can not be located or parsed into a DOM 
     * @throws UnmarshallingException thrown if the DOM can not be unmarshalled
     */
    @Test
    public void testSOAPMessage() throws XMLParserException, UnmarshallingException{
        Document soapDoc = parserPool.parse(SOAPTest.class.getResourceAsStream(soapMessage));
        Element envelopeElem = soapDoc.getDocumentElement();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(envelopeElem);
        
        Envelope envelope = (Envelope) unmarshaller.unmarshall(envelopeElem);
        
        // Check to make sure everything unmarshalled okay
        QName encodingStyleName = new QName("http://schemas.xmlsoap.org/soap/envelope/", "encodingStyle");
        String encodingStyleValue = envelope.getUnknownAttributes().get(encodingStyleName);
        Assert.assertNotNull(encodingStyleValue, "Encoding style was null");
        Assert.assertEquals(encodingStyleValue, 
                "http://schemas.xmlsoap.org/soap/encoding/", "Encoding style had unexpected value");
        
        Header header = envelope.getHeader();
        Assert.assertNotNull(header, "Header was null");
        Assert.assertEquals(header.getUnknownXMLObjects().size(), 1, "Unexpected number of Header children");
        
        Body body = envelope.getBody();
        Assert.assertNotNull(body, "Body was null");
        Assert.assertEquals(body.getUnknownXMLObjects().size(), 1, "Unexpected number of Body children");
        
        // Drop the DOM and remarshall, hopefully we get the same document back
        envelope.releaseDOM();
        envelope.releaseChildrenDOM(true);
        assertXMLEquals("Marshalled DOM was not the same as control DOM", soapDoc, envelope);
    }
    
    /**
     * Test unmarshalling a SOAP fault, dropping its DOM representation and then remarshalling it.
     * @throws XMLParserException thrown if the XML document can not be located or parsed into a DOM 
     * @throws UnmarshallingException thrown if the DOM can not be unmarshalled
     */
    @Test
    public void testSOAPFault() throws XMLParserException, UnmarshallingException{
        Document soapFaultDoc = parserPool.parse(SOAPTest.class.getResourceAsStream(soapFault));
        Element envelopeElem = soapFaultDoc.getDocumentElement();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(envelopeElem);
        
        Envelope envelope = (Envelope) unmarshaller.unmarshall(envelopeElem);
        
        // Check to make sure everything unmarshalled okay
        Header header = envelope.getHeader();
        Assert.assertNull(header, "Header was not null");
        
        Body body = envelope.getBody();
        Assert.assertNotNull(body, "Body was null");
        Assert.assertEquals(body.getUnknownXMLObjects().size(), 1, "Unexpected number of Body children");
        
        Fault fault = (Fault) body.getUnknownXMLObjects().get(0);
        Assert.assertNotNull(fault, "Fault was null");
        
        FaultActor actor = fault.getActor();
        Assert.assertNotNull(actor, "FaultActor was null");
        Assert.assertEquals(actor.getValue(), expectedFaultActor, "FaultActor had unexpected value");
        
        FaultCode code = fault.getCode();
        Assert.assertNotNull(code, "FaultCode was null");
        Assert.assertEquals(code.getValue(), expectedFaultCode, "FaultCode had unexpected value");
        
        FaultString message = fault.getMessage();
        Assert.assertNotNull(message, "FaultString was null");
        Assert.assertEquals(message.getValue(), expectedFaultString, "FaultString had unexpected value");
        
        Detail detail = fault.getDetail();
        Assert.assertNotNull(detail, "Detail was null");
        Assert.assertEquals(detail.getUnknownXMLObjects().size(), 1, "Unexpected number of Body children");
        
        // Drop the DOM and remarshall, hopefully we get the same document back
        envelope.releaseDOM();
        envelope.releaseChildrenDOM(true);
        assertXMLEquals("Marshalled DOM was not the same as control DOM", soapFaultDoc, envelope);
    }
    
    /**
     * Test constructing and marshalling a SOAP fault message.
     * 
     * @throws MarshallingException  if the DOM can not b marshalled
     * @throws XMLParserException 
     */
    @Test
    public void testSOAPFaultConstructAndMarshall() throws MarshallingException, XMLParserException {
        Document soapDoc = parserPool.parse(SOAPTest.class.getResourceAsStream(soapFaultMarshall));
        
        Envelope envelope = (Envelope) buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        
        Body body = (Body) buildXMLObject(Body.DEFAULT_ELEMENT_NAME);
        envelope.setBody(body);
        
        Fault fault = (Fault) buildXMLObject(Fault.DEFAULT_ELEMENT_NAME);
        body.getUnknownXMLObjects().add(fault);
        
        FaultCode faultCode = (FaultCode) buildXMLObject(FaultCode.DEFAULT_ELEMENT_NAME);
        faultCode.setValue(expectedFaultCode);
        fault.setCode(faultCode);
        
        FaultString faultString = (FaultString) buildXMLObject(FaultString.DEFAULT_ELEMENT_NAME);
        faultString.setValue(expectedFaultString);
        fault.setMessage(faultString);
        
        FaultActor faultActor = (FaultActor) buildXMLObject(FaultActor.DEFAULT_ELEMENT_NAME);
        faultActor.setValue(expectedFaultActor);
        fault.setActor(faultActor);
        
        Detail detail = (Detail) buildXMLObject(Detail.DEFAULT_ELEMENT_NAME);
        fault.setDetail(detail);
        
        marshallerFactory.getMarshaller(envelope).marshall(envelope);
        assertXMLEquals("Marshalled DOM was not the same as control DOM", soapDoc, envelope);
        
    }
    
    /**
     *  Test that the no-arg SOAP fault-related builders are operating correcting, i.e. not namespace-qualified.
     */
    @Test
    public void testSOAPFaultBuilders() {
        
       DetailBuilder detailBuilder = (DetailBuilder) builderFactory.getBuilder(Detail.DEFAULT_ELEMENT_NAME); 
       Detail detail = detailBuilder.buildObject();
       Assert.assertTrue(Strings.isNullOrEmpty(detail.getElementQName().getNamespaceURI()), "Namespace URI was not empty");
       Assert.assertTrue(Strings.isNullOrEmpty(detail.getElementQName().getPrefix()), "Namespace prefix was not empty");
        
       FaultActorBuilder faultActorBuilder = (FaultActorBuilder) builderFactory.getBuilder(FaultActor.DEFAULT_ELEMENT_NAME); 
       FaultActor faultActor = faultActorBuilder.buildObject();
       Assert.assertTrue(Strings.isNullOrEmpty(faultActor.getElementQName().getNamespaceURI()), "Namespace URI was not empty");
       Assert.assertTrue(Strings.isNullOrEmpty(faultActor.getElementQName().getPrefix()), "Namespace prefix was not empty");
       
       FaultCodeBuilder faultCodeBuilder = (FaultCodeBuilder) builderFactory.getBuilder(FaultCode.DEFAULT_ELEMENT_NAME); 
       FaultCode faultCode = faultCodeBuilder.buildObject();
       Assert.assertTrue(Strings.isNullOrEmpty(faultCode.getElementQName().getNamespaceURI()), "Namespace URI was not empty");
       Assert.assertTrue(Strings.isNullOrEmpty(faultCode.getElementQName().getPrefix()), "Namespace prefix was not empty");
       
       FaultStringBuilder faultStringBuilder = (FaultStringBuilder) builderFactory.getBuilder(FaultString.DEFAULT_ELEMENT_NAME); 
       FaultString faultString = faultStringBuilder.buildObject();
       Assert.assertTrue(Strings.isNullOrEmpty(faultString.getElementQName().getNamespaceURI()), "Namespace URI was not empty");
       Assert.assertTrue(Strings.isNullOrEmpty(faultString.getElementQName().getPrefix()), "Namespace prefix was not empty");
    }
}