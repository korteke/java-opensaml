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

package org.opensaml.xml.io;

import org.opensaml.xml.DOMCachingXMLObject;
import org.opensaml.xml.XMLObject;
import org.w3c.dom.Element;

/**
 * A thread-safe unmarshaller that extends {@link org.opensaml.xml.io.AbstractXMLObjectMarshaller} by adding support for
 * DOM caching.
 */
public abstract class AbstractDOMCachingXMLObjectUnmarshaller extends AbstractXMLObjectUnmarshaller {

    public AbstractDOMCachingXMLObjectUnmarshaller() {
        super();
    }

    protected AbstractDOMCachingXMLObjectUnmarshaller(String targetNamespaceURI, String targetLocalName)
            throws IllegalArgumentException {
        super(targetNamespaceURI, targetLocalName);
    }

    public XMLObject unmarshall(Element domElement) throws UnmarshallingException {
        DOMCachingXMLObject dcXMLObject = (DOMCachingXMLObject) super.unmarshall(domElement);
        dcXMLObject.setDOM(domElement);

        return dcXMLObject;
    }
}