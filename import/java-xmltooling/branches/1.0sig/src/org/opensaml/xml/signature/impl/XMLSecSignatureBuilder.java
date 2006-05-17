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

package org.opensaml.xml.signature.impl;

import javax.xml.namespace.QName;

import org.opensaml.xml.AbstractXMLObjectBuilder;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.signature.Signature;
import org.w3c.dom.Element;

/**
 * Builder of {@link org.opensaml.xml.signature.impl.XMLSecSignatureImpl}s.
 */
public class XMLSecSignatureBuilder extends AbstractXMLObjectBuilder<Signature> {

    /**
     * Constructor
     */
    public XMLSecSignatureBuilder() {

    }

    public XMLObject buildObject() {
        Signature signature = new XMLSecSignatureImpl();
        return signature;
    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(org.w3c.dom.Element)
     */
    public Signature buildObject(Element element) {
        return new XMLSecSignatureImpl();
    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(java.lang.String, java.lang.String, java.lang.String)
     */
    public Signature buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new XMLSecSignatureImpl();
    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(java.lang.String, java.lang.String, java.lang.String, javax.xml.namespace.QName)
     */
    public Signature buildObject(String namespaceURI, String localName, String namespacePrefix, QName schemaType) {
        return new XMLSecSignatureImpl();
    }
}