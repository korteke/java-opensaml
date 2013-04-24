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

package org.opensaml.profile.action.impl;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * An action that schema validates inbound XML messages.
 * 
 * @event
 * {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID} <br/>
 * {@link org.opensaml.profile.action.EventIds#INVALID_MSG_CTX} <br/>
 * {@link #SCHEMA_INVALID}
 * @pre <pre>ProfileRequestContext.getInboundMessageContext().getMessage().getDOM() != null</pre>
 */
public class SchemaValidateXmlMessage extends AbstractProfileAction<XMLObject, Object> {

    /** ID of the event returned if the incoming request is schema invalid. */
    public static final String SCHEMA_INVALID = "SchemaInvalid";

    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(SchemaValidateXmlMessage.class);

    /** Schema used to validate incoming messages. */
    private final Schema validationSchema;

    /** The message to validate. */
    private XMLObject message;
    
    /**
     * Constructor.
     * 
     * @param schema schema used to validate incoming messages
     */
    public SchemaValidateXmlMessage(@Nonnull final Schema schema) {
        validationSchema = Constraint.isNotNull(schema, "Schema cannot be null");
    }

    /**
     * Gets the schema used to validate incoming messages.
     * 
     * @return schema used to validate incoming messages, not null after action is initialized
     */
    @Nonnull public Schema getValidationSchema() {
        return validationSchema;
    }

    /** {@inheritDoc} */
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext<XMLObject, Object> profileRequestContext)
            throws ProfileException {
        
        final MessageContext<XMLObject> msgCtx = profileRequestContext.getInboundMessageContext();
        if (msgCtx == null) {
            log.debug("Action {}: Inbound message context is null, unable to proceed", getId());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        message = msgCtx.getMessage();
        if (message == null) {
            log.debug("Action {}: Inbound message context did not contain a message, unable to proceed", getId());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        if (message.getDOM() == null) {
            log.debug("Action {}: Inbound message doesn't contain a DOM, unable to proceed", getId());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    protected void doExecute(@Nonnull final ProfileRequestContext<XMLObject, Object> profileRequestContext)
            throws ProfileException {

        log.debug("Action {}: Attempting to schema validate incoming message", getId());

        try {
            final Validator schemaValidator = validationSchema.newValidator();
            schemaValidator.validate(new DOMSource(message.getDOM()));
        } catch (SAXException e) {
            log.debug("Action {}: Incoming message {} is not schema-valid",
                    new Object[] {getId(), message.getElementQName(), e});
            ActionSupport.buildEvent(profileRequestContext, SCHEMA_INVALID);
            return;
        } catch (IOException e) {
            log.debug("Action {}: Unable to read incoming message");
            ActionSupport.buildEvent(profileRequestContext, SCHEMA_INVALID);
            return;
        }

        log.debug("Action {}: Incoming message is valid", getId());
    }
}