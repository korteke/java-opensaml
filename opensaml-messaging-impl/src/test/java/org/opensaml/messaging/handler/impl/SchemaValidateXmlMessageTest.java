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
import org.opensaml.messaging.handler.impl.SchemaValidateXmlMessage;
import net.shibboleth.utilities.java.support.resource.ClasspathResource;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.xml.SchemaBuilder;
import net.shibboleth.utilities.java.support.xml.SchemaBuilder.SchemaLanguage;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.mock.SimpleXMLObjectBuilder;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** Unit test for {@link SchemaValidateXmlMessage}. */
public class SchemaValidateXmlMessageTest extends XMLObjectBaseTestCase {

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

        Resource schemaResource = new ClasspathResource(SCHEMA_FILE);
        schemaResource.initialize();

        schema = SchemaBuilder.buildSchema(SchemaLanguage.XML, schemaResource);
    }

    /** Test a null inbound message context. */
    @Test
    public void testNullInboundMessageContext() throws Exception {

        SchemaValidateXmlMessage handler = new SchemaValidateXmlMessage(schema);
        handler.initialize();

        MessageContext messageContext = new MessageContext();

        try {
            handler.invoke(messageContext);
        } catch (MessageHandlerException e) {
            
        }
    }

    /** Test a null dom. */
    @Test public void testNullDom() throws Exception {

        SchemaValidateXmlMessage handler = new SchemaValidateXmlMessage(schema);
        handler.initialize();

        MessageContext messageContext = new MessageContext();

        SimpleXMLObject simpleXml = new SimpleXMLObjectBuilder().buildObject();

        messageContext.setMessage(simpleXml);
        
        try {
            handler.invoke(messageContext);
        } catch (MessageHandlerException e) {
            
        }
    }

    /** Test validation of an invalid xml file. */
    @Test public void testInvalidSchema() throws Exception {

        SchemaValidateXmlMessage handler = new SchemaValidateXmlMessage(schema);
        handler.initialize();

        MessageContext messageContext = new MessageContext();

        Resource invalidXmlResource = new ClasspathResource(INVALID_XML_FILE);
        invalidXmlResource.initialize();

        XMLObject invalidXml =
                XMLObjectSupport.unmarshallFromInputStream(parserPool, invalidXmlResource.getInputStream());

        messageContext.setMessage(invalidXml);
                
        try {
            handler.invoke(messageContext);
        } catch (MessageHandlerException e) {
            
        }
    }

    /** Test validation of a valid xml file. */
    @Test public void testValidSchema() throws Exception {

        SchemaValidateXmlMessage handler = new SchemaValidateXmlMessage(schema);
        handler.initialize();

        MessageContext messageContext = new MessageContext();

        Resource validXmlResource = new ClasspathResource(VALID_XML_FILE);
        validXmlResource.initialize();

        XMLObject validXml = XMLObjectSupport.unmarshallFromInputStream(parserPool, validXmlResource.getInputStream());

        messageContext.setMessage(validXml);
        
        handler.invoke(messageContext);
    }
}