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

package org.opensaml.soap.messaging.context;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.soap.soap11.Envelope;

/**
 * Subcontext that carries information about the SOAP 1.1 message transport.
 */
public class SOAP11Context extends BaseContext {
    
    //TODO handle storage for understood headers here also?

    /** The SAML protocol in use. */
    private Envelope envelope;

    /**
     * Gets the current SOAP 1.1 Envelope.
     * 
     * @return current SOAP 1.1 Envelope, may be null
     */
    @Nullable public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * Sets the current SOAP 1.1 Envelope.
     * 
     * @param newEnvelope the current SOAP 1.1 Envelope
     */
    public void setEnvelope(@Nullable final Envelope newEnvelope) {
        envelope = newEnvelope;
    }
    
}