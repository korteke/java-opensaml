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


import org.opensaml.ws.wstrust.Renewing;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Marshaller for the Renewing element.
 * 
 * @see Renewing
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class RenewingMarshaller extends AbstractWSTrustObjectMarshaller {

    /**
     * Default constructor.
     */
    public RenewingMarshaller() {
        super(Renewing.ELEMENT_NAME.getNamespaceURI(),
              Renewing.ELEMENT_NAME.getLocalPart());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.impl.AbstractWSTrustObjectMarshaller#marshallAttributes(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement)
            throws MarshallingException {
        Document document= domElement.getOwnerDocument();
        Renewing renewing= (Renewing) xmlObject;
        Boolean ok= renewing.getOK();
        if (ok != null) {
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         Renewing.OK_ATTR_NAME);
            attribute.setValue(ok.toString());
            domElement.setAttributeNodeNS(attribute);
        }
        Boolean allow= renewing.getAllow();
        if (allow != null) {
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         Renewing.ALLOW_ATTR_NAME);
            attribute.setValue(allow.toString());
            domElement.setAttributeNodeNS(attribute);
        }
        super.marshallAttributes(xmlObject, domElement);
    }

}
