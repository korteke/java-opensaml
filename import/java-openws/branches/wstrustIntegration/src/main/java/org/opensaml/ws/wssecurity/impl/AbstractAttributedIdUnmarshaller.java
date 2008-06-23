/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.wssecurity.impl;

import javax.xml.namespace.QName;

import org.opensaml.ws.wssecurity.AttributedId;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * AbstractAttributedIdUnmarshaller
 * 
 */
public abstract class AbstractAttributedIdUnmarshaller extends AbstractWSSecurityObjectUnmarshaller {

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    protected AbstractAttributedIdUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the &lt;@wsu:Id&gt; attribute.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        String attrNamespace = attribute.getNamespaceURI();
        String attrName = attribute.getLocalName();
        QName attrQName = new QName(attrNamespace, attrName);
        if (AttributedId.ID_ATTR_NAME.equals(attrQName)) {
            AttributedId attributedId = (AttributedId) xmlObject;
            String attrValue = attribute.getValue();
            attributedId.setId(attrValue);
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}
