/*
 * Licensed to the University Corporation for Advanced Internet Development, Inc.
 * under one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.mock;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * Marshaller for {@link org.opensaml.xml.mock.SimpleXMLObject} objects.
 */
public class SignableSimpleXMLObjectMarshaller extends AbstractXMLObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        SignableSimpleXMLObject simpleXMLObject = (SignableSimpleXMLObject) xmlObject;

        if (simpleXMLObject.getId() != null) {
            domElement.setAttributeNS(null, SignableSimpleXMLObject.ID_ATTRIB_NAME, simpleXMLObject.getId());
            domElement.setIdAttributeNS(null, SignableSimpleXMLObject.ID_ATTRIB_NAME, true);
        }
        
        XMLHelper.marshallAttributeMap(simpleXMLObject.getUnknownAttributes(), domElement);

    }

    /** {@inheritDoc} */
    protected void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException {
        SignableSimpleXMLObject simpleXMLObject = (SignableSimpleXMLObject) xmlObject;

        if (simpleXMLObject.getValue() != null) {
            domElement.setTextContent(simpleXMLObject.getValue());
        }
    }
}