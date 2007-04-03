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
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLConstants;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A thread safe, abstract implementation of the {@link org.opensaml.xml.io.Marshaller} interface. This class handles
 * most of the boilerplate code:
 * <ul>
 * <li>Ensuring elements to be marshalled are of either the correct xsi:type or element QName</li>
 * <li>Setting the appropriate namespace and prefix for the marshalled element</li>
 * <li>Setting the xsi:type for the element if the element has an explicit type</li>
 * <li>Setting namespaces attributes declared for the element</li>
 * <li>Marshalling of child elements</li>
 * </ul>
 */
public abstract class AbstractXMLObjectMarshaller implements Marshaller {

    /** Class logger. */
    private static Logger log = Logger.getLogger(AbstractXMLObjectMarshaller.class);

    /** The target name and namespace for this marshaller. */
    private QName targetQName;

    /** Factory for XMLObject Marshallers. */
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
     */
    protected AbstractXMLObjectMarshaller(String targetNamespaceURI, String targetLocalName) {
        targetQName = XMLHelper.constructQName(targetNamespaceURI, targetLocalName, null);

        marshallerFactory = Configuration.getMarshallerFactory();
    }

    /** {@inheritDoc} */
    public Element marshall(XMLObject xmlObject) throws MarshallingException {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            return marshall(xmlObject, document);
        } catch (ParserConfigurationException e) {
            throw new MarshallingException("Unable to create Document to place marshalled elements in", e);
        }
    }

    /** {@inheritDoc} */
    public Element marshall(XMLObject xmlObject, Document document) throws MarshallingException {
        Element domElement;

        if (log.isDebugEnabled()) {
            log.debug("Starting to marshall " + xmlObject.getElementQName());
        }

        if (document == null) {
            throw new MarshallingException("Given document may not be null");
        }

        checkXMLObjectIsTarget(xmlObject);

        if (log.isDebugEnabled()) {
            log.debug("Checking if " + xmlObject.getElementQName() + " contains a cached DOM representation");
        }
        domElement = xmlObject.getDOM();
        if (domElement != null) {

            prepareForAdoption(xmlObject);

            if (domElement.getOwnerDocument() != document) {
                if (log.isDebugEnabled()) {
                    log.debug("Adopting DOM of XMLObject into given Document");
                }
                XMLHelper.adoptElement(domElement, document);
            }

            if (log.isDebugEnabled()) {
                log.debug("Setting DOM of XMLObject as document element of given Document");
            }
            setDocumentElement(document, domElement);

            return domElement;
        }

        if (log.isDebugEnabled()) {
            log.debug(xmlObject.getElementQName() + " does not contain a cached DOM representation");
            log.debug("Creating Element to marshall " + xmlObject.getElementQName() + " into");
        }
        domElement = XMLHelper.constructElement(document, xmlObject.getElementQName());

        if (log.isDebugEnabled()) {
            log.debug("Setting created element as document root");
        }
        // we need to do this before the rest of the marshalling so that signing and other ID dependent operations have
        // a path to the document root
        setDocumentElement(document, domElement);

        domElement = marshallInto(xmlObject, domElement);

        if (log.isDebugEnabled()) {
            log.debug("Setting created element to DOM cache for XMLObject " + xmlObject.getElementQName());
        }
        xmlObject.setDOM(domElement);
        xmlObject.releaseParentDOM(true);

        return domElement;
    }

    /** {@inheritDoc} */
    public Element marshall(XMLObject xmlObject, Element parentElement) throws MarshallingException {
        Element domElement;

        if (log.isDebugEnabled()) {
            log.debug("Starting to marshall " + xmlObject.getElementQName() + " as child of "
                    + XMLHelper.getNodeQName(parentElement));
        }

        if (parentElement == null) {
            throw new MarshallingException("Given parent element is null");
        }

        checkXMLObjectIsTarget(xmlObject);

        if (log.isDebugEnabled()) {
            log.debug("Checking if " + xmlObject.getElementQName() + " contains a cached DOM representation");
        }
        domElement = xmlObject.getDOM();
        if (domElement != null) {
            if (log.isDebugEnabled()) {
                log.debug(xmlObject.getElementQName() + " contains a cached DOM representation");
            }

            prepareForAdoption(xmlObject);

            if (log.isDebugEnabled()) {
                log.debug("Appending DOM of XMLObject " + xmlObject.getElementQName() + " as child of parent element "
                        + XMLHelper.getNodeQName(parentElement));
            }
            XMLHelper.appendChildElement(parentElement, domElement);

            return domElement;
        }

        if (log.isDebugEnabled()) {
            log.debug(xmlObject.getElementQName() + " does not contain a cached DOM representation");
            log.debug("Creating Element to marshall " + xmlObject.getElementQName() + " into");
        }
        Document owningDocument = parentElement.getOwnerDocument();
        domElement = XMLHelper.constructElement(owningDocument, xmlObject.getElementQName());

        if (log.isDebugEnabled()) {
            log.debug("Appending newly created element to given parent element");
        }
        // we need to do this before the rest of the marshalling so that signing and other ID dependent operations have
        // a path to the document root
        XMLHelper.appendChildElement(parentElement, domElement);
        domElement = marshallInto(xmlObject, domElement);

        if (log.isDebugEnabled()) {
            log.debug("Setting created element to DOM cache for XMLObject " + xmlObject.getElementQName());
        }
        xmlObject.setDOM(domElement);
        xmlObject.releaseParentDOM(true);

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
     * @return the DOM element the {@link XMLObject} is marshalled into
     * 
     * @throws MarshallingException thrown if there is a problem marshalling the object
     */
    protected Element marshallInto(XMLObject xmlObject, Element targetElement) throws MarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Setting namespace prefix for " + xmlObject.getElementQName().getPrefix() + " for XMLObject "
                    + xmlObject.getElementQName());
        }

        marshallNamespacePrefix(xmlObject, targetElement);

        marshallSchemaInstanceAttributes(xmlObject, targetElement);

        marshallNamespaces(xmlObject, targetElement);

        marshallAttributes(xmlObject, targetElement);

        marshallChildElements(xmlObject, targetElement);

        marshallElementContent(xmlObject, targetElement);

        return targetElement;
    }

    /**
     * Checks to make sure the given XMLObject's schema type or element QName matches the target parameters given at
     * marshaller construction time.
     * 
     * @param xmlObject the XMLObject to marshall
     * 
     * @throws MarshallingException thrown if the given object is not or the required type
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
     * Marshalls the namespace prefix of the XMLObject into the DOM element.
     * 
     * @param xmlObject the XMLObject being marshalled
     * @param domElement the DOM element the XMLObject is being marshalled into
     */
    protected void marshallNamespacePrefix(XMLObject xmlObject, Element domElement) {
        String prefix = xmlObject.getElementQName().getPrefix();
        prefix = DatatypeHelper.safeTrimOrNullString(prefix);

        if (prefix != null) {
            domElement.setPrefix(prefix);
        }
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
     * Creates the xmlns attributes for any namespaces set on the given XMLObject.
     * 
     * @param xmlObject the XMLObject
     * @param domElement the DOM element the namespaces will be added to
     */
    protected void marshallNamespaces(XMLObject xmlObject, Element domElement) {
        if (log.isDebugEnabled()) {
            log.debug("Marshalling namespace attributes for XMLObject " + xmlObject.getElementQName());
        }
        Set<Namespace> namespaces = xmlObject.getNamespaces();

        for (Namespace namespace : namespaces) {
            if (!namespace.alwaysDeclare()
                    && XMLHelper.lookupNamespaceURI(domElement, namespace.getNamespacePrefix()) != null) {
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

                XMLHelper.appendNamespaceDecleration(domElement, nsURI, nsPrefix);
            }
        }
    }

    /**
     * Creates the XSI type, schemaLocation, and noNamespaceSchemaLocation attributes for an XMLObject.
     * 
     * @param xmlObject the XMLObject
     * @param domElement the DOM element the namespaces will be added to
     * 
     * @throws MarshallingException thrown if the schema type information is invalid
     */
    protected void marshallSchemaInstanceAttributes(XMLObject xmlObject, Element domElement)
            throws MarshallingException {
        
        if (!DatatypeHelper.isEmpty(xmlObject.getSchemaLocation())) {
            if (log.isDebugEnabled()) {
                log.debug("Setting xsi:schemaLocation for XMLObject " + xmlObject.getElementQName() + " to "
                        + xmlObject.getSchemaLocation());
            }
            domElement.setAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_PREFIX + ":schemaLocation", xmlObject
                    .getSchemaLocation());
        }

        if (!DatatypeHelper.isEmpty(xmlObject.getNoNamespaceSchemaLocation())) {
            if (log.isDebugEnabled()) {
                log.debug("Setting xsi:noNamespaceSchemaLocation for XMLObject " + xmlObject.getElementQName() + " to "
                        + xmlObject.getNoNamespaceSchemaLocation());
            }
            domElement.setAttributeNS(XMLConstants.XSI_NS, XMLConstants.XSI_PREFIX + ":noNamespaceSchemaLocation",
                    xmlObject.getNoNamespaceSchemaLocation());
        }

        QName type = xmlObject.getSchemaType();
        if (type == null) {
            return;
        }
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
            log.debug("Adding XSI namespace to list of namespaces used by XMLObject " + xmlObject.getElementQName());
        }
        xmlObject.addNamespace(new Namespace(XMLConstants.XSI_NS, XMLConstants.XSI_PREFIX));
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
     * 
     * @throws MarshallingException thrown if the textual content can not be added to the DOM element
     */
    protected abstract void marshallElementContent(XMLObject xmlObject, Element domElement) throws MarshallingException;

    /**
     * Prepares the given DOM caching XMLObject for adoption into another document. If the XMLObject has a parent then
     * all visible namespaces used by the given XMLObject and its descendants are declared within that subtree and the
     * parent's DOM is invalidated.
     * 
     * @param domCachingObject the XMLObject to prepare for adoption
     * 
     * @throws MarshallingException thrown if a namespace within the XMLObject's DOM subtree can not be resolved.
     */
    private void prepareForAdoption(XMLObject domCachingObject) throws MarshallingException {
        if (domCachingObject.getParent() != null) {
            if (log.isDebugEnabled()) {
                log.debug("Rooting all visible namespaces of XMLObject " + domCachingObject.getElementQName()
                        + " before adding it to new parent Element");
            }
            try {
                XMLHelper.rootNamespaces(domCachingObject.getDOM());
            } catch (XMLParserException e) {
                String errorMsg = "Unable to root namespaces of cached DOM element, "
                        + domCachingObject.getElementQName();
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