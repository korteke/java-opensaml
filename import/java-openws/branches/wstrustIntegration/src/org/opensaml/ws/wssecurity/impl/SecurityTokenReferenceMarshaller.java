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

import java.util.List;


import org.opensaml.ws.wssecurity.AttributedId;
import org.opensaml.ws.wssecurity.AttributedTokenType;
import org.opensaml.ws.wssecurity.SecurityTokenReference;
import org.opensaml.xml.AbstractExtensibleXMLObjectMarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * SecurityTokenReferenceMarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class SecurityTokenReferenceMarshaller extends
        AbstractExtensibleXMLObjectMarshaller {

    /**
     * Default constructor.
     */
    public SecurityTokenReferenceMarshaller() {
        super(SecurityTokenReference.ELEMENT_NAME.getNamespaceURI(),
              SecurityTokenReference.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Marshalls the &lt;wsu:Id&gt;, the &lt;wsse:Usage&gt; and the
     * &lt;wsse11:TokenType&gt; attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement)
            throws MarshallingException {
        Document document= domElement.getOwnerDocument();
        AttributedId attributedId= (AttributedId) xmlObject;
        String id= attributedId.getId();
        if (id != null) {
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         AttributedId.ID_ATTR_NAME);
            attribute.setValue(id);
            domElement.setAttributeNodeNS(attribute);
            // TODO: check if needed???
            // domElement.setIdAttributeNode(attribute,true);
        }
        AttributedTokenType tokenTyped= (AttributedTokenType) xmlObject;
        String tokenType= tokenTyped.getTokenType();
        if (tokenType != null) {
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         AttributedTokenType.TOKEN_TYPE_ATTR_NAME);
            attribute.setValue(tokenType);
            domElement.setAttributeNode(attribute);
        }
        SecurityTokenReference securityTokenReference= (SecurityTokenReference) xmlObject;
        List<String> usages= securityTokenReference.getUsages();
        if (!usages.isEmpty()) {
            StringBuffer sb= new StringBuffer();
            for (String usage : usages) {
                sb.append(" ").append(usage);
            }
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         SecurityTokenReference.USAGE_ATTR_NAME);
            String usagesList= sb.toString().trim();
            attribute.setValue(usagesList);
            domElement.setAttributeNode(attribute);
        }
        super.marshallAttributes(xmlObject, domElement);
    }

}
