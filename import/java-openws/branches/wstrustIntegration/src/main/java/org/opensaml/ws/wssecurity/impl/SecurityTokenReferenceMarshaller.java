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

import org.opensaml.ws.wssecurity.SecurityTokenReference;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * SecurityTokenReferenceMarshaller.
 * 
 */
public class SecurityTokenReferenceMarshaller extends AbstractWSSecurityObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        SecurityTokenReference str = (SecurityTokenReference) xmlObject;
        
        if (!DatatypeHelper.isEmpty(str.getId())) {
            XMLHelper.marshallAttribute(SecurityTokenReference.ID_ATTR_NAME, str.getId(), domElement, true);
        }
        
        List<String> usages = str.getUsages();
        if (usages != null && ! usages.isEmpty()) {
            XMLHelper.marshallAttribute(SecurityTokenReference.USAGE_ATTR_NAME, XMLHelper.listToStringValue(usages),
                    domElement, false);
        }
        
        XMLHelper.marshallAttributeMap(str.getUnknownAttributes(), domElement);
    }

}
