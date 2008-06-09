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

import java.util.Arrays;


import org.opensaml.ws.wssecurity.AttributedId;
import org.opensaml.ws.wssecurity.Embedded;
import org.opensaml.ws.wssecurity.KeyIdentifier;
import org.opensaml.ws.wssecurity.Reference;
import org.opensaml.ws.wssecurity.SecurityTokenReference;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * SecurityTokenReferenceUnmarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class SecurityTokenReferenceUnmarshaller extends
        AbstractAttributedIdUnmarshaller {

    /**
     * Default constructor.
     */
    public SecurityTokenReferenceUnmarshaller() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractWSSecurityObjectUnmarshaller#processChildElement(org.opensaml.xml.XMLObject,
     *      org.opensaml.xml.XMLObject)
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        SecurityTokenReference securityTokenReference= (SecurityTokenReference) parentXMLObject;
        if (childXMLObject instanceof Reference) {
            securityTokenReference.setReference((Reference) childXMLObject);
        }
        else if (childXMLObject instanceof KeyIdentifier) {
            securityTokenReference.setKeyIdentifier((KeyIdentifier) childXMLObject);
        }
        else if (childXMLObject instanceof Embedded) {
            securityTokenReference.setEmbedded((Embedded) childXMLObject);
        }
        else {
            // unmarshalls xs:any element
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractIdUnmarshaller#processAttribute(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Attr)
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute)
            throws UnmarshallingException {
        String attrName= attribute.getLocalName();
        if (AttributedId.ID_ATTR_LOCAL_NAME.equals(attrName)) {
            AttributedId id= (AttributedId) xmlObject;
            String attrValue= attribute.getValue();
            id.setId(attrValue);
        }
        else if (SecurityTokenReference.TOKEN_TYPE_ATTR_NAME.equals(attrName)) {
            SecurityTokenReference securityTokenReference= (SecurityTokenReference) xmlObject;
            String attrValue= attribute.getValue();
            securityTokenReference.setTokenType(attrValue);
        }
        else if (SecurityTokenReference.USAGE_ATTR_NAME.equals(attrName)) {
            SecurityTokenReference securityTokenReference= (SecurityTokenReference) xmlObject;
            String attrValue= attribute.getValue();
            String usages[]= attrValue.split(" ");
            securityTokenReference.setUsages(Arrays.asList(usages));
        }
        else {
            // unmarshall xs:anyAttribute
            super.processAttribute(xmlObject, attribute);
        }
    }

}
