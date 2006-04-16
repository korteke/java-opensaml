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

package org.opensaml.xml.io;

import org.apache.log4j.Logger;
import org.opensaml.xml.DOMCachingXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * An extension to {@link org.opensaml.xml.io.AbstractXMLObjectMarshaller} that operates on
 * {@link org.opensaml.xml.DOMCachingXMLObject}s. This marshaller ensures that cached DOM's are used when available and
 * that constructed DOMs are cached after they are built.
 */
public abstract class AbstractDOMCachingXMLObjectMarshaller extends AbstractXMLObjectMarshaller {

    /** Logger */
    private static Logger log = Logger.getLogger(AbstractDOMCachingXMLObjectMarshaller.class);

    /**
     * Constructor.
     */
    protected AbstractDOMCachingXMLObjectMarshaller() {
        super();
    }

    /**
     * This constructor supports checking an XMLObject to be marshalled, either element name or schema type, against a
     * given namespace/local name pair.
     * 
     * @param targetNamespaceURI the namespace URI of either the schema type QName or element QName of the elements this
     *            unmarshaller operates on
     * @param targetLocalName the local name of either the schema type QName or element QName of the elements this
     *            unmarshaller operates on
     * 
     * @throws IllegalArgumentException if any of the arguments are null (or empty in the case of String parameters)
     */
    protected AbstractDOMCachingXMLObjectMarshaller(String targetNamespaceURI, String targetLocalName)
            throws IllegalArgumentException {
        super(targetNamespaceURI, targetLocalName);
    }

    /**
     * {@inheritDoc}
     */
    public Element marshall(XMLObject xmlObject, Document document) throws MarshallingException {
        DOMCachingXMLObject domCachingObject = (DOMCachingXMLObject) xmlObject;
        Element domElement;

        if (document == null) {
            throw new MarshallingException("Given document may not be null");
        }

        if (log.isDebugEnabled()) {
            log.debug("Checking if " + xmlObject.getElementQName() + " contains a cached DOM representation");
        }
        domElement = domCachingObject.getDOM();
        if (domElement != null) {
            
            prepareForAdoption(domCachingObject);

            if (domElement.getOwnerDocument() != document) {
                if(log.isDebugEnabled()){
                    log.debug("Adopting DOM of XMLObject into given Document");
                }
                XMLHelper.adoptElement(domElement, document);
            }
            
            if(log.isDebugEnabled()){
                log.debug("Setting DOM of XMLObject as document element of given Document");
            }
            setDocumentElement(document, domElement);
            
            return domElement;
        }

        if (log.isDebugEnabled()) {
            log.debug(xmlObject.getElementQName() + " does not contain a cached DOM representation");
        }
        domElement = super.marshall(xmlObject, document);

        if (log.isDebugEnabled()) {
            log.debug("Setting created element to DOM cache for XMLObject " + xmlObject.getElementQName());
        }
        domCachingObject.setDOM(domElement);
        domCachingObject.releaseParentDOM(true);

        return domElement;
    }

    /**
     * {@inheritDoc}
     */
    public Element marshall(XMLObject xmlObject, Element parentElement) throws MarshallingException {
        DOMCachingXMLObject domCachingObject = (DOMCachingXMLObject) xmlObject;
        Element domElement;

        if (parentElement == null) {
            throw new MarshallingException("Can not marshalli into a null parent element");
        }

        if (log.isDebugEnabled()) {
            log.debug("Checking if " + xmlObject.getElementQName() + " contains a cached DOM representation");
        }
        domElement = domCachingObject.getDOM();
        if (domElement != null) {
            if (log.isDebugEnabled()) {
                log.debug(xmlObject.getElementQName() + " contains a cached DOM representation");
            }

            prepareForAdoption(domCachingObject);

            if (log.isDebugEnabled()) {
                log.debug("Appending DOM of XMLObject " + xmlObject.getElementQName() + " as child of parent element "
                        + XMLHelper.getNodeQName(parentElement));
            }
            XMLHelper.appendChildElement(parentElement, domElement);
            
            return domElement;
        }

        if (log.isDebugEnabled()) {
            log.debug(xmlObject.getElementQName() + " does not contain a cached DOM representation");
        }
        domElement = super.marshall(xmlObject, parentElement);

        if (log.isDebugEnabled()) {
            log.debug("Setting created element to DOM cache for XMLObject " + xmlObject.getElementQName());
        }
        domCachingObject.setDOM(domElement);
        domCachingObject.releaseParentDOM(true);

        return domElement;
    }
    
    /**
     * Prepares the given DOM caching XMLObject for adoption into another document.  If the XMLObject has a parent 
     * then all visible namespaces used by the given XMLObject and its descendants are declared within that subtree 
     * and the parent's DOM is invalidated. 
     * 
     * @param domCachingObject the XMLObject to prepare for adoption
     * 
     * @throws MarshallingException thrown if a namespace within the XMLObject's DOM subtree can not be resolved.
     */
    private void prepareForAdoption(DOMCachingXMLObject domCachingObject) throws MarshallingException{        
        if (domCachingObject.getParent() != null) {
            if (log.isDebugEnabled()) {
                log.debug("Rooting all visible namespaces of XMLObject " + domCachingObject.getElementQName()
                        + " before adding it to new parent Element");
            }
            try {
                XMLHelper.rootNamespaces(domCachingObject.getDOM());
            } catch (XMLParserException e) {
                String errorMsg = "Unable to root namespaces of cached DOM element, " + domCachingObject.getElementQName();
                log.error(errorMsg, e);
                throw new MarshallingException(errorMsg, e);
            }
            
            if (log.isDebugEnabled()) {
                log.debug("Release DOM of XMLObject parent");
            }
            domCachingObject.releaseParentDOM(true);
        }
    }
}