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
 * Base implementation for XMLObject builders.
 * 
 * <strong>Note:</strong> This class only works with {@link org.opensaml.xml.AbstractXMLObject}s
 */
public abstract class AbstractXMLObjectBuilder<XMLObjectType extends XMLObject> implements XMLObjectBuilder<XMLObjectType> {

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(java.lang.String, java.lang.String, java.lang.String)
     */
    public abstract XMLObjectType buildObject(String namespaceURI, String localName, String namespacePrefix);

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(java.lang.String, java.lang.String, java.lang.String, javax.xml.namespace.QName)
     */
    public XMLObjectType buildObject(String namespaceURI, String localName, String namespacePrefix, QName schemaType) {
        XMLObjectType xmlObject;
        
        xmlObject = buildObject(namespaceURI, localName, namespacePrefix);
        ((AbstractXMLObject)xmlObject).setSchemaType(schemaType);
        
        return xmlObject;
    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(org.w3c.dom.Element)
     */
    public XMLObjectType buildObject(Element element) {
        XMLObjectType xmlObject;
        
        String localName = element.getLocalName();
        String nsURI = element.getNamespaceURI();
        String nsPrefix = element.getPrefix();
        QName schemaType = XMLHelper.getXSIType(element);
        
        xmlObject = buildObject(nsURI, localName, nsPrefix, schemaType);
        
        return xmlObject; 
    }
}