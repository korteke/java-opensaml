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

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SigningContext;

/**
 * Builder of {@link org.opensaml.xml.signature.impl.XMLSecSignatureImpl}s.
 */
public class XMLSecSignatureBuilder implements XMLObjectBuilder {

    /** Signing context used when creating a XMLSecSignatureImpl */
    private SigningContext context;

    /**
     * Constructor
     */
    public XMLSecSignatureBuilder() {

    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject()
     */
    public XMLObject buildObject() {
        Signature signature = new XMLSecSignatureImpl(context);
        return signature;
    }
}