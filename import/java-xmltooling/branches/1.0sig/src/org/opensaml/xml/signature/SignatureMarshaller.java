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

/**
 * 
 */

package org.opensaml.xml.signature;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A marshaller for {@link org.opensaml.xml.signature.Signature} objects.  This marshaller is really a no-op
 * class.  All the creation of the signature DOM elements is handled by {@link org.opensaml.xml.signature.Signer} 
 * when it signs the object.
 */
public class SignatureMarshaller implements Marshaller {

    /**
     * Constructor
     */
    public SignatureMarshaller() {
        
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(org.opensaml.xml.XMLObject)
     */
    public Element marshall(XMLObject xmlObject) throws MarshallingException {
        return null;
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(org.opensaml.xml.XMLObject, org.w3c.dom.Element)
     */
    public Element marshall(XMLObject xmlObject, Element parentElement) throws MarshallingException {
        return null;
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(org.opensaml.xml.XMLObject, org.w3c.dom.Document)
     */
    public Element marshall(XMLObject xmlObject, Document document) throws MarshallingException {
        return null;
    }
}