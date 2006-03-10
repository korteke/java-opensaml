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

package org.opensaml.xml.io;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.DOMCachingXMLObject;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.encryption.EncryptableXMLObject;
import org.opensaml.xml.encryption.EncryptableXMLObjectMarshaller;
import org.opensaml.xml.signature.SignableXMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureMarshaller;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLConstants;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A thread safe, abstract implementation of the {@link org.opensaml.xml.io.Marshaller} interface that handles most of
 * the boilerplate code:
 * <ul>
 * <li>Ensuring elements to be marshalled are of either the correct xsi:type or element QName</li>
 * <li>Setting the appropriate namespace and prefix for the marshalled element</li>
 * <li>Setting the xsi:type for the element if the element has an explicit type</li>
 * <li>Setting namespaces attributes declared for the element</li>
 * <li>Marshalling of child elements</li>
 * <li>Digitally signing instance of {@link org.opensaml.xml.signature.SignableXMLObject} that contain a
 * {@link org.opensaml.xml.signature.Signature}</li>
 * <li>Encrypting instances of {@link org.opensaml.xml.encryption.EncryptableXMLObject} that contain an
 * {@link org.opensaml.xml.encryption.EncryptionContext}</li>
 * <li>Caching of created DOM for elements that implement {@link org.opensaml.xml.DOMCachingXMLObject}</li>
 * </ul>
 */
public abstract class AbstractXMLObjectMarshaller implements Marshaller {

    /** Logger */
    private static Logger log = Logger.getLogger(AbstractXMLObjectMarshaller.class);

    /** The target name and namespace for this marshaller. */
    private QName targetQName;

    /** Factory for XMLObject Marshallers */
    private MarshallerFactory marshallerFactory;

    /**
     * Constructor.
     * 
     */
    protected AbstractXMLObjectMarshaller() {
        marshallerFactory = Configuration.getMarshallerFactory();
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
     * @throws NullPointerException if any of the arguments are null (or empty in the case of String parameters)
     */
    protected AbstractXMLObjectMarshaller(String targetNamespaceURI, String targetLocalName)
            throws NullPointerException {
        if (DatatypeHelper.isEmpty(targetNamespaceURI)) {
            throw new NullPointerException("Target Namespace URI may not be null or an empty");
        }

        if (DatatypeHelper.isEmpty(targetLocalName)) {
            throw new NullPointerException("Target Local Name may not be null or an empty");
        }
        targetQName = XMLHelper.constructQName(targetNamespaceURI, targetLocalName, null);

        marshallerFactory = Configuration.getMarshallerFactory();
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(org.opensaml.xml.XMLObject)
     */
    public Element marshall(XMLObject xmlObject) throws MarshallingException {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            return marshall(xmlObject, document);
        } catch (ParserConfigurationException e) {
            throw new MarshallingException("Unable to create Document to place marshalled elements in", e);
        }
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(org.opensaml.xml.XMLObject, org.w3c.dom.Document)
     */
    public Element marshall(XMLObject xmlObject, Document document) throws MarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Starting to marshall " + xmlObject.getElementQName());
        }

        if (document == null) {
            throw new MarshallingException("Given document may not be null");
        }

        checkXMLObjectIsTarget(xmlObject);

        if (xmlObject instanceof DOMCachingXMLObject) {
            DOMCachingXMLObject domCachingObject = (DOMCachingXMLObject) xmlObject;
            Element cachedDOM = domCachingObject.getDOM();

            if (cachedDOM != null) {
                if (cachedDOM.getOwnerDocument() != document) {
                    XMLHelper.adoptElement(cachedDOM, document);
                }

                domCachingObject.releaseParentDOM(true);
                setDocumentElement(document, cachedDOM);
                return cachedDOM;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Creating Element to marshall " + xmlObject.getElementQName() + " into");
        }
        Element domElement = document.createElementNS(xmlObject.getElementQName().getNamespaceURI(), xmlObject
                .getElementQName().getLocalPart());

        if (log.isDebugEnabled()) {
            log.debug("Setting created element as document root");
        }
        // we need to do this before the rest of the marshalling so that signing and other ID dependent operations have
        // a path to the document root
        setDocumentElement(document, domElement);

        domElement = marshallInto(xmlObject, domElement);

        if (xmlObject instanceof DOMCachingXMLObject) {
            DOMCachingXMLObject domCachingObject = (DOMCachingXMLObject) xmlObject;
            if (log.isDebugEnabled()) {
                log.debug("Setting created element to DOM cache for XMLObject " + xmlObject.getElementQName());
            }
            domCachingObject.setDOM(domElement);
            domCachingObject.releaseParentDOM(true);
        }

        return domElement;
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(org.opensaml.xml.XMLObject, org.w3c.dom.Document)
     */
    public Element marshall(XMLObject xmlObject, Element parentElement) throws MarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Starting to marshall " + xmlObject.getElementQName() + " as child of "
                    + XMLHelper.getNodeQName(parentElement));
        }

