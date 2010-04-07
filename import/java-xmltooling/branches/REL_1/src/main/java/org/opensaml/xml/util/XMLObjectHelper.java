/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.util;

import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLRuntimeException;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * A helper class for working with XMLObjects.
 */
public final class XMLObjectHelper {
    
    /** Class logger. */
    private static final Logger log = LoggerFactory.getLogger(XMLObjectHelper.class);

    /** Constructor. */
    private XMLObjectHelper() { }
    
    /**
     * Clone an XMLObject by brute force:
     * 
     * <p>
     * 1) Marshall the original object if necessary
     * 2) Clone the resulting DOM Element
     * 3) Unmarshall a new XMLObject tree around it.
     * </p>
     * 
     * <p>
     * This method variant is equivalent to <code>cloneXMLObject(originalXMLObject, false).</code>
     * </p>
     * 
     * 
     * @param originalXMLObject the object to be cloned
     * @return a clone of the original object
     * 
     * @throws MarshallingException if original object can not be marshalled
     * @throws UnmarshallingException if cloned object tree can not be unmarshalled
     * 
     * @param <T> the type of object being cloned
     */
    public static <T extends XMLObject> T cloneXMLObject(T originalXMLObject)
            throws MarshallingException, UnmarshallingException {
        return cloneXMLObject(originalXMLObject, false);
    }
    
    /**
     * Clone an XMLObject by brute force:
     * 
     * <p>
     * 1) Marshall the original object if necessary
     * 2) Clone the resulting DOM Element
     * 3) Unmarshall a new XMLObject tree around it.
     * </p>
     * 
     * @param originalXMLObject the object to be cloned
     * @param rootInNewDocument if true the cloned object's cached DOM will be rooted
     *          in a new Document; if false, the original object's underlying DOM is cloned,
     *          but the cloned copy remains unrooted and owned by the original Document
     * @return a clone of the original object
     * 
     * @throws MarshallingException if original object can not be marshalled
     * @throws UnmarshallingException if cloned object tree can not be unmarshalled
     * 
     * @param <T> the type of object being cloned
     */
    public static <T extends XMLObject> T cloneXMLObject(T originalXMLObject, boolean rootInNewDocument)
            throws MarshallingException, UnmarshallingException {
        
        if (originalXMLObject == null) {
            return null;
        }
        
        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(originalXMLObject);
        Element origElement = marshaller.marshall(originalXMLObject);
        
        Element clonedElement = null;
        
        if (rootInNewDocument) {
            try {
                Document newDocument = Configuration.getParserPool().newDocument();
                // Note: importNode copies the node tree and does not modify the source document
                clonedElement = (Element) newDocument.importNode(origElement, true);
                newDocument.appendChild(clonedElement);
            } catch (XMLParserException e) {
                throw new XMLRuntimeException("Error obtaining new Document from parser pool", e);
            }
        } else {
            clonedElement = (Element) origElement.cloneNode(true);
        }
        
        Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(clonedElement);
        T clonedXMLObject = (T) unmarshaller.unmarshall(clonedElement);
        
        return clonedXMLObject;
    }
    
}