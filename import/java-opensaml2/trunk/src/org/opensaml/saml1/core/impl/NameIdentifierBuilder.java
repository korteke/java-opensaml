/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.saml1.core.impl;

import org.opensaml.common.impl.AbstractSAMLObjectBuilder;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml1.core.NameIdentifier;

/**
 * Builder of{@link org.opensaml.saml1.core.impl.NameIdentifierImpl} objects.
 */
public class NameIdentifierBuilder extends AbstractSAMLObjectBuilder<NameIdentifier> {

    /**
     * Constructor
     */
    public NameIdentifierBuilder() {

    }

    /*
     * @see org.opensaml.common.impl.AbstractSAMLObjectBuilder#buildObject()
     */
    public NameIdentifier buildObject() {
        return buildObject(SAMLConstants.SAML1_NS, NameIdentifier.LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(java.lang.String, java.lang.String, java.lang.String)
     */
    public NameIdentifier buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new NameIdentifierImpl(namespaceURI, localName, namespacePrefix);
    }
}