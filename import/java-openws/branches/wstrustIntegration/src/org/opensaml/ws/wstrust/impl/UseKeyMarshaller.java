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


import org.opensaml.ws.wstrust.UseKey;
import org.opensaml.xml.AbstractElementExtensibleXMLObjectMarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Marshaller for the UseKey element.
 * 
 * @see UseKey
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class UseKeyMarshaller extends
        AbstractElementExtensibleXMLObjectMarshaller {

    /**
     * Default constructor.
     */
    public UseKeyMarshaller() {
        super(UseKey.ELEMENT_NAME.getNamespaceURI(),
              UseKey.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Marshalls the &lt;wst:Sig&gt; attribute.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement)
            throws MarshallingException {
        UseKey useKey= (UseKey) xmlObject;
        String sig= useKey.getSig();
        if (sig != null) {
            Document document= domElement.getOwnerDocument();
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         UseKey.SIG_ATTR_NAME);
            attribute.setValue(sig);
            domElement.setAttributeNodeNS(attribute);
        }
        super.marshallAttributes(xmlObject, domElement);
    }

}
