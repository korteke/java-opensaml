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

import org.opensaml.ws.wssecurity.AttributedValueType;
import org.opensaml.xml.AbstractExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Attr;

/**
 * EmbeddedUnmarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class EmbeddedUnmarshaller extends AbstractExtensibleXMLObjectUnmarshaller {

    /**
     * Default constructor.
     */
    public EmbeddedUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the &lt;@ValueType&gt; attribute.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        String attrName = attribute.getLocalName();
        if (AttributedValueType.VALUE_TYPE_ATTR_LOCAL_NAME.equals(attrName)) {
            AttributedValueType valueType = (AttributedValueType) xmlObject;
            String attrValue = attribute.getValue();
            valueType.setValueType(attrValue);
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}
