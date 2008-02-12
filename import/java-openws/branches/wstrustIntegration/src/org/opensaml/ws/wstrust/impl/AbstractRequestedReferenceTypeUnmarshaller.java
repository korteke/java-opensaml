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


import org.opensaml.ws.wssecurity.SecurityTokenReference;
import org.opensaml.ws.wstrust.RequestedAttachedReference;
import org.opensaml.ws.wstrust.RequestedReferenceType;
import org.opensaml.ws.wstrust.RequestedUnattachedReference;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * Abstract unmarshaller for the element of type RequestedReferenceType.
 * 
 * @see RequestedReferenceType
 * @see RequestedAttachedReference
 * @see RequestedUnattachedReference
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public abstract class AbstractRequestedReferenceTypeUnmarshaller extends
        AbstractWSTrustObjectUnmarshaller {

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public AbstractRequestedReferenceTypeUnmarshaller(
            String targetNamespaceURI, String targetLocalName) {
        super(targetNamespaceURI, targetLocalName);
    }

    /**
     * Unmarshalls the &lt;wst:SecurityTokenReference&gt; child element.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        RequestedReferenceType container= (RequestedReferenceType) parentXMLObject;
        if (childXMLObject instanceof SecurityTokenReference) {
            SecurityTokenReference ref= (SecurityTokenReference) childXMLObject;
            container.setSecurityTokenReference(ref);
        }
        else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
