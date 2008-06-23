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

package org.opensaml.ws.wstrust.impl;

import org.opensaml.ws.wstrust.RequestSecurityTokenType;
import org.opensaml.xml.AbstractExtensibleXMLObjectMarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * AbstractRequestSecurityTokenTypeMarshaller is an abstract marshaller for element of type
 * {@link RequestSecurityTokenType}.
 * 
 * @see RequestSecurityTokenType
 * 
 */
public abstract class AbstractRequestSecurityTokenTypeMarshaller extends AbstractExtensibleXMLObjectMarshaller {

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public AbstractRequestSecurityTokenTypeMarshaller() {
        super();
    }

    /**
     * Marshalls the &lt;@wsu:Id&gt; and the &lt;@Context&gt; attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        RequestSecurityTokenType message = (RequestSecurityTokenType) xmlObject;
        String id = message.getId();
        if (id != null) {
            Document document = domElement.getOwnerDocument();
            Attr attribute = XMLHelper.constructAttribute(document, RequestSecurityTokenType.ID_ATTR_NAME);
            attribute.setValue(id);
            domElement.setAttributeNodeNS(attribute);
        }
        String context = message.getContext();
        if (context != null) {
            Document document = domElement.getOwnerDocument();
            Attr attribute = XMLHelper.constructAttribute(document, RequestSecurityTokenType.CONTEXT_ATTR_NAME);
            attribute.setValue(context);
            domElement.setAttributeNodeNS(attribute);
        }
        // marshall xs:anyAttribute
        super.marshallAttributes(xmlObject, domElement);
    }

}
