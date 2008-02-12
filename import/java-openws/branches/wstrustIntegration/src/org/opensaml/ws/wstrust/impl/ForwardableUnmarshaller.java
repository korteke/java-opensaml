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


import org.opensaml.ws.wstrust.Forwardable;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSBoolean;
import org.opensaml.xml.schema.XSBooleanValue;

/**
 * Unmarshaller for the &lt;wst:Forwardable&gt; element.
 * 
 * @see Forwardable
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class ForwardableUnmarshaller extends AbstractWSTrustObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public ForwardableUnmarshaller() {
        super(Forwardable.ELEMENT_NAME.getNamespaceURI(),
              Forwardable.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Unmarshalls the &lt;wst:Forwardable&gt; element boolean content.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processElementContent(XMLObject xmlObject,
            String elementContent) {
        if (elementContent != null) {
            XSBoolean xsBoolean= (XSBoolean) xmlObject;
            XSBooleanValue value= XSBooleanValue.valueOf(elementContent);
            xsBoolean.setValue(value);
        }
    }

}
