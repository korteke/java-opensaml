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
package org.opensaml.ws.wsaddressing;

import java.io.InputStream;

import org.opensaml.ws.WSBaseTestCase;
import org.opensaml.xml.XMLConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WSAddressingObjectTestCase is the test case for the WS-Addressing objects.
 * 
 */
public class WSAddressingObjectsTestCase extends WSBaseTestCase {

    public Logger log= LoggerFactory.getLogger(WSAddressingObjectsTestCase.class);

    /** {@inheritDoc} */
    protected void configureWS() throws Exception {
        // load ws-policy config
        InputStream is= getClass().getResourceAsStream("/wsaddressing-config.xml");
        XMLConfigurator configurator= new XMLConfigurator();
        configurator.load(is);
    }

    public void testAction() throws Exception {
        Action action= buildXMLObject(Action.ELEMENT_NAME);
        action.setValue("urn:test:foo:bar");
        marshallAndUnmarshall(action);
    }

    public void testAddress() throws Exception {
        Address address= buildXMLObject(Address.ELEMENT_NAME);
        address.setValue(Address.ANONYMOUS);
        marshallAndUnmarshall(address);
    }

    public void testEndpointReference() throws Exception {
        EndpointReference epr= buildXMLObject(EndpointReference.ELEMENT_NAME);
        Address address= buildXMLObject(Address.ELEMENT_NAME);
        address.setValue(Address.ANONYMOUS);
        ReferenceParameters referenceParameters= buildXMLObject(ReferenceParameters.ELEMENT_NAME);
        Metadata metadata= buildXMLObject(Metadata.ELEMENT_NAME);
        epr.setAddress(address);
        epr.setMetadata(metadata);
        epr.setReferenceParameters(referenceParameters);
        marshallAndUnmarshall(epr);
    }
    
    public void testFaultTo() {
        //TODO
    }

    public void testFrom() {
        //TODO
    }

    public void testMessageID() {
        //TODO
    }

    public void testMetadata() throws Exception {
        Metadata metadata= buildXMLObject(Metadata.ELEMENT_NAME);
        // TODO: add some child elements

        marshallAndUnmarshall(metadata);
    }
    
    public void testProblemAction() {
        //TODO
    }

    public void testProblemHeaderQName() {
        //TODO
    }

    public void testProblemIRI() {
        //TODO
    }
    
    public void testReferenceParameters() throws Exception {
        ReferenceParameters referenceParameters= buildXMLObject(ReferenceParameters.ELEMENT_NAME);
        // TODO: add some child elements
        marshallAndUnmarshall(referenceParameters);
    }

    public void testRelatesTo() {
        //TODO
    }

    public void testReplyTo() {
        //TODO
    }

    public void testRetryAfter() {
        //TODO
    }

    public void testSoapAction() {
        //TODO
    }

    public void testTo() {
        //TODO
    }

}
