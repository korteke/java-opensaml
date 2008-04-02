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


import org.opensaml.ws.wstrust.Code;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSURI;

/**
 * Unmarshaller for the &lt;wst:Code&gt; element.
 * 
 * @see Code
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class CodeUnmarshaller extends AbstractWSTrustObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public CodeUnmarshaller() {
        super(Code.ELEMENT_NAME.getNamespaceURI(),
              Code.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Unmarshalls the &lt;wst:Code&gt; element content.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processElementContent(XMLObject xmlObject,
            String elementContent) {
        if (elementContent != null) {
            XSURI xsUri= (XSURI) xmlObject;
            xsUri.setValue(elementContent);
        }
    }

}
