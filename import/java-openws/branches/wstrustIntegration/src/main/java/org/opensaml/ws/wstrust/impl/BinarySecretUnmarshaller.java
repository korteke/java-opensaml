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

import javax.xml.namespace.QName;


import org.opensaml.ws.wstrust.BinarySecret;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSBase64Binary;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for the &lt;wst:BinarySecret&gt; element.
 * 
 * @see BinarySecret
 * 
 */
public class BinarySecretUnmarshaller extends AbstractWSTrustObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public BinarySecretUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the &lt;@Type&gt; and the <code>xs:anyAttribute</code>
     * attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute)
            throws UnmarshallingException {
        String attrName= attribute.getLocalName();
        BinarySecret binarySecret= (BinarySecret) xmlObject;
        if (BinarySecret.TYPE_ATTR_LOCAL_NAME.equals(attrName)) {
            String type= attribute.getValue();
            binarySecret.setType(type);
        }
        else {
            // xs:anyAttribute
            QName attribQName= XMLHelper.constructQName(attribute.getNamespaceURI(),
                                                        attribute.getLocalName(),
                                                        attribute.getPrefix());
            if (attribute.isId()) {
                binarySecret.getUnknownAttributes().registerID(attribQName);
            }
            binarySecret.getUnknownAttributes().put(attribQName,
                                                    attribute.getValue());
        }
    }

    /**
     * Unmarshalls the &lt;wst:BinarySecret&gt; element base64 binary content.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processElementContent(XMLObject xmlObject,
            String elementContent) {
        if (elementContent != null) {
            XSBase64Binary base64= (XSBase64Binary) xmlObject;
            base64.setValue(elementContent);
        }
    }

}
