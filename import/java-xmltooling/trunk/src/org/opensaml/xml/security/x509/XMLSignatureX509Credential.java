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

package org.opensaml.xml.security.x509;

import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;

/**
 * A credential class which represents a {@link KeyInfoX509Credential} which was resolved based on a 
 * {@link KeyInfo} that was found within an XML {@link Signature} element.
 */
public class XMLSignatureX509Credential extends KeyInfoX509Credential {

    /** The Signature element which contains the KeyInfo resolution context. */
    private Signature signature;

    /**
     * Gets the Signature element which contains the KeyInfo resolution context.
     * 
     * @return signature being adapted
     */
    public Signature getSignature() {
        return signature;
    }
    
    /** {@inheritDoc} */
    public void setKeyInfo(KeyInfo info) {
        super.setKeyInfo(info);
        // KeyInfo -> Signature
        signature = (Signature) info.getParent();
    }
    
}