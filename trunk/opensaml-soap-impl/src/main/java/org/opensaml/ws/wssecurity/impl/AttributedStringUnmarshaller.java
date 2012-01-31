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

package org.opensaml.ws.wssecurity.impl;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.QNameSupport;

import org.opensaml.ws.wssecurity.AttributedString;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.XMLObjectSupport;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for instances of {@link AttributedString}.
 */
public class AttributedStringUnmarshaller extends AbstractWSSecurityObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        AttributedString attributedString = (AttributedString) xmlObject;
        
        QName attribQName = 
            QNameSupport.constructQName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getPrefix());
        if (AttributedString.WSU_ID_ATTR_NAME.equals(attribQName)) {
            attributedString.setWSUId(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(attributedString.getUnknownAttributes(), attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processElementContent(XMLObject xmlObject, String elementContent) {
        AttributedString attributedString = (AttributedString) xmlObject;
        attributedString.setValue(elementContent);
    }
    
}
