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
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for the empty &lt;wst:Renewing&gt; element.
 * 
 * @see Renewing
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class RenewingUnmarshaller extends AbstractWSTrustObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public RenewingUnmarshaller() {
        super(Renewing.ELEMENT_NAME.getNamespaceURI(),
              Renewing.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Unmarshalls the &lt;wst:OK&gt; and the &lt;Allow&gt; attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute)
            throws UnmarshallingException {
        Renewing renewing= (Renewing) xmlObject;
        String attrName= attribute.getLocalName();
        if (Renewing.OK_ATTR_LOCAL_NAME.equals(attrName)) {
            String value= attribute.getValue();
            Boolean ok= Boolean.parseBoolean(value);
            // TODO: check if need to set false?
            renewing.setOK(ok);
        }
        else if (Renewing.ALLOW_ATTR_LOCAL_NAME.equals(attrName)) {
            String value= attribute.getValue();
            Boolean allow= Boolean.parseBoolean(value);
            // TODO: check if need to set false?
            renewing.setOK(allow);
        }
        else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}
