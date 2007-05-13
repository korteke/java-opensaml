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

package org.opensaml.common.binding.decoding;

/**
 * Simple builder interface for {@link MessageDecoder}s.
 * 
 * Builders must be thread-safe and reusable.
 */
public interface MessageDecoderBuilder {

    /**
     * Creates a new instance of the message decoder.
     * 
     * @return new instance of the message decoder
     */
    public MessageDecoder buildDecoder();
}