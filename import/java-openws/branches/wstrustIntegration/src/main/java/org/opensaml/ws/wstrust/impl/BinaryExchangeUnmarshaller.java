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

import org.opensaml.ws.wstrust.BinaryExchange;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSBase64Binary;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for the &lt;wst:BinaryExchange&gt; element.
 * 
 * @see BinaryExchange
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class BinaryExchangeUnmarshaller extends AbstractWSTrustObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public BinaryExchangeUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the &lt;wst:BinaryExchange&gt; element base64 binary content.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processElementContent(XMLObject xmlObject, String elementContent) {
        if (elementContent != null) {
            XSBase64Binary base64 = (XSBase64Binary) xmlObject;
            base64.setValue(elementContent);
        }
    }

    /**
     * Unmarshalls the &lt;@ValueType&gt;, the &lt;@EncodingType&gt; and the <code>xs:anyAttribute</code> attributes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        BinaryExchange binaryExchange = (BinaryExchange) xmlObject;
        String attrName = attribute.getLocalName();
        if (BinaryExchange.VALUE_TYPE_ATTR_LOCAL_NAME.equals(attrName)) {
            String valueType = attribute.getValue();
            binaryExchange.setValueType(valueType);
        } else if (BinaryExchange.ENCODING_TYPE_ATTR_LOCAL_NAME.equals(attrName)) {
            String encodingType = attribute.getValue();
            binaryExchange.setEncodingType(encodingType);
        } else {
            // xs:anyAttribute
            QName attrQName = XMLHelper.constructQName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute
                    .getPrefix());
            if (attribute.isId()) {
                binaryExchange.getUnknownAttributes().registerID(attrQName);
            }
            binaryExchange.getUnknownAttributes().put(attrQName, attribute.getValue());
        }
    }

}
