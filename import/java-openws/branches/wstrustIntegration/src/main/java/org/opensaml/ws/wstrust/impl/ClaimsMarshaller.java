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


import org.opensaml.ws.wstrust.Claims;
import org.opensaml.xml.AbstractExtensibleXMLObjectMarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Marshaller for the Claims element.
 * 
 * @see Claims
 * 
 */
public class ClaimsMarshaller extends AbstractExtensibleXMLObjectMarshaller {

    /**
     * Default constructor.
     * 
     */
    public ClaimsMarshaller() {
        super();
    }

    /**
     * Marshalls the &lt;wst:Dialect&gt; attribute.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement)
            throws MarshallingException {
        Claims claims= (Claims) xmlObject;
        String dialect= claims.getDialect();
        if (dialect != null) {
            Document document= domElement.getOwnerDocument();
            Attr attribute= XMLHelper.constructAttribute(document,
                                                         Claims.DIALECT_ATTR_NAME);
            attribute.setValue(dialect);
            domElement.setAttributeNodeNS(attribute);
        }
        super.marshallAttributes(xmlObject, domElement);
    }

}
