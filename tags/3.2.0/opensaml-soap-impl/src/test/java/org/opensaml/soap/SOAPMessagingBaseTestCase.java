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

package org.opensaml.soap;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.testng.annotations.BeforeMethod;

/**
 *
 */
public class SOAPMessagingBaseTestCase extends XMLObjectBaseTestCase {
    
    private MessageContext<XMLObject> messageContext;
    
    private Envelope envelope;
    
    protected MessageContext<XMLObject> getMessageContext() {
        return messageContext;
    }
    
    protected Envelope getEnvelope() {
        return envelope;
    }
    
    @BeforeMethod
    protected void setUpMessageContextAndEnvelope() {
        messageContext = new MessageContext<>();
        messageContext.setMessage(buildXMLObject(simpleXMLObjectQName));
        
        envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        envelope.setBody((Body) buildXMLObject(Body.DEFAULT_ELEMENT_NAME));
        envelope.getBody().getUnknownXMLObjects().add(messageContext.getMessage());
        messageContext.getSubcontext(SOAP11Context.class, true).setEnvelope(envelope);
    }

}
