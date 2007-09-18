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

package org.opensaml.xml.encryption.impl;

import org.apache.log4j.Logger;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * An abstract unmarshaller implementation for XMLObjects from {@link org.opensaml.xml.encryption}.
 */
public abstract class AbstractXMLEncryptionUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /**
     * Logger.
     */
    private static Logger log = Logger.getLogger(AbstractXMLEncryptionUnmarshaller.class);

    /**
     * Constructor.
     *
     * @param targetNamespaceURI namespace URI
     * @param targetLocalName local name
     */
    protected AbstractXMLEncryptionUnmarshaller(String targetNamespaceURI, String targetLocalName){
        super(targetNamespaceURI, targetLocalName);
    }

    /**
     * {@inheritDoc}
     */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Ignoring unknown element " + childXMLObject.getElementQName());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Ignorning unknown attribute " + attribute.getLocalName());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void processElementContent(XMLObject xmlObject, String elementContent) {
        if (log.isDebugEnabled()) {
            log.debug("Ignoring element content " + elementContent);
        }
    }
}
