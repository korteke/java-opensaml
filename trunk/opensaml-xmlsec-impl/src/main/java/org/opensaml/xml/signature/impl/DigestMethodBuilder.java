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

package org.opensaml.xml.signature.impl;

import org.opensaml.core.xml.AbstractXMLObjectBuilder;
import org.opensaml.xmlsec.encryption.EncryptionConstants;
import org.opensaml.xmlsec.signature.DigestMethod;
import org.opensaml.xmlsec.signature.XMLSignatureBuilder;

/**
 * Builder of {@link org.opensaml.xmlsec.signature.DigestMethod}.
 */
public class DigestMethodBuilder extends AbstractXMLObjectBuilder<DigestMethod> implements
        XMLSignatureBuilder<DigestMethod> {

    /**
     * Constructor.
     * 
     */
    public DigestMethodBuilder() {
    }

    /** {@inheritDoc} */
    public DigestMethod buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new DigestMethodImpl(namespaceURI, localName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public DigestMethod buildObject() {
        return buildObject(EncryptionConstants.XMLENC_NS, DigestMethod.DEFAULT_ELEMENT_LOCAL_NAME,
                EncryptionConstants.XMLENC_PREFIX);
    }
}