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

import org.opensaml.ws.wssecurity.Created;
import org.opensaml.ws.wssecurity.Iteration;
import org.opensaml.ws.wssecurity.Nonce;
import org.opensaml.ws.wssecurity.Password;
import org.opensaml.ws.wssecurity.Salt;
import org.opensaml.ws.wssecurity.Username;
import org.opensaml.ws.wssecurity.UsernameToken;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;

/**
 * UsernameUnmarshaller.
 */
public class UsernameTokenUnmarshaller extends AbstractWSSecurityObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        UsernameToken token = (UsernameToken) parentXMLObject;
        if (childXMLObject instanceof Username) {
            token.setUsername((Username) childXMLObject);
        } else if (childXMLObject instanceof Password) {
            token.setPassword((Password) childXMLObject);
        } else if (childXMLObject instanceof Created) {
            token.setCreated((Created) childXMLObject);
        } else if (childXMLObject instanceof Nonce) {
            token.setNonce((Nonce) childXMLObject);
        } else if (childXMLObject instanceof Salt) {
            token.setSalt((Salt) childXMLObject);
        } else if (childXMLObject instanceof Iteration) {
            token.setIteration((Iteration) childXMLObject);
        } else {
            token.getUnknownXMLObjects().add(childXMLObject);
        }
    }

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        UsernameToken token = (UsernameToken) xmlObject;
        
        //TODO - fix this - should be based on QName, not local name
        if (attribute.getLocalName().equals(UsernameToken.ID_ATTR_LOCAL_NAME)) {
            token.setId(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else {
            QName attribQName = 
                XMLHelper.constructQName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getPrefix());
            if (attribute.isId()) {
                token.getUnknownAttributes().registerID(attribQName);
            }
            token.getUnknownAttributes().put(attribQName, attribute.getValue());
        }
    }

}
