/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.signature;

import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Element;

/**
 * An unmarshaller for {@link org.opensaml.xml.signature.Signature} objects. This unmarshaller unmarshalls 
 * the enveloped XML Digital Signature element (and it's children) and validates the signature.
 */
public interface SignatureUnmarshaller extends Unmarshaller {

    /**
     * Verifies the signature on the DOM element using the information provided in the Signature.
     * 
     * @param domElement the DOM element whose signature should be verified
     * @param signature information necessary to verify the signature
     * 
     * @throws UnmarshallingException thrown if the signature can not be verified
     */
    public void verifySignature(Element domElement, Signature signature) throws UnmarshallingException;
}