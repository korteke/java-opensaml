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

package org.opensaml.xml;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.XMLHelper;

/**
 * An abstract implementation of XMLObject.
 */
public abstract class AbstractXMLObject implements XMLObject {

    /** Parent of this element */
    private XMLObject parent;

    /** The name of this element with namespace and prefix information */
    private QName elementQname;

    /** The schema type of this element with namespace and prefix information */
    private QName typeQname;

    /** Namespaces declared on this element */
    private HashSet<Namespace> namespaces = new HashSet<Namespace>();

    /**
     * A constructor that allows the element QName to be set after construction. <strong>NOTE</strong> great care
     * should be taken when using this method of construction, very bad things will happen if most code tries to work
     * with XMLObjects that don't have Element QNames.
     */
    protected AbstractXMLObject() {

    }

    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AbstractXMLObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        elementQname = XMLHelper.constructQName(namespaceURI, elementLocalName, namespacePrefix);
        addNamespace(new Namespace(namespaceURI, namespacePrefix));
        setElementNamespacePrefix(namespacePrefix);
    }

    /*
     * @see org.opensaml.common.SAMLObject#getQName()
     */
    public QName getElementQName() {
        return new QName(elementQname.getNamespaceURI(), elementQname.getLocalPart(), elementQname.getPrefix());
    }

    /**
     * Sets the element QName.
     * 
     * @param elementQName the element's QName
     * 
     * @throws NullPointerException thrown if the give QName is null
     */
    protected void setElementQName(QName elementQName) throws NullPointerException {
        this.elementQname = XMLHelper.constructQName(elementQName.getNamespaceURI(), elementQName.getLocalPart(),
                elementQName.getPrefix());
        addNamespace(new Namespace(elementQName.getNamespaceURI(), elementQName.getLocalPart()));
    }

    /*
     * @see org.opensaml.common.SAMLObject#setNamespaceAndPrefix(java.lang.String, java.lang.String)
     */
    public void setElementNamespacePrefix(String prefix) {
        if (prefix == null) {
            elementQname = new QName(elementQname.getNamespaceURI(), elementQname.getLocalPart());
        } else {
            elementQname = new QName(elementQname.getNamespaceURI(), elementQname.getLocalPart(), prefix);
        }
    }

    /*
     * @see org.opensaml.common.SAMLObject#getNamespaces()
     */
    public Set<Namespace> getNamespaces() {
        return Collections.unmodifiableSet(namespaces);
    }

    /*
     * @see org.opensaml.common.SAMLObject#addNamespace(javax.xml.namespace.QName)
     */
    public void addNamespace(Namespace namespace) {
        if (namespace != null) {
            namespaces.add(namespace);
        }
    }

    /*
     * @see org.opensaml.common.SAMLObject#removeNamespace(javax.xml.namespace.QName)
     */
    public void removeNamespace(Namespace namespace) {
        namespaces.remove(namespace);
    }

    /*
     * @see org.opensaml.common.SAMLObject#getType()
     */
    public QName getSchemaType() {
        return typeQname;
    }

    /**
     * Sets a given QName as the schema type for the Element represented by this XMLObject. This will add the namespace
     * to the list of namespaces scoped for this XMLObject. It will not remove any namespaces, for example, if there is
     * already a schema type set and null is passed in.
     * 
     * @param type the schema type
     */
    protected void setSchemaType(QName type) {
        if (type == null) {
            typeQname = null;
        } else {
            typeQname = type;
            addNamespace(new Namespace(type.getNamespaceURI(), type.getPrefix()));
        }
    }

    /**
     * Gets the parent of this element
     * 
     * @return the parent of this element
     */
    public XMLObject getParent() {
        return parent;
    }

    /*
     * @see org.opensaml.common.SAMLObject#setParent(org.opensaml.common.SAMLObject)
     */
    public void setParent(XMLObject parent) {
        this.parent = parent;
    }

    /*
     * @see org.opensaml.common.SAMLObject#hasParent()
     */
    public boolean hasParent() {
        return getParent() != null;
    }

    /*
     * @see org.opensaml.xml.XMLObject#hasChildren()
     */
    public boolean hasChildren() {
        List<? extends XMLObject> children = getOrderedChildren();
        return (children != null && children.size() > 0);
    }

    /**
     * A helper function that prepares the XMLObject for the assigned of a new QName value to an attribute. This
     * includes adding a new namespace to the namespace list associated with this Object, if necessary.
     * 
     * @param oldValue existing attribute value
     * @param newValue new attribute value
     * 
     * @return the QName value to assinged to that attribute
     */
    protected QName prepareForAssignment(QName oldValue, QName newValue) {
        if (oldValue == null) {
            if (newValue != null) {
                Namespace newNamespace = new Namespace(newValue.getNamespaceURI(), newValue.getPrefix());
                addNamespace(newNamespace);
            } else {
                return null;
            }
        }

        if (!oldValue.equals(newValue)) {
            Namespace newNamespace = new Namespace(newValue.getNamespaceURI(), newValue.getPrefix());
            addNamespace(newNamespace);
        }

        return newValue;
    }
}