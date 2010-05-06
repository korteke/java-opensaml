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
package org.opensaml.messaging.error;

import org.opensaml.messaging.context.MessageContext;

/**
 * Component that handles message processing-related errors.
 * 
 * @param <MessageType> the type of message being handled
 */
public interface MessageErrorHandler<MessageType> {
    
    /**
     * Handle a particular thrown error.
     * 
     * @param t the error that was thrown
     * @param messageContext the message context being processed, if available
     * @return true if error was successfully handled, false otherwise
     */
    public boolean handleError(Throwable t, MessageContext<MessageType> messageContext);

}
