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

package org.opensaml.messaging.handler;

import java.util.List;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;


/**
 *
 * A specialization of {@link MessageHandler} which represents an ordered list of
 * message handlers which may be invoked in order.
 * 
 * @param <MessageType> the type of message being handled
 */
public interface MessageHandlerChain<MessageType> extends MessageHandler<MessageType> {
    
    /**
     * Get the ordered list of message handlers which comprise the handler chain.
     * 
     * @return the list of members of the handler chain
     */
    @NonnullAfterInit @NonnullElements List<MessageHandler<MessageType>> getHandlers();
    
}