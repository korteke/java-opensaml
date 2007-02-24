/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.ws;

import java.io.InputStream;

/**
 * Represents the source of an incoming message.
 * 
 * If the message is being delivered in an asychronous method invoking {@link #getMessageAsStream()},
 * {@link #getMessageAsString()}, or {@link #getMessageAsString(String)} may return null if the message has not yet
 * arrived.
 */
public interface MessageSource {

    /**
     * Gets the character encoding of the message, if known.
     * 
     * @return character encoding of the message
     */
    public String getContentCharacterEncoding();

    /**
     * Gets the length, in bytes, of the message.
     * 
     * @return length, in bytes, of the message
     */
    public long getContentLength();

    /**
     * Gets the MIME type of the message, if known.
     * 
     * @return MIME type of the message
     */
    public String getContentType();

    /**
     * Gets the message, if available, as a stream.
     * 
     * @return message as a stream
     */
    public InputStream getMessageAsStream();

    /**
     * Gets the message, if available, as a string encoded using the encoding type provided by
     * {@link #getContentCharacterEncoding()} or UTF-8 if the encoding type is not known.
     * 
     * @return message as a string
     */
    public String getMessageAsString();

    /**
     * Gets the message, if available as an encoded string.
     * 
     * @param encoding character encoding to use when creating the string
     * 
     * @return message as a string
     */
    public String getMessageAsString(String encoding);
}