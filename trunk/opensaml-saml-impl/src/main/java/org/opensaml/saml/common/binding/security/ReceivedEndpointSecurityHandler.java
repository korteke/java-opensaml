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

package org.opensaml.saml.common.binding.security;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.MessageException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.util.net.BasicUrlComparator;
import org.opensaml.util.net.UriComparator;

/**
 * Message handler which checks the validity of the SAML protocol message receiver 
 * endpoint against requirements indicated in the message.
 */
public class ReceivedEndpointSecurityHandler extends AbstractMessageHandler<SAMLObject> {
    
    /** The URI comparator to use in performing the validation. */
    private UriComparator uriComparator;

    /**
     * Constructor.
     */
    public ReceivedEndpointSecurityHandler() {
        super();
        uriComparator = new BasicUrlComparator();
    }

    /**
     * Get the URI comparator instance to use.
     * 
     * @return the uriComparator.
     */
    @Nonnull public UriComparator getUriComparator() {
        return uriComparator;
    }

    /**
     * Set the URI comparator instance to use.
     * 
     * @param comparator the new URI comparator to use
     */
    public void setUriComparator(@Nonnull final UriComparator comparator) {
       uriComparator = Constraint.isNotNull(comparator, "UriComparator may not be null");
    }

    /** {@inheritDoc} */
    protected void doInvoke(MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        try {
            if (!SAMLBindingSupport.checkEndpointUri(messageContext, getUriComparator())) {
                throw new MessageHandlerException("SAML message failed received endpoint check");
            }
        } catch (MessageException e) {
            throw new MessageHandlerException("Evaluation of SAML message received endpoint check failed", e);
        }
    }

}
