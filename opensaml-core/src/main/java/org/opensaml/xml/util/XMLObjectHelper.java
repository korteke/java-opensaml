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

package org.opensaml.xml.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.opensaml.util.ObjectSupport;
import org.opensaml.util.StringSupport;
import org.opensaml.util.xml.AttributeSupport;
import org.opensaml.util.xml.QNameSupport;
import org.opensaml.util.xml.SerializeSupport;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLRuntimeException;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * A helper class for working with XMLObjects.
 */
public final class XMLObjectHelper {
    
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
    
    /**
     * Unmarshall a Document from an InputSteam.
     * 
     * @param parserPool the ParserPool instance to use
     * @param inputStream the InputStream to unmarshall
     * @return the unmarshalled XMLObject
     * @throws XMLParserException if there is a problem parsing the input data
     * @throws UnmarshallingException if there is a problem unmarshalling the parsed DOM
     */
    public static XMLObject unmarshallFromInputStream(ParserPool parserPool, InputStream inputStream)
            throws XMLParserException, UnmarshallingException {
        Logger log = getLogger();
        log.debug("Parsing InputStream into DOM document");

        Document messageDoc = parserPool.parse(inputStream);
        Element messageElem = messageDoc.getDocumentElement();

        if (log.isTraceEnabled()) {
            log.trace("Resultant DOM message was:");
            log.trace(SerializeSupport.nodeToString(messageElem));
        }

        log.debug("Unmarshalling DOM parsed from InputStream");
        Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(messageElem);
        if (unmarshaller == null) {
            log.error("Unable to unmarshall InputStream, no unmarshaller registered for element "
                    + QNameSupport.getNodeQName(messageElem));
            throw new UnmarshallingException(
                    "Unable to unmarshall InputStream, no unmarshaller registered for element "
                            + QNameSupport.getNodeQName(messageElem));
        }

        XMLObject message = unmarshaller.unmarshall(messageElem);

        log.debug("InputStream succesfully unmarshalled");
        return message;
    }
    
    /**
     * Unmarshall a Document from a Reader.
     * 
     * @param parserPool the ParserPool instance to use
     * @param reader the Reader to unmarshall
     * @return the unmarshalled XMLObject
     * @throws XMLParserException if there is a problem parsing the input data
     * @throws UnmarshallingException if there is a problem unmarshalling the parsed DOM
     */
    public static XMLObject unmarshallFromReader(ParserPool parserPool, Reader reader)
            throws XMLParserException, UnmarshallingException {
        Logger log = getLogger();
        log.debug("Parsing Reader into DOM document");
        

        Document messageDoc = parserPool.parse(reader);
        Element messageElem = messageDoc.getDocumentElement();

        if (log.isTraceEnabled()) {
            log.trace("Resultant DOM message was:");
            log.trace(SerializeSupport.nodeToString(messageElem));
        }

        log.debug("Unmarshalling DOM parsed from Reader");
        Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(messageElem);
        if (unmarshaller == null) {
            log.error("Unable to unmarshall Reader, no unmarshaller registered for element "
                    + QNameSupport.getNodeQName(messageElem));
            throw new UnmarshallingException(
                    "Unable to unmarshall Reader, no unmarshaller registered for element "
                            + QNameSupport.getNodeQName(messageElem));
        }

        XMLObject message = unmarshaller.unmarshall(messageElem);

        log.debug("Reader succesfully unmarshalled");
        return message;
    }

    /**
     * Marshall an XMLObject.  If the XMLObject already has a cached DOM via {@link XMLObject#getDOM()},
     * that Element will be returned.  Otherwise the object will be fully marshalled and that Element returned.
     * 
     * @param xmlObject the XMLObject to marshall
     * @return the marshalled Element
     * @throws MarshallingException if there is a problem marshalling the XMLObject
     */
    public static Element marshall(XMLObject xmlObject) throws MarshallingException {
        Logger log = getLogger();
        log.debug("Marshalling XMLObject");
        
        if (xmlObject.getDOM() != null) {
            log.debug("XMLObject already had cached DOM, returning that element");
            return xmlObject.getDOM();
        }

        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(xmlObject);
        if (marshaller == null) {
            log.error("Unable to marshall XMLOBject, no marshaller registered for object: "
                    + xmlObject.getElementQName());
        }
        
        Element messageElem = marshaller.marshall(xmlObject);
        
        if (log.isTraceEnabled()) {
            log.trace("Marshalled XMLObject into DOM:");
            log.trace(SerializeSupport.nodeToString(messageElem));
        }
        
        return messageElem;
    }
    
