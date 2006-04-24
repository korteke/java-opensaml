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

import java.util.List;

import org.opensaml.common.impl.AbstractSAMLObject;
import org.opensaml.saml1.core.Audience;
import org.opensaml.xml.XMLObject;

/**
 * Concrete class implementation of {@link org.opensaml.saml1.core.Audience}
 */
public class AudienceImpl extends AbstractSAMLObject implements Audience {

    /** String to hold the URI */
    private String uri;

    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AudienceImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * @see org.opensaml.saml1.core.Audience#getUri()
     */
    public String getUri() {
        return uri;
    }

    /*
     * @see org.opensaml.saml1.core.Audience#setUri()
     */
    public void setUri(String uri) {

        this.uri = prepareForAssignment(this.uri, uri);
    }

    /*
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        return null;
    }
}