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

/**
 *
 * A specialization of error handler that explicitly indicates that it is capable of handling 
 * one or more types of throwable errors.
 * 
 * @param <MessageType> the type of message being handled
 */
public interface TypedMessageErrorHandler<MessageType> extends MessageErrorHandler<MessageType> {
    
    /**
     * Indicates whether the message error handle is capable of handling the specified error.
     * 
     * @param t the type of error about which to query
     * @return true if the handle can handle the specified error type, false otherwise
     */
    public boolean handlesError(Throwable t);

}
