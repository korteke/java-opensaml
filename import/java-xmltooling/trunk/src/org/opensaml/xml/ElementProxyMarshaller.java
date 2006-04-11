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

package org.opensaml.xml;

import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * A thread-safe marshaller for {@link org.apache.xml.security.utils.ElementProxy}s.
 */
public class ElementProxyMarshaller extends AbstractXMLObjectMarshaller {

    /**
     * {@inheritDoc}
     */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        ElementProxy proxy = (ElementProxy) xmlObject;

        Map<QName, String> attributes = proxy.getUnknownAttributes();
        if (attributes.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<QName, String>> entryItr = attributes.entrySet().iterator();
        String qualifiedName;
        Map.Entry<QName, String> attribute;
        while (entryItr.hasNext()) {
            attribute = entryItr.next();
            if (attribute.getKey().getPrefix() != null) {
                qualifiedName = attribute.getKey().getPrefix() + ":" + attribute.getKey().getLocalPart();
            } else {
                qualifiedName = attribute.getKey().getLocalPart();
            }

            domElement.setAttributeNS(attribute.getKey().getNamespaceURI(), qualifiedName, attribute.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {
        ElementProxy proxy = (ElementProxy) xmlObject;

        if (proxy.getTextContent() != null) {
            XMLHelper.appendTextContent(domElement, proxy.getTextContent());
        }
    }
}