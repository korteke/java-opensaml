/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
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

package org.opensaml.xmlsec.encryption.impl;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.encryption.AlgorithmIdentifierType;
import org.opensaml.xmlsec.encryption.Parameters;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.xmlsec.encryption.AlgorithmIdentifierType} objects.
 */
public class AlgorithmIdentifierTypeUnmarshaller extends AbstractXMLEncryptionUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        AlgorithmIdentifierType algoIdType = (AlgorithmIdentifierType) xmlObject;

        if (attribute.getLocalName().equals(AlgorithmIdentifierType.ALGORITHM_ATTRIB_NAME)) {
            algoIdType.setAlgorithm(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentXMLObject, XMLObject childXMLObject)
            throws UnmarshallingException {
        AlgorithmIdentifierType algoIdType = (AlgorithmIdentifierType) parentXMLObject;
        
        QName childQName = childXMLObject.getElementQName();
        if (childQName.getLocalPart().equals(Parameters.DEFAULT_ELEMENT_LOCAL_NAME)
                && childQName.getNamespaceURI().equals(EncryptionConstants.XMLENC11_NS)) {
            algoIdType.setParameters(childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