    /**
     * Marshall an XMLObject to an OutputStream.
     * 
     * @param xmlObject the XMLObject to marshall
     * @param outputStream the OutputStream to which to marshall
     * @throws MarshallingException if there is a problem marshalling the object
     */
    public static void marshallToOutputStream(XMLObject xmlObject, OutputStream outputStream) 
            throws MarshallingException {
        Element element = marshall(xmlObject);
        SerializeSupport.writeNode(element, outputStream);
    }
    
    /**
     * Get the namespace URI bound to the specified prefix within the scope of the specified
     * XMLObject.
     *
     * @param xmlObject the XMLObject from which to search
     * @param prefix the prefix to search
     * @return the namespace URI bound to the prefix, or none if not found
     */
    public static String lookupNamespaceURI(XMLObject xmlObject, String prefix) {
        XMLObject current = xmlObject;
        
        while (current != null) {
            for (Namespace ns : current.getNamespaces()) {
                if (ObjectSupport.equals(ns.getNamespacePrefix(), prefix)) {
                    return ns.getNamespaceURI();
                }
            }
            current = current.getParent();
        }
        
        return null;
    }
    
    /**
     * Get the prefix bound to the specified namespace URI within the scope of the specified
     * XMLObject.
     *
     * @param xmlObject the XMLObject from which to search
     * @param namespaceURI the namespace URI to search
     * @return the prefix bound to the namespace URI, or none if not found
     */
    public static String lookupNamespacePrefix(XMLObject xmlObject, String namespaceURI) {
        XMLObject current = xmlObject;
        
        while (current != null) {
            for (Namespace ns : current.getNamespaces()) {
                if (ObjectSupport.equals(ns.getNamespaceURI(), namespaceURI)) {
                    return ns.getNamespacePrefix();
                }
            }
            current = current.getParent();
        }
        
        return null;
    }
    
    /**
     * Get an SLF4J Logger.
     * 
     * @return a Logger instance
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(XMLObjectHelper.class);
    }

    /**
     * Marshall an attribute name and value to a DOM Element. This is particularly useful for attributes whose names
     * appear in namespace-qualified form.
     * 
     * @param attributeName the attribute name in QName form
     * @param attributeValues the attribute values
     * @param domElement the target element to which to marshall
     * @param isIDAttribute flag indicating whether the attribute being marshalled should be handled as an ID-typed
     *            attribute
     */
    public static void marshallAttribute(QName attributeName, List<String> attributeValues, Element domElement,
            boolean isIDAttribute) {
        marshallAttribute(attributeName, StringSupport.listToStringValue(attributeValues, " "), domElement,
                isIDAttribute);
    }

    /**
     * Marshall an attribute name and value to a DOM Element. This is particularly useful for attributes whose names
     * appear in namespace-qualified form.
     * 
     * @param attributeName the attribute name in QName form
     * @param attributeValue the attribute value
     * @param domElement the target element to which to marshall
     * @param isIDAttribute flag indicating whether the attribute being marshalled should be handled as an ID-typed
     *            attribute
     */
    public static void marshallAttribute(QName attributeName, String attributeValue, Element domElement,
            boolean isIDAttribute) {
        Document document = domElement.getOwnerDocument();
        Attr attribute = AttributeSupport.constructAttribute(document, attributeName);
        attribute.setValue(attributeValue);
        domElement.setAttributeNodeNS(attribute);
        if (isIDAttribute) {
            domElement.setIdAttributeNode(attribute, true);
        }
    }

    /**
     * Marshall the attributes represented by the indicated AttributeMap into the indicated DOM Element.
     * 
     * @param attributeMap the AttributeMap
     * @param domElement the target Element
     */
    public static void marshallAttributeMap(AttributeMap attributeMap, Element domElement) {
        Document document = domElement.getOwnerDocument();
        Attr attribute = null;
        for (Entry<QName, String> entry : attributeMap.entrySet()) {
            attribute = AttributeSupport.constructAttribute(document, entry.getKey());
            attribute.setValue(entry.getValue());
            domElement.setAttributeNodeNS(attribute);
            if (Configuration.isIDAttribute(entry.getKey()) || attributeMap.isIDAttribute(entry.getKey())) {
                domElement.setIdAttributeNode(attribute, true);
            }
        }
    }

    /**
     * Unmarshall a DOM Attr to an AttributeMap.
     * 
     * @param attributeMap the target AttributeMap
     * @param attribute the target DOM Attr
     */
    public static void unmarshallToAttributeMap(AttributeMap attributeMap, Attr attribute) {
        QName attribQName = QNameSupport.constructQName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute
                .getPrefix());
        attributeMap.put(attribQName, attribute.getValue());
        if (attribute.isId() || Configuration.isIDAttribute(attribQName)) {
            attributeMap.registerID(attribQName);
        }
    }
    
}