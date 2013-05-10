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

package org.opensaml.saml.common.messaging.context;

import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

/** A {@link Context} intended to be used as a subcontext of a {@link MessageContext}  that carries 
 * some basic information about the SAML message.
 * 
 * <p>
 * The methods {@link #getMessageId()} and {@link #getMessageIssueInstant()} will attempt to 
 * dynamically resolve the appropriate data from the SAML message held in the message context
 * if the data has not been set statically by the corresponding setter method. This evaluation
 * will be attempted only if the this context instance is an immediate child of the message context,
 * as returned by {@link #getParent()}.
 * </p>
 *
 */
public class SamlMessageInfoContext extends BaseContext {

    /** The ID of the message. */
    private String messageId;

    /** The issue instant of the message. */
    private DateTime issueInstant;

    /**
     * Gets the ID of the message.
     * 
     * @return ID of the message, may be null
     */
    @Nullable public String getMessageId() {
        if (messageId == null) {
            messageId = resolveMessageId();
        }
        return messageId;
    }

    /**
     * Sets the ID of the message.
     * 
     * @param newMessageId ID of the message
     */
    public void setMessageId(@Nullable final String newMessageId) {
        messageId = StringSupport.trimOrNull(newMessageId);
    }

    /**
     * Gets the issue instant of the message.
     * 
     * @return issue instant of the message
     */
    @Nullable public DateTime getMessageIssueInstant() {
        if (issueInstant == null) {
            issueInstant = resolveIssueInstant();
        }
        return issueInstant;
    }

    /**
     * Sets the issue instant of the message.
     * 
     * @param messageIssueInstant issue instant of the message
     */
    public void setMessageIssueInstant(@Nullable final DateTime messageIssueInstant) {
        issueInstant = messageIssueInstant;
    }

    /**
     * Dynamically resolve the message ID from the SAML protocol message held in 
     * {@link MessageContext#getMessage()}.
     * 
     * @return the message ID, or null if it can not be resolved
     */
    @Nullable protected String resolveMessageId() {
        SAMLObject samlMessage = resolveSAMLMessage();
        if (samlMessage instanceof org.opensaml.saml.saml2.core.RequestAbstractType) {
            org.opensaml.saml.saml2.core.RequestAbstractType request =  
                    (org.opensaml.saml.saml2.core.RequestAbstractType) samlMessage;
            return request.getID();
        //SAML 2 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml2.core.StatusResponseType) {
            org.opensaml.saml.saml2.core.StatusResponseType response = 
                    (org.opensaml.saml.saml2.core.StatusResponseType) samlMessage;
            return response.getID();
        //SAML 1 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.ResponseAbstractType) {
            org.opensaml.saml.saml1.core.ResponseAbstractType response = 
                    (org.opensaml.saml.saml1.core.ResponseAbstractType) samlMessage;
            return response.getID();
        //SAML 1 Request
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.RequestAbstractType) {
            org.opensaml.saml.saml1.core.RequestAbstractType request = 
                    (org.opensaml.saml.saml1.core.RequestAbstractType) samlMessage;
            return request.getID();
        }
        return null;
    }
    
    /**
     * Dynamically resolve the message issue instant from the SAML protocol message held in 
     * {@link MessageContext#getMessage()}.
     * 
     * @return the message issue instant, or null if it can not be resolved
     */
    @Nullable protected DateTime resolveIssueInstant() {
        SAMLObject samlMessage = resolveSAMLMessage();
        //SAML 2 Request
        if (samlMessage instanceof org.opensaml.saml.saml2.core.RequestAbstractType) {
            org.opensaml.saml.saml2.core.RequestAbstractType request =  
                    (org.opensaml.saml.saml2.core.RequestAbstractType) samlMessage;
            return request.getIssueInstant();
        //SAML 2 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml2.core.StatusResponseType) {
            org.opensaml.saml.saml2.core.StatusResponseType response = 
                    (org.opensaml.saml.saml2.core.StatusResponseType) samlMessage;
            return response.getIssueInstant();
        //SAML 1 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.ResponseAbstractType) {
            org.opensaml.saml.saml1.core.ResponseAbstractType response = 
                    (org.opensaml.saml.saml1.core.ResponseAbstractType) samlMessage;
            return response.getIssueInstant();
        //SAML 1 Request
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.RequestAbstractType) {
            org.opensaml.saml.saml1.core.RequestAbstractType request = 
                    (org.opensaml.saml.saml1.core.RequestAbstractType) samlMessage;
            return request.getIssueInstant();
        }
        
        return null;
    }
    
    /**
     * Resolve the SAML message from the message context.
     * 
     * @return the SAML message, or null if it can not be resolved
     */
    @Nullable protected SAMLObject resolveSAMLMessage() {
        if (getParent() instanceof MessageContext) {
            MessageContext parent = (MessageContext) getParent();
            if (parent.getMessage() instanceof SAMLObject) {
                return (SAMLObject) parent.getMessage();
            } 
        }
        return null;
    }

}