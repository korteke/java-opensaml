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

package org.opensaml.soap.soap11;

import javax.xml.namespace.QName;

import org.opensaml.soap.BaseTestCase;
import org.opensaml.ws.soap.soap11.Body;
import org.opensaml.ws.soap.soap11.Detail;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.ws.soap.soap11.Fault;
import org.opensaml.ws.soap.soap11.FaultActor;
import org.opensaml.ws.soap.soap11.FaultCode;
import org.opensaml.ws.soap.soap11.FaultString;
import org.opensaml.ws.soap.soap11.Header;
import org.opensaml.ws.soap.util.SOAPConstants;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests marshalling and unmarshalling SOAP messages.
 */
public class SOAPTest extends BaseTestCase {
    
    /** Path, on classpath, to SOAP message test document. */
    private String soapMessage;
    
    /** Path, on classpath, to SOAP fault test document. */
    private String soapFault;
    
    private QName expectedFaultCode;
    
    private String expectedFaultString;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        soapMessage = "/data/org/opensaml/soap/soap11/SOAP.xml";
        soapFault = "/data/org/opensaml/soap/soap11/SOAPFault.xml";
        
        expectedFaultCode = new QName(SOAPConstants.SOAP11_NS, "Server", SOAPConstants.SOAP11_PREFIX);
        expectedFaultString = "Server Error";
    }
    
    /**
     * Test unmarshalling a SOAP message, dropping its DOM representation and then remarshalling it.
     * 
     * @throws XMLParserException thrown if the XML document can not be located or parsed into a DOM 
     * @throws UnmarshallingException thrown if the DOM can not be unmarshalled
     */
    public void testSOAPMessage() throws XMLParserException, UnmarshallingException{
        Document soapDoc = parserPool.parse(SOAPTest.class.getResourceAsStream(soapMessage));
        Element envelopeElem = soapDoc.getDocumentElement();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(envelopeElem);
        
        Envelope envelope = (Envelope) unmarshaller.unmarshall(envelopeElem);
        
        // Check to make sure everything unmarshalled okay
        //TODO test encodingStyle
        
        Header header = envelope.getHeader();
        assertNotNull("Header was null", header);
        assertEquals("Unexpected number of Header children", 1, header.getUnknownXMLObjects().size());
        
        Body body = envelope.getBody();
        assertNotNull("Body was null", body);
        assertEquals("Unexpected number of Body children", 1, body.getUnknownXMLObjects().size());
        
        // Drop the DOM and remarshall, hopefully we get the same document back
        envelope.releaseDOM();
        envelope.releaseChildrenDOM(true);
        assertEquals("Marshalled DOM was not the same as control DOM", soapDoc, envelope);
    }
    
    /**
     * Test unmarshalling a SOAP fault, dropping its DOM representation and then remarshalling it.
     * @throws XMLParserException thrown if the XML document can not be located or parsed into a DOM 
     * @throws UnmarshallingException thrown if the DOM can not be unmarshalled
     */
    public void testSOAPFault() throws XMLParserException, UnmarshallingException{
        Document soapFaultDoc = parserPool.parse(SOAPTest.class.getResourceAsStream(soapFault));
        Element envelopeElem = soapFaultDoc.getDocumentElement();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(envelopeElem);
        
        Envelope envelope = (Envelope) unmarshaller.unmarshall(envelopeElem);
        
        // Check to make sure everything unmarshalled okay
        Header header = envelope.getHeader();
        assertNull("Header was not null", header);
        
        Body body = envelope.getBody();
        assertNotNull("Body was null", body);
        assertEquals("Unexpected number of Body children", 1, body.getUnknownXMLObjects().size());
        
        Fault fault = (Fault) body.getUnknownXMLObjects().get(0);
        assertNotNull("Fault was null", fault);
        
        FaultActor actor = fault.getActor();
        assertNull("Actor was not null", actor);
        
        FaultCode code = fault.getCode();
        assertNotNull("FaultCode was null", code);
        assertEquals("FaultCode had unexpected value", code.getValue(), expectedFaultCode);
        
        FaultString message = fault.getMessage();
        assertNotNull("FaultString was null", message);
        assertEquals("FaultString had unexpected value", message.getValue(), expectedFaultString);
        
        Detail detail = fault.getDetail();
        assertNotNull("Detail was null", detail);
        assertEquals("Unexpected number of Body children", 1, detail.getUnknownXMLObjects().size());
        
        // Drop the DOM and remarshall, hopefully we get the same document back
        envelope.releaseDOM();
        envelope.releaseChildrenDOM(true);
        assertEquals("Marshalled DOM was not the same as control DOM", soapFaultDoc, envelope);
    }
}