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


import org.opensaml.ws.wssecurity.Password;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * PasswordMarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class PasswordUnmarshaller extends AbstractAttributedIdUnmarshaller {

    // private final Logger log=
    // LoggerFactory.getLogger(PasswordUnmarshaller.class);

    /**
     * Default constructor.
     */
    public PasswordUnmarshaller() {
        super(Password.ELEMENT_NAME.getNamespaceURI(),
              Password.ELEMENT_NAME.getLocalPart());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractWSSecurityObjectUnmarshaller#processAttribute(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Attr)
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute)
            throws UnmarshallingException {
        String attrName= attribute.getLocalName();
        if (Password.TYPE_ATTR_LOCAL_NAME.equals(attrName)) {
            Password password= (Password) xmlObject;
            password.setType(attribute.getValue());
        }
        else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractWSSecurityObjectUnmarshaller#processElementContent(org.opensaml.xml.XMLObject,
     *      java.lang.String)
     */
    @Override
    protected void processElementContent(XMLObject xmlObject,
            String elementContent) {
        if (elementContent != null) {
            Password password= (Password) xmlObject;
            password.setValue(elementContent);
        }
    }

}
