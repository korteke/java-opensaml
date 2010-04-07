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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.opensaml.xml.Configuration;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * A helper class for working with XMLObjects.
 */
public final class XMLObjectHelper {
    
    /** Class logger. */
    private static final Logger LOG = LoggerFactory.getLogger(XMLObjectHelper.class);

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
        LOG.debug("Parsing InputStream into DOM document");

        Document messageDoc = parserPool.parse(inputStream);
        Element messageElem = messageDoc.getDocumentElement();

        if (LOG.isTraceEnabled()) {
            LOG.trace("Resultant DOM message was:");
            LOG.trace(XMLHelper.nodeToString(messageElem));
        }

        LOG.debug("Unmarshalling DOM parsed from InputStream");
        Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(messageElem);
        if (unmarshaller == null) {
            LOG.error("Unable to unmarshall InputStream, no unmarshaller registered for element "
                    + XMLHelper.getNodeQName(messageElem));
            throw new UnmarshallingException(
                    "Unable to unmarshall InputStream, no unmarshaller registered for element "
                            + XMLHelper.getNodeQName(messageElem));
        }

        XMLObject message = unmarshaller.unmarshall(messageElem);

        LOG.debug("InputStream succesfully unmarshalled");
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
        LOG.debug("Parsing Reader into DOM document");

        Document messageDoc = parserPool.parse(reader);
        Element messageElem = messageDoc.getDocumentElement();

        if (LOG.isTraceEnabled()) {
            LOG.trace("Resultant DOM message was:");
            LOG.trace(XMLHelper.nodeToString(messageElem));
        }

        LOG.debug("Unmarshalling DOM parsed from Reader");
        Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(messageElem);
        if (unmarshaller == null) {
            LOG.error("Unable to unmarshall Reader, no unmarshaller registered for element "
                    + XMLHelper.getNodeQName(messageElem));
            throw new UnmarshallingException(
                    "Unable to unmarshall Reader, no unmarshaller registered for element "
                            + XMLHelper.getNodeQName(messageElem));
        }

        XMLObject message = unmarshaller.unmarshall(messageElem);

        LOG.debug("Reader succesfully unmarshalled");
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
        LOG.debug("Marshalling XMLObject");
        
        if (xmlObject.getDOM() != null) {
            LOG.debug("XMLObject already had cached DOM, returning that element");
            return xmlObject.getDOM();
        }

        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(xmlObject);
        if (marshaller == null) {
            LOG.error("Unable to marshall XMLOBject, no marshaller registered for object: "
                    + xmlObject.getElementQName());
        }
        
        Element messageElem = marshaller.marshall(xmlObject);
        
        if (LOG.isTraceEnabled()) {
            LOG.trace("Marshalled XMLObject into DOM:");
            LOG.trace(XMLHelper.nodeToString(messageElem));
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
        XMLHelper.writeNode(element, outputStream);
    }
    
    /**
     * Marshall an XMLObject to a Writer.
     * 
     * @param xmlObject the XMLObject to marshall
     * @param writer the Writer to which to marshall
     * @throws MarshallingException if there is a problem marshalling the object
     */
    public static void marshallToWriter(XMLObject xmlObject, Writer writer) throws MarshallingException {
        Element element = marshall(xmlObject);
        XMLHelper.writeNode(element, writer);
    }

    
}