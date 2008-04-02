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


import org.opensaml.ws.wssecurity.Created;
import org.opensaml.ws.wssecurity.Expires;
import org.opensaml.ws.wstrust.Lifetime;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * Unmarshaller for the &lt;wst:Lifetime&gt; element.
 * 
 * @see Lifetime
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class LifetimeUnmarshaller extends AbstractWSTrustObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public LifetimeUnmarshaller() {
        super(Lifetime.ELEMENT_NAME.getNamespaceURI(),
              Lifetime.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Unmarshalls the &lt;wsu:Created&gt; and the &lt;wsu:Expires&gt; child
     * elements.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        Lifetime container= (Lifetime) parentXMLObject;
        if (childXMLObject instanceof Created) {
            Created created= (Created) childXMLObject;
            container.setCreated(created);
        }
        else if (childXMLObject instanceof Expires) {
            Expires expires= (Expires) childXMLObject;
            container.setExpires(expires);
        }
        else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
