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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.log4j.Logger;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * An abstract implementation of XMLObject.
 */
public abstract class AbstractXMLObject implements XMLObject {

    /** Class logger. */
    private final Logger log = Logger.getLogger(AbstractXMLObject.class);

    /** Parent of this element. */
    private XMLObject parent;

    /** The name of this element with namespace and prefix information. */
    private QName elementQname;

    /** The schema type of this element with namespace and prefix information. */
    private QName typeQname;

    /** Namespaces declared on this element. */
    private FastSet<Namespace> namespaces;

    /** DOM Element representation of this object. */
    private Element dom;
    
    /** Mapping of ID attributes to XMLObjects in the subtree rooted at this object.
     * This allows constant-time dereferencing of ID-typed attributes within the subtree.  */
    private Map<String, XMLObject> idMappings;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AbstractXMLObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        idMappings = new FastMap<String, XMLObject>();
        namespaces = new FastSet<Namespace>();
        elementQname = XMLHelper.constructQName(namespaceURI, elementLocalName, namespacePrefix);
        addNamespace(new Namespace(namespaceURI, namespacePrefix));
        setElementNamespacePrefix(namespacePrefix);
    }

    /** {@inheritDoc} */
    public QName getElementQName() {
        return new QName(elementQname.getNamespaceURI(), elementQname.getLocalPart(), elementQname.getPrefix());
    }

    /**
     * Sets the element QName.
     * 
     * @param elementQName the element's QName
     */
    protected void setElementQName(QName elementQName) {
        this.elementQname = XMLHelper.constructQName(elementQName.getNamespaceURI(), elementQName.getLocalPart(),
                elementQName.getPrefix());
        addNamespace(new Namespace(elementQName.getNamespaceURI(), elementQName.getLocalPart()));
    }

    /**
     * Sets the prefix for this element's namespace.
     * 
     * @param prefix the prefix for this element's namespace
     */
    public void setElementNamespacePrefix(String prefix) {
        if (prefix == null) {
            elementQname = new QName(elementQname.getNamespaceURI(), elementQname.getLocalPart());
        } else {
            elementQname = new QName(elementQname.getNamespaceURI(), elementQname.getLocalPart(), prefix);
        }
    }

    /** {@inheritDoc} */
    public Set<Namespace> getNamespaces() {
        return Collections.unmodifiableSet(namespaces);
    }

    /** {@inheritDoc} */
    public void addNamespace(Namespace namespace) {
        if (namespace != null) {
            namespaces.add(namespace);
        }
    }

    /** {@inheritDoc} */
    public void removeNamespace(Namespace namespace) {
        namespaces.remove(namespace);
    }

    /** {@inheritDoc} */
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
     * Gets the parent of this element.
     * 
     * @return the parent of this element
     */
    public XMLObject getParent() {
        return parent;
    }

    /** {@inheritDoc} */
    public void setParent(XMLObject newParent) {
        parent = newParent;
    }

    /** {@inheritDoc} */
    public boolean hasParent() {
        return getParent() != null;
    }

    /** {@inheritDoc} */
    public boolean hasChildren() {
        List<? extends XMLObject> children = getOrderedChildren();
        return children != null && children.size() > 0;
    }

    /** {@inheritDoc} */
    public Element getDOM() {
        return dom;
    }

    /** {@inheritDoc} */
    public void setDOM(Element newDom) {
        dom = newDom;
    }

    /** {@inheritDoc} */
    public void releaseDOM() {
        if (log.isTraceEnabled()) {
            log.trace("Releasing cached DOM reprsentation for " + getElementQName());
        }

        setDOM(null);
    }

    /** {@inheritDoc} */
    public void releaseParentDOM(boolean propagateRelease) {
        if (log.isTraceEnabled()) {
            log.trace("Releasing cached DOM reprsentation for parent of " + getElementQName()
                    + " with propagation set to " + propagateRelease);
        }

        XMLObject parentElement = getParent();
        if (parentElement != null) {
            parent.releaseDOM();
            if (propagateRelease) {
                parent.releaseParentDOM(propagateRelease);
            }
        }
    }

    /** {@inheritDoc} */
    public void releaseChildrenDOM(boolean propagateRelease) {
        if (log.isTraceEnabled()) {
            log.trace("Releasing cached DOM reprsentation for children of " + getElementQName()
                    + " with propagation set to " + propagateRelease);
        }

        if (getOrderedChildren() != null) {
            for (XMLObject child : getOrderedChildren()) {
                if (child != null) {
                    child.releaseDOM();
                    if (propagateRelease) {
                        child.releaseChildrenDOM(propagateRelease);
                    }
                }
            }
        }
    }

    /**
     * A convience method that is equal to calling {@link #releaseDOM()} then {@link #releaseParentDOM(boolean)} with
     * the release being propogated.
     */
    public void releaseThisandParentDOM() {
        if (getDOM() != null) {
            releaseDOM();
            releaseParentDOM(true);
        }
    }

    /**
     * A convience method that is equal to calling {@link #releaseDOM()} then {@link #releaseChildrenDOM(boolean)} with
     * the release being propogated.
     */
    public void releaseThisAndChildrenDOM() {
        if (getDOM() != null) {
            releaseDOM();
            releaseChildrenDOM(true);
        }
    }

    /**
     * A helper function for derived classes. This 'nornmalizes' newString and then if it is different from oldString
     * invalidates the DOM. It returns the normalized value so subclasses just have to go. this.foo =
     * prepareForAssignment(this.foo, foo);
     * 
     * @param oldValue - the current value
     * @param newValue - the new value
     * 
     * @return the value that should be assigned
     */
    protected String prepareForAssignment(String oldValue, String newValue) {
        String newString = DatatypeHelper.safeTrimOrNullString(newValue);

        if (!DatatypeHelper.safeEquals(oldValue, newString)) {
            releaseThisandParentDOM();
        }

        return newString;
    }

    /**
     * A helper function for derived classes. This checks for semantic equality between two QNames if it they are
     * different invalidates the DOM. It returns the normalized value so subclasses just have to go. this.foo =
     * prepareForAssignment(this.foo, foo);
     * 
     * @param oldValue - the current value
     * @param newValue - the new value
     * 
     * @return the value that should be assigned
     */
    protected QName prepareForAssignment(QName oldValue, QName newValue) {
        if (oldValue == null) {
            if (newValue != null) {
                Namespace newNamespace = new Namespace(newValue.getNamespaceURI(), newValue.getPrefix());
                addNamespace(newNamespace);
                releaseThisandParentDOM();
                return newValue;
            } else {
                return null;
            }
        }

        if (!oldValue.equals(newValue)) {
            if (newValue != null) {
                Namespace newNamespace = new Namespace(newValue.getNamespaceURI(), newValue.getPrefix());
                addNamespace(newNamespace);
            }
            releaseThisandParentDOM();
        }

        return newValue;
    }

    /**
     * A helper function for derived classes that checks to see if the old and new value are equal and if so releases
     * the cached dom. Derived classes are expected to use this thus: <code>
     *   this.foo = prepareForAssignment(this.foo, foo);
     *   </code>
     * 
     * This method will do a (null) safe compare of the objects and will also invalidate the DOM if appropriate
     * 
     * @param <T> - type of object being compared and assigned
     * @param oldValue - current value
     * @param newValue - proposed new value
     * 
     * @return The value to assign to the saved Object.
     */
    protected <T extends Object> T prepareForAssignment(T oldValue, T newValue) {
        if (oldValue == null) {
            if (newValue != null) {
                releaseThisandParentDOM();
                return newValue;
            } else {
                return null;
            }
        }

        if (!oldValue.equals(newValue)) {
            releaseThisandParentDOM();
        }

        return newValue;
    }

    /**
     * A helper function for derived classes, similar to assignString, but for (singleton) SAML objects. It is
     * indifferent to whether either the old or the new version of the value is null. Derived classes are expected to
     * use this thus: <code>
     *   this.foo = prepareForAssignment(this.foo, foo);
     *   </code>
     * 
     * This method will do a (null) safe compare of the objects and will also invalidate the DOM if appropriate
     * 
     * @param <T> type of object being compared and assigned
     * @param oldValue current value
     * @param newValue proposed new value
     * 
     * @return The value to assign to the saved Object.
     */
    protected <T extends XMLObject> T prepareForAssignment(T oldValue, T newValue) {

        if (newValue != null && newValue.hasParent()) {
            throw new IllegalArgumentException(newValue.getClass().getName()
                    + " cannot be added - it is already the child of another SAML Object");
        }

        if (oldValue == null) {
            if (newValue != null) {
                releaseThisandParentDOM();
                newValue.setParent(this);
                registerIDMappings(newValue.getIDMappings());
                return newValue;

            } else {
                return null;
            }
        }

        if (!oldValue.equals(newValue)) {
            oldValue.setParent(null);
            releaseThisandParentDOM();
            deregisterIDMappings(oldValue.getIDMappings());
            if (newValue != null) {
                newValue.setParent(this);
                registerIDMappings(newValue.getIDMappings());
            }
        }

        return newValue;
    }
    
    /** A helper function for derived classes.  The mutator/setter method for any ID-typed
     * attributes should call this method in order to handle getting the old value removed
     * from the ID-to-XMLObject mapping, and the new value added to the mapping.  
     * 
     * @param oldID the old value of the ID-typed attribute
     * @param newID the new value of the ID-typed attribute
     */
    protected void registerOwnID(String oldID, String newID) {
        String newString = DatatypeHelper.safeTrimOrNullString(newID);
        
        if (!DatatypeHelper.safeEquals(oldID, newString)) {
            if (oldID != null) {
                deregisterIDMapping(oldID);
            }
            
            if (newString != null) {
                registerIDMapping(newString, this);
            }
        }
    }
    
    /** {@inheritDoc} */
    public Map<String, XMLObject> getIDMappings() {
        return Collections.unmodifiableMap(idMappings);
    }
    
    /** {@inheritDoc} */
    public void registerIDMapping(String id, XMLObject referent) {
        if (id == null) {
            return;
        }
        
        idMappings.put(id, referent);
        if (hasParent()) {
            getParent().registerIDMapping(id, referent);
        }
    }
    
    /** {@inheritDoc} */
    public void registerIDMappings(Map<String, XMLObject> idMap) {
        if (idMap == null || idMap.isEmpty()) {
            return;
        }
        
        idMappings.putAll(idMap);
        if (hasParent()) {
            getParent().registerIDMappings(idMap);
        }
    }
    
    /** {@inheritDoc} */
    public void deregisterIDMapping(String id) {
        if (id == null) {
            return;
        }
        
        idMappings.remove(id);
        if (hasParent()) {
            getParent().deregisterIDMapping(id);
        }
    }
    
    /** {@inheritDoc} */
    public void deregisterIDMappings(Map<String, XMLObject> idMap) {
        if (idMap == null || idMap.isEmpty()) {
            return;
        }
        
        for (String id : idMap.keySet()) {
            idMappings.remove(id);
        }
        if (hasParent()) {
            getParent().deregisterIDMappings(idMap);
        }
    }

    /** {@inheritDoc} */
    public XMLObject resolveID(String id) {
        return idMappings.get(id);
    }
    
}