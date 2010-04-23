/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensaml.messaging.handler.impl;

import org.opensaml.messaging.handler.MessageHandler;


/**
 * A base abstract implementation of {@link MessageHandler}.
 * 
 * @param <MessageType> the type of message being handled
 */
public abstract class BaseMessageHandler<MessageType> implements MessageHandler<MessageType> {

    /** The handler unique identifier. */
    private String id;

    /** {@inheritDoc} */
    public String getId() {
        return id;
    }
    
    /**
     * Set the handler's unique identifier.
     * 
     * @param newId the handler's new unique identifier
     */
    public void setId(String newId) {
        id = newId;
    }

}
