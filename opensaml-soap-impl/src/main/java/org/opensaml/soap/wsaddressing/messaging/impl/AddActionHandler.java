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

package org.opensaml.soap.wsaddressing.messaging.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wsaddressing.Action;
import org.opensaml.soap.wsaddressing.messaging.context.WSAddressingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler implementation that adds a wsa:Action header to the outbound SOAP envelope.
 * 
 * <p>
 * The value from {@link WSAddressingContext#getActionURI()} will be used, if present. If not,
 * then the locally-configured value from {@link #getActionURI()} will be used.  If neither is present,
 * no header will be added.
 * </p>
 */
public class AddActionHandler extends AbstractMessageHandler {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(AddActionHandler.class);
    
    /** The Action URI value. */
    private String actionURI;
    
    /**
     * Get the Action URI.
     * 
     * @return the URI, or null
     */
    @Nullable public String getActionURI() {
        return actionURI;
    }

    /**
     * Set the expected Action URI value. 
     * 
     * @param uri the new URI value
     */
    public void setActionURI(@Nullable final String uri) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        actionURI = StringSupport.trimOrNull(uri);
    }
    
    /** {@inheritDoc} */
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        // A non-null subcontext value will override what is statically configured
        WSAddressingContext addressing = messageContext.getSubcontext(WSAddressingContext.class, false);
        if (addressing != null && addressing.getActionURI() != null) {
            actionURI = addressing.getActionURI();
        }
        if (actionURI == null) {
            log.debug("No WS-Addressing Action URI found locally or in message context, skipping further processing");
            return false;
        } else {
            return super.doPreInvoke(messageContext);
        }
    }

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        log.debug("Issuing WS-Addressing Action header with URI value: {}", getActionURI());
        Action action = (Action) XMLObjectSupport.buildXMLObject(Action.ELEMENT_NAME);
        action.setValue(getActionURI());
        SOAPMessagingSupport.addHeaderBlock(messageContext, action);
    }

}
