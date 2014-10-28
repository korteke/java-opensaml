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

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml2.core.NameID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A context implementation which represents a SAML 1 {@link NameIdentifier} or a SAML 2 {@link NameID}.
 * 
 * <p>
 * The methods {@link #getSubjectNameIdentifier()}, {@link #getSAML1SubjectNameIdentifier()} and 
 * {@link #getSAML2SubjectNameID()} will attempt to dynamically resolve the appropriate data from 
 * the SAML message held in the parent message context if the data has not been set statically by 
 * the corresponding setter method. This evaluation will be attempted only if:
 * <ul>
 * <li>this context instance is an immediate child of a {@link MessageContext} as returned by {@link #getParent()}
 * <li>that message context holds a SAML 1 or 2 protocol message as an instance of {@link SAMLObject}</li>
 * <li>that SAML message is a type that may carry a subject: {@link org.opensaml.saml.saml1.core.SubjectQuery},
 *   {@link org.opensaml.saml.saml2.core.SubjectQuery} or {@link org.opensaml.saml.saml2.core.AuthnRequest}.
 * </ul>
 * </p>
 *
 */
public class SAMLSubjectNameIdentifierContext extends BaseContext {
    
    /** Logger. */
    @Nullable private Logger log = LoggerFactory.getLogger(SAMLSubjectNameIdentifierContext.class);

    /** The SAML name identifier represented by this context. */
    @Nullable private SAMLObject nameID;

    /**
     * Gets the subject name identifier represented by the context, which is guaranteed to be either 
     * a SAML 1 {@link NameIdentifier} or a SAML 2 {@link NameID}.
     * 
     * <p>
     * The value will be dynamically resolved if the immediate parent context of this context is
     * a {@link MessageContext} holding a {@link SAMLObject} representing a SAML protocol message,
     * and that protocol message contains a SAML 1 or SAML 2 subject.
     * </p>
     * 
     * @return the name identifier instance, may be null
     */
    @Nullable public SAMLObject getSubjectNameIdentifier() {
        if (nameID == null) {
            nameID = resolveNameIdentifier();
        }
        return nameID;
    }
    
    /**
     * Gets the SAML 1 {@link NameIdentifier} represented by this context.
     * 
     * <p>
     * Dynamic resolution is attempted per {@link #getSubjectNameIdentifier()}.
     * </p>
     * 
     * @return the name identifier instance or null
     */
    @Nullable public NameIdentifier getSAML1SubjectNameIdentifier() {
        final SAMLObject samlObject = getSubjectNameIdentifier();
        if (samlObject instanceof NameIdentifier) {
            return (NameIdentifier) samlObject;
        } else {
            return null;
        }
    }

    /**
     * Gets the SAML 2 {@link NameID} represented by this context.
     * 
     * <p>
     * Dynamic resolution is attempted per {@link #getSubjectNameIdentifier()}.
     * </p>
     * 
     * @return the name identifier instance or null
     */
    @Nullable public NameID getSAML2SubjectNameID() {
        final SAMLObject samlObject = getSubjectNameIdentifier();
        if (samlObject instanceof NameID) {
            return (NameID) samlObject;
        } else {
            return null;
        }
    }
    /**
     * Sets the name identifier, which must be either a SAML 1 {@link NameIdentifier} 
     * or a SAML 2 {@link NameID} or null.
     * 
     * @param newNameID the name identifier instance
     */
    public void setSubjectNameIdentifier(@Nullable final SAMLObject newNameID) {
        if (newNameID == null || newNameID instanceof NameIdentifier || newNameID instanceof NameID) {
            nameID = newNameID;
        } else {
            throw new IllegalArgumentException("Argument was not a valid SAML 1 or 2 name identifier type or null: " 
                    + newNameID.getClass().getName());
        }
    }

    /**
     * Dynamically resolve the name identifier from the SAML protocol message held in 
     * {@link MessageContext#getMessage()}, if it exists.
     * 
     * @return the name identifier, or null if it can not be resolved
     */
    @Nullable protected SAMLObject resolveNameIdentifier() {
        final SAMLObject samlMessage = resolveSAMLMessage();
        if (samlMessage == null) {
            log.debug("SAML message could not be dynamically resolved from parent context");
            return null;
        }
        if (samlMessage instanceof org.opensaml.saml.saml2.core.SubjectQuery) {
            final org.opensaml.saml.saml2.core.SubjectQuery query =  
                    (org.opensaml.saml.saml2.core.SubjectQuery) samlMessage;
            if (query.getSubject() != null) {
                return query.getSubject().getNameID();
            } else {
                return null;
            }
        } else if (samlMessage instanceof org.opensaml.saml.saml2.core.AuthnRequest) {
            final org.opensaml.saml.saml2.core.AuthnRequest request = 
                    (org.opensaml.saml.saml2.core.AuthnRequest) samlMessage;
            if (request.getSubject() != null) {
                return request.getSubject().getNameID();
            } else {
                return null;
            }
        } else if (samlMessage instanceof Request && ((Request) samlMessage).getSubjectQuery() != null) {
            final org.opensaml.saml.saml1.core.SubjectQuery query = ((Request) samlMessage).getSubjectQuery();
            if (query.getSubject() != null) {
                return query.getSubject().getNameIdentifier();
            } else {
                return null;
            }
        } else {
            log.debug("Message in resolved parent message context was not a supported instance of SAMLObject: {}", 
                    samlMessage.getClass().getName());
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
            final MessageContext parent = (MessageContext) getParent();
            if (parent.getMessage() instanceof SAMLObject) {
                return (SAMLObject) parent.getMessage();
            } 
        }
        return null;
    }

}