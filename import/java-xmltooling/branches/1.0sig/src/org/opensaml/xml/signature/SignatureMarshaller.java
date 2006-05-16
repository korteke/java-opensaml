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

import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;

/**
 * A marshaller for {@link org.opensaml.xml.signature.Signature} objects that creates the enveloped XML Digital
 * Signature element and its children and signs the DOM.
 */
public interface SignatureMarshaller extends Marshaller {

    /**
     * Signs the given DOM element using the information provided in the Signature.
     * 
     * @param domElement the DOM element to be signed
     * @param signature information necessary to perform the signing
     * 
     * @throws MarshallingException thrown if the signature can not be performed
     */
    public void signElement(Element domElement, Signature signature) throws MarshallingException;
}