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

package org.opensaml.saml.common.binding;

import java.util.List;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Body handler impl for use with SAML SOAP message decoders.
 */
public class SAMLSOAPDecoderBodyHandler extends AbstractMessageHandler<SAMLObject> {
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(SAMLSOAPDecoderBodyHandler.class);

    /** {@inheritDoc} */
    protected void doInvoke(MessageContext messageContext) throws MessageHandlerException {
        SOAP11Context soap11Context = messageContext.getSubcontext(SOAP11Context.class, false);
        Constraint.isNotNull(soap11Context, "SOAP 1.1 context was not present in message context");
        Envelope soapMessage = soap11Context.getEnvelope();
        Constraint.isNotNull(soapMessage, "SOAP 1.1 envelope was not present in SOAP context");
        
        List<XMLObject> soapBodyChildren = soapMessage.getBody().getUnknownXMLObjects();
        if (soapBodyChildren.size() < 1 || soapBodyChildren.size() > 1) {
            log.error("Unexpected number of children in the SOAP body, " + soapBodyChildren.size()
                    + ".  Unable to extract SAML message");
            throw new MessageHandlerException("Unexpected number of children in the SOAP body, unable to extract SAML message");
        }

        XMLObject incommingMessage = soapBodyChildren.get(0);
        if (!(incommingMessage instanceof SAMLObject)) {
            log.error("Unexpected SOAP body content.  Expected a SAML request but recieved {}", incommingMessage
                    .getElementQName());
            throw new MessageHandlerException("Unexpected SOAP body content.  Expected a SAML request but recieved "
                    + incommingMessage.getElementQName());
        }
        
        messageContext.setMessage(incommingMessage);
        
    }

}
