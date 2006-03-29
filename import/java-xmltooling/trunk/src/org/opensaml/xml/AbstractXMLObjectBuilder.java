/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */
package org.opensaml.xml;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 *
 */
public abstract class AbstractXMLObjectBuilder implements XMLObjectBuilder {

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(java.lang.String, java.lang.String, java.lang.String)
     */
    public abstract AbstractXMLObject buildObject(String namespaceURI, String localName, String namespacePrefix);

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(java.lang.String, java.lang.String, java.lang.String, javax.xml.namespace.QName)
     */
    public AbstractXMLObject buildObject(String namespaceURI, String localName, String namespacePrefix, QName schemaType) {
        AbstractXMLObject xmlObject;
        
        xmlObject = buildObject(namespaceURI, localName, namespacePrefix);
        xmlObject.setSchemaType(schemaType);
        
        return xmlObject;
    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(org.w3c.dom.Element)
     */
    public AbstractXMLObject buildObject(Element element) {
        AbstractXMLObject xmlObject;
        
        String localName = element.getLocalName();
        String nsURI = element.getNamespaceURI();
        String nsPrefix = element.getPrefix();
        QName schemaType = XMLHelper.getXSIType(element);
        
        xmlObject = buildObject(nsURI, localName, nsPrefix, schemaType);
        
        return xmlObject; 
    }
}