        if (parentElement == null) {
            throw new MarshallingException("Given parent element is null");
        }

        checkXMLObjectIsTarget(xmlObject);

        if (xmlObject instanceof DOMCachingXMLObject) {
            DOMCachingXMLObject domCachingObject = (DOMCachingXMLObject) xmlObject;
            Element cachedDOM = domCachingObject.getDOM();
            if (cachedDOM != null) {
                domCachingObject.releaseParentDOM(true);
                XMLHelper.appendChildElement(cachedDOM, parentElement);
                return cachedDOM;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Creating Element to marshall " + xmlObject.getElementQName() + " into");
        }
        Document owningDocument = parentElement.getOwnerDocument();
        Element domElement = owningDocument.createElementNS(xmlObject.getElementQName().getNamespaceURI(), xmlObject
                .getElementQName().getLocalPart());

        if (log.isDebugEnabled()) {
            log.debug("Appending newly created element to given parent element");
        }
        // we need to do this before the rest of the marshalling so that signing and other ID dependent operations have
        // a path to the document root
        XMLHelper.appendChildElement(parentElement, domElement);
        domElement = marshallInto(xmlObject, domElement);

        if (xmlObject instanceof DOMCachingXMLObject) {
            DOMCachingXMLObject domCachingObject = (DOMCachingXMLObject) xmlObject;
            if (log.isDebugEnabled()) {
                log.debug("Setting created element to DOM cache for XMLObject " + xmlObject.getElementQName());
            }
            domCachingObject.setDOM(domElement);
            domCachingObject.releaseParentDOM(true);
        }

        return domElement;

    }

    /**
     * Sets the given element as the Document Element of the given Document. If the document already has a Document
     * Element it is replaced by the given element.
     * 
     * @param document the document
     * @param element the Element that will serve as the Document Element
     */
    protected void setDocumentElement(Document document, Element element) {
        Element documentRoot = document.getDocumentElement();
        if (documentRoot != null) {
            document.replaceChild(documentRoot, element);
        } else {
            document.appendChild(element);
        }
    }

