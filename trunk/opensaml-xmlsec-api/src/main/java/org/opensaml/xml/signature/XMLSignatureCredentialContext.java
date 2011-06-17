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

package org.opensaml.xml.signature;

import org.opensaml.xml.security.credential.CredentialContext;

/**
 * A credential context for credentials resolved from a {@link KeyInfo} that was found in 
 * in XML Signature {@link Signature} element.
 */
public class XMLSignatureCredentialContext implements CredentialContext {

    /** The Signature element context. */ 
    private Signature sig;
    
    /**
     * Constructor.
     *
     * @param signature the signature resolution context
     */
    public XMLSignatureCredentialContext(Signature signature) {
        sig = signature;
    }

    /**
     * Gets the Signature element context.
     * 
     * @return signature context
     */
    public Signature getSignature() {
        return sig;
    }
    
}