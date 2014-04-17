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

package org.opensaml.messaging.handler.impl;

import javax.xml.validation.Schema;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.messaging.handler.impl.SchemaValidateXMLMessage;

import net.shibboleth.utilities.java.support.xml.SchemaBuilder;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.mock.SimpleXMLObjectBuilder;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** Unit test for {@link SchemaValidateXMLMessage}. */
public class SchemaValidateXMLMessageTest extends XMLObjectBaseTestCase {

    /** Simple xml object schema file. */
    private static final String SCHEMA_FILE = "org/opensaml/messaging/handler/impl/schemaValidateXmlMessageTest-schema.xsd";

    /** Invalid xml file. */
    private static final String INVALID_XML_FILE =
            "org/opensaml/messaging/handler/impl/schemaValidateXmlMessageTest-invalid.xml";

    /** Valid xml file. */
    private static final String VALID_XML_FILE =
            "org/opensaml/messaging/handler/impl/schemaValidateXmlMessageTest-valid.xml";

    /** The simple xml object schema. */
    private Schema schema;

    /**
     * Build the schema.
     * 
     * @throws Exception
     */
    @BeforeClass public void setUp() throws Exception {

        final Resource r = new ClassPathResource(SCHEMA_FILE);
        final SchemaBuilder schemaBuilder = new SchemaBuilder();
        schemaBuilder.addSchema(r.getInputStream());
        schema = schemaBuilder.buildSchema();
    }

    /** Test a null inbound message context. */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testNullInboundMessageContext() throws Exception {

        final SchemaValidateXMLMessage handler = new SchemaValidateXMLMessage(schema);
        handler.setId("test");
        handler.initialize();

        final MessageContext messageContext = new MessageContext();

        handler.invoke(messageContext);
    }

    /** Test a null dom. */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testNullDom() throws Exception {

        final SchemaValidateXMLMessage<SimpleXMLObject> handler = new SchemaValidateXMLMessage(schema);
        handler.setId("test");
        handler.initialize();

        final MessageContext messageContext = new MessageContext();

        final SimpleXMLObject simpleXml = new SimpleXMLObjectBuilder().buildObject();

        messageContext.setMessage(simpleXml);
        
        handler.invoke(messageContext);
    }

    /** Test validation of an invalid xml file. */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testInvalidSchema() throws Exception {

        final SchemaValidateXMLMessage handler = new SchemaValidateXMLMessage(schema);
        handler.setId("test");
        handler.initialize();

        final MessageContext messageContext = new MessageContext();

        final Resource invalidXmlResource = new ClassPathResource(INVALID_XML_FILE);

        final XMLObject invalidXml =
                XMLObjectSupport.unmarshallFromInputStream(parserPool, invalidXmlResource.getInputStream());

        messageContext.setMessage(invalidXml);
                
        handler.invoke(messageContext);
    }

    /** Test validation of a valid xml file. */
    @Test public void testValidSchema() throws Exception {

        final SchemaValidateXMLMessage handler = new SchemaValidateXMLMessage(schema);
        handler.setId("test");
        handler.initialize();

        final MessageContext messageContext = new MessageContext();

        final Resource validXmlResource = new ClassPathResource(VALID_XML_FILE);

        final XMLObject validXml =
                XMLObjectSupport.unmarshallFromInputStream(parserPool, validXmlResource.getInputStream());

        messageContext.setMessage(validXml);
        
        handler.invoke(messageContext);
    }
}