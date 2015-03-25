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

package org.opensaml.soap.client;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.common.SOAPException;


/**
 * An interface for a very basic SOAP client.
 * 
 * Implementations of this interface do NOT attempt to do intelligent things like figure out when and how to attach
 * WS-Security headers. It is strictly meant to open sockets, shuttle messages over it, and return a response.
 */
@ThreadSafe
public interface SOAPClient {

    /**
     * Sends a message and waits for a response.
     * 
     * @param endpoint the endpoint to which to send the message
     * @param context the operation context containing the outbound SOAP message
     * 
     * @throws SOAPException thrown if there is a problem sending the message or receiving the response or if the
     *             response is a SOAP fault
     * @throws SecurityException thrown if the response does not meet any security policy associated with the message
     *             context
     */
    void send(@Nonnull @NotEmpty final String endpoint, @Nonnull final InOutOperationContext context)
            throws SOAPException, SecurityException;

    /** Marker interface for binding/transport request parameters. */
    interface SOAPRequestParameters {};
}