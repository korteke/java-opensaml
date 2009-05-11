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

import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.opensaml.ws.wssecurity.UsernameToken;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * UsernameTokenMarshaller.
 */
public class UsernameTokenMarshaller extends AbstractWSSecurityObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        UsernameToken usernameToken = (UsernameToken) xmlObject;
        Attr attribute;
        Document document = domElement.getOwnerDocument();
        
        for (Entry<QName, String> entry : usernameToken.getUnknownAttributes().entrySet()) {
            attribute = XMLHelper.constructAttribute(document, entry.getKey());
            attribute.setValue(entry.getValue());
            domElement.setAttributeNodeNS(attribute);
            if (Configuration.isIDAttribute(entry.getKey())
                    || usernameToken.getUnknownAttributes().isIDAttribute(entry.getKey())) {
                attribute.getOwnerElement().setIdAttributeNode(attribute, true);
            }
        }
        
        if (!DatatypeHelper.isEmpty(usernameToken.getId())) {
            attribute = XMLHelper.constructAttribute(document, UsernameToken.ID_ATTR_NAME);
            attribute.setValue(usernameToken.getId());
            domElement.setAttributeNodeNS(attribute);
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        }
        
        super.marshallAttributes(xmlObject, domElement);
    }

}
