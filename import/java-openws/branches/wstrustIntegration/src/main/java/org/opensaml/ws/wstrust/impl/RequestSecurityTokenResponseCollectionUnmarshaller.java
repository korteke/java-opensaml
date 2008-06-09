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

import java.util.List;

import javax.xml.namespace.QName;


import org.opensaml.ws.wstrust.RequestSecurityTokenResponse;
import org.opensaml.ws.wstrust.RequestSecurityTokenResponseCollection;
import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;

/**
 * RequestSecurityTokenResponseCollectionUnmarshaller
 * 
 * @see RequestSecurityTokenResponseCollection
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class RequestSecurityTokenResponseCollectionUnmarshaller extends
        AbstractWSTrustObjectUnmarshaller {

    /**
     * Default constructor.
     */
    public RequestSecurityTokenResponseCollectionUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the {@link RequestSecurityTokenResponse} child elements and
     * add them to the RSTR list.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        if (childXMLObject instanceof RequestSecurityTokenResponse) {
            RequestSecurityTokenResponseCollection rstrc= (RequestSecurityTokenResponseCollection) parentXMLObject;
            List<RequestSecurityTokenResponse> rstrs= rstrc.getRequestSecurityTokenResponses();
            RequestSecurityTokenResponse rstr= (RequestSecurityTokenResponse) childXMLObject;
            rstrs.add(rstr);
        }
        else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

    /**
     * Unmarshalls the <code>xs:anyAttribute</code> attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute)
            throws UnmarshallingException {
        AttributeExtensibleXMLObject anyAttribute= (AttributeExtensibleXMLObject) xmlObject;
        QName attribQName= XMLHelper.constructQName(attribute.getNamespaceURI(),
                                                    attribute.getLocalName(),
                                                    attribute.getPrefix());
        if (attribute.isId()) {
            anyAttribute.getUnknownAttributes().registerID(attribQName);
        }
        anyAttribute.getUnknownAttributes().put(attribQName,
                                                attribute.getValue());
    }

}
