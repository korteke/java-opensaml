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

package org.opensaml.xml;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * Builder of {@link org.opensaml.xml.ElementProxy} objects.
 */
public class ElementProxyBuilder implements XMLObjectBuilder {

    /**
     * Constructor
     */
    public ElementProxyBuilder() {

    }
    
    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(java.lang.String, java.lang.String, java.lang.String)
     */
    public XMLObject buildObject(String namespaceURI, String localName, String namespacePrefix){
        return new ElementProxy(namespaceURI, localName, namespacePrefix);
    }
    
    /**
     * Creates an XMLObject with a given fully qualified name and schema type.
     * 
     * @param namespaceURI the URI of the namespace the Element represented by this XMLObject will be in
     * @param localName the local name of the Element represented by this XMLObject
     * @param namespacePrefix the namespace prefix of the Element represented by this XMLObject
     * @param schemaType the schema type of the Element represented by this XMLObject 
     * 
     * @return the constructed XMLObject
     */
    public XMLObject buildObject(String namespaceURI, String localName, String namespacePrefix, QName schemaType){
        ElementProxy eProxy = new ElementProxy(namespaceURI, localName, namespacePrefix);
        eProxy.setSchemaType(schemaType);
        
        return eProxy;
    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(org.w3c.dom.Element)
     */
    public XMLObject buildObject(Element element) {
        ElementProxy eProxy;
        
        String localName = element.getLocalName();
        String nsURI = element.getNamespaceURI();
        String nsPrefix = element.getPrefix();
        
        eProxy = new ElementProxy(nsURI, localName, nsPrefix);
        eProxy.setSchemaType(XMLHelper.getXSIType(element));
        
        return eProxy; 
    }
}