    /**
     * Marshalls the given XMLObject into the given DOM Element. The DOM Element must be within a DOM tree whose root is
     * the Document Element of the Document that owns the given DOM Element.
     * 
     * @param xmlObject the XMLObject to marshall
     * @param targetElement the Element into which the XMLObject is marshalled into
     * 
     * @throws MarshallingException thrown if there is a problem marshalling the object
     */
    protected Element marshallInto(XMLObject xmlObject, Element targetElement) throws MarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Setting namespace prefix for " + xmlObject.getElementQName().getPrefix() + " for XMLObject "
                    + xmlObject.getElementQName());
        }
        targetElement.setPrefix(xmlObject.getElementQName().getPrefix());

        marshallElementType(xmlObject, targetElement);

        marshallNamespaces(xmlObject, targetElement);

        marshallAttributes(xmlObject, targetElement);

        marshallChildElements(xmlObject, targetElement);

        marshallElementContent(xmlObject, targetElement);

        if (xmlObject instanceof SignableXMLObject) {
            signElement(targetElement, xmlObject);
        }

        if (xmlObject instanceof EncryptableXMLObject) {
            targetElement = encryptElement(targetElement, xmlObject);
        }

        return targetElement;
    }

    /**
     * Checks to make sure the given XMLObject's schema type or element QName matches the target parameters given at
     * marshaller construction time.
     * 
     * @param xmlObject the XMLObject to marshall
     */
    protected void checkXMLObjectIsTarget(XMLObject xmlObject) throws MarshallingException {
        if (targetQName == null) {
            if (log.isDebugEnabled()) {
                log.debug("Targeted QName checking is not available for this marshaller, XMLObject "
                        + xmlObject.getElementQName() + " was not verified");
            }
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Checking that " + xmlObject.getElementQName() + " meets target criteria");
        }

        QName type = xmlObject.getSchemaType();
        if (type != null && type.equals(targetQName)) {
            if (log.isDebugEnabled()) {
                log.debug(xmlObject.getElementQName() + " schema type matches target");
            }
            return;
        } else {
            QName elementQName = xmlObject.getElementQName();
            if (elementQName.equals(targetQName)) {
                if (log.isDebugEnabled()) {
                    log.debug(xmlObject.getElementQName() + " element QName matches target");
                }
                return;
            }
        }

        String errorMsg = "This marshaller only operations on " + targetQName + " elements not "
                + xmlObject.getElementQName();
        log.error(errorMsg);
        throw new MarshallingException(errorMsg);
    }

    /**
     * Marshalls the child elements of the given XMLObject.
     * 
     * @param xmlObject the XMLObject whose children will be marshalled
     * @param domElement the DOM element that will recieved the marshalled children
     * 
     * @throws MarshallingException thrown if there is a problem marshalling a child element
     */
    protected void marshallChildElements(XMLObject xmlObject, Element domElement) throws MarshallingException {

        if (log.isDebugEnabled()) {
            log.debug("Marshalling child elements for XMLObject " + xmlObject.getElementQName());
        }

        List<XMLObject> childXMLObjects = xmlObject.getOrderedChildren();
        if (childXMLObjects != null && childXMLObjects.size() > 0) {
            for (XMLObject childXMLObject : childXMLObjects) {
                if (childXMLObject == null) {
                    continue;
                }

                if (log.isDebugEnabled()) {
                    log.debug("Getting marshaller for child XMLObject " + childXMLObject.getElementQName());
                }
                Marshaller marshaller = marshallerFactory.getMarshaller(childXMLObject);

                if (marshaller == null) {
                    marshaller = marshallerFactory.getMarshaller(Configuration.getDefaultProviderQName());

                    if (marshaller == null) {
                        String errorMsg = "No marshaller available for " + childXMLObject.getElementQName()
                                + ", child of " + xmlObject.getElementQName();
                        log.error(errorMsg);
                        throw new MarshallingException(errorMsg);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("No marshaller was registered for " + childXMLObject.getElementQName()
                                    + ", child of " + xmlObject.getElementQName() + " but the default marshaller "
                                    + marshaller.getClass().getName() + " was available, using it.");
                        }
                    }
                }

                if (log.isDebugEnabled()) {
                    log.debug("Marshalling " + childXMLObject.getElementQName() + " and adding it to DOM");
                }
                marshaller.marshall(childXMLObject, domElement);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("No child elements to marshall for XMLObject " + xmlObject.getElementQName());
            }
        }
    }

    /**
     * Creates an xsi:type attribute, corresponding to the given type of the XMLObject, on the DOM element.
     * 
     * @param xmlObject the XMLObject
     * @param domElement the DOM element
     * 
     * @throws MarshallingException thrown if the type on the XMLObject is does contain a local name, local name prefix,
     *             and namespace URI
     */
    protected void marshallElementType(XMLObject xmlObject, Element domElement) throws MarshallingException {
        QName type = xmlObject.getSchemaType();
        if (type != null) {
            if (log.isDebugEnabled()) {
                log.debug("Setting xsi:type attribute with for XMLObject " + xmlObject.getElementQName());
            }
            String typeLocalName = DatatypeHelper.safeTrimOrNullString(type.getLocalPart());
            String typePrefix = DatatypeHelper.safeTrimOrNullString(type.getPrefix());

            if (typeLocalName == null) {
                throw new MarshallingException("The type QName on XMLObject " + xmlObject.getElementQName()
                        + " may not have a null local name");
            }

            if (type.getNamespaceURI() == null) {
                throw new MarshallingException("The type URI QName on XMLObject " + xmlObject.getElementQName()
                        + " may not have a null namespace URI");
            }

            String attributeValue;
            if (typePrefix == null) {
                attributeValue = typeLocalName;
            } else {
                attributeValue = typePrefix + ":" + typeLocalName;
            }

            domElement.setAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_PREFIX + ":type", attributeValue);

            if (log.isDebugEnabled()) {
                log
                        .debug("Adding XSI namespace to list of namespaces used by XMLObject "
                                + xmlObject.getElementQName());
            }
            xmlObject.addNamespace(new Namespace(XMLConstants.XSI_NS, XMLConstants.XSI_PREFIX));
        }
    }

    /**
     * Creates the xmlns attributes for any namespaces set on the given XMLObject.
     * 
     * @param xmlObject the XMLObject
     * @param domElement the DOM element the namespaces will be added to
     */
    protected void marshallNamespaces(XMLObject xmlObject, Element domElement) throws MarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Marshalling namespace attributes for XMLObject " + xmlObject.getElementQName());
        }
        Set<Namespace> namespaces = xmlObject.getNamespaces();

        for (Namespace namespace : namespaces) {
            if (!namespace.alwaysDeclare() && domElement.lookupNamespaceURI(namespace.getNamespacePrefix()) != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Namespace " + namespace + " has already been declared on an ancestor of "
                            + xmlObject.getElementQName() + " no need to add it here");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Adding namespace decleration " + namespace + " to " + xmlObject.getElementQName());
                }
                String nsURI = DatatypeHelper.safeTrimOrNullString(namespace.getNamespaceURI());
                String nsPrefix = DatatypeHelper.safeTrimOrNullString(namespace.getNamespacePrefix());

                String attributeName;
                if (nsPrefix == null) {
                    attributeName = XMLConstants.XMLNS_PREFIX;
                } else {
                    attributeName = XMLConstants.XMLNS_PREFIX + ":" + nsPrefix;
                }

                String attributeValue;
                if (nsURI == null) {
                    attributeValue = "";
                } else {
                    attributeValue = nsURI;
                }

                domElement.setAttributeNS(XMLConstants.XMLNS_NS, attributeName, attributeValue);
            }
        }
    }

    /**
     * Signs the DOM representation of the given XMLObject.
     * 
     * @param xmlObject the XMLObject whose XML representation will be signed
     * 
     * @throws MarshallingException thrown is there is a problem signing the XML
     */
    protected void signElement(Element domElement, XMLObject xmlObject) throws MarshallingException {
        SignableXMLObject signableXMLObject = (SignableXMLObject) xmlObject;

        Signature signature = signableXMLObject.getSignature();
        if (signature == null) {
            if (log.isDebugEnabled()) {
                log
                        .debug(XMLHelper.getNodeQName(domElement)
                                + " is a signable object but does not contain a Signature child, skipping signature computation");
            }
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Computing digital signature for " + xmlObject.getElementQName());
        }

        SignatureMarshaller signatureMarshaller = (SignatureMarshaller) marshallerFactory.getMarshaller(signature);
        signatureMarshaller.signElement(domElement, signature);
    }

    /**
     * Encrypts the given DOM Element which is a representation of the given XMLObject. The given XMLObject MUST be of
     * type {@link EncryptableXMLObject}
     * 
     * @param domElement the Element to be encrypted
     * @param xmlObject the XMLObject represented by the Element
     * 
     * @return the encrypted element
     * 
     * @throws MarshallingException thrown if the element can not be encrypted
     */
    protected Element encryptElement(Element domElement, XMLObject xmlObject) throws MarshallingException {
        EncryptableXMLObject encryptableXMLObject = (EncryptableXMLObject) xmlObject;

        if (encryptableXMLObject.getEncryptionContext() == null) {
            if (log.isDebugEnabled()) {
                log.debug(xmlObject.getElementQName()
                        + " is an encryptable object but does not contain an encryption context, skipping encryption");
            }
            return domElement;
        }

        EncryptableXMLObjectMarshaller marshaller = (EncryptableXMLObjectMarshaller) marshallerFactory
                .getMarshaller(encryptableXMLObject);
        return marshaller.encryptElement(domElement, encryptableXMLObject);
    }

    /**
     * Marshalls a given XMLObject into a W3C Element. The given signing context should be blindly passed to the
     * marshaller for child elements. The XMLObject passed to this method is guaranteed to be of the target name
     * specified during this unmarshaller's construction.
     * 
     * @param xmlObject the XMLObject to marshall
     * @param domElement the W3C DOM element
     * 
     * @throws MarshallingException thrown if there is a problem marshalling the element
     */
    protected abstract void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException;

    /**
     * Marshalls data from the XMLObject into content of the DOM Element.
     * 
     * @param xmlObject the XMLObject
     * @param domElement the DOM element recieving the content
     */
    protected abstract void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException;
}