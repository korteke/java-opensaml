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

import org.opensaml.ws.wssecurity.AttributedId;
import org.opensaml.ws.wssecurity.Created;
import org.opensaml.ws.wssecurity.Iteration;
import org.opensaml.ws.wssecurity.Nonce;
import org.opensaml.ws.wssecurity.Password;
import org.opensaml.ws.wssecurity.Salt;
import org.opensaml.ws.wssecurity.Username;
import org.opensaml.ws.wssecurity.UsernameToken;
import org.opensaml.xml.AbstractExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * UsernameUnmarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class UsernameTokenUnmarshaller extends AbstractExtensibleXMLObjectUnmarshaller {

    /**
     * Default constructor.
     */
    public UsernameTokenUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the &lt;wsse:Username&gt;, the &lt;wsse:Password&gt;, the &lt;wsu:Created&gt;, the
     * &lt;wsse:Nonce&gt;, the &lt;wsse11:Salt&gt; and the &lt;wsse11:Iteration&gt; child elements.
     * <p>
     * {@inheritDoc}
     */
    @Override
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
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

    /**
     * Unmarshalls the &lt;@wsu:Id&gt; attribute.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        String attrName = attribute.getLocalName();
        if (AttributedId.ID_ATTR_LOCAL_NAME.equals(attrName)) {
            AttributedId attributedId = (AttributedId) xmlObject;
            String attrValue = attribute.getValue();
            attributedId.setId(attrValue);
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}
