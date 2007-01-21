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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.IDIndex;
import org.w3c.dom.Element;

/**
 * A object that represents an XML element, usually of a specific schema type, that has been unmarshalled into this Java
 * object.
 */
public interface XMLObject {

    /**
     * Gets the QName for this element. This QName <strong>MUST</strong> contain the namespace URI, namespace prefix,
     * and local element name. Changes made to the returned QName are not reflected by the QName held by this element,
     * that is, the returned QName is a copy of the internal QName member of this class.
     * 
     * @return the QName for this attribute
     */
    public QName getElementQName();

    /**
     * Gets the namespaces that are scoped to this element.
     * 
     * @return the namespaces that are scoped to this element
     */
    public Set<Namespace> getNamespaces();

    /**
     * Adds a namespace to the ones already scoped to this element.
     * 
     * @param namespace the namespace to add
     */
    public void addNamespace(Namespace namespace);

    /**
     * Removes a namespace from this element.
     * 
     * @param namespace the namespace to remove
     */
    public void removeNamespace(Namespace namespace);

    /**
     * Gets the XML schema type of this element. This translates to contents the xsi:type attribute for the element.
     * 
     * @return XML schema type of this element
     */
    public QName getSchemaType();

    /**
     * Checks to see if this object has a parent.
     * 
     * @return true if the object has a parent, false if not
     */
    public boolean hasParent();

    /**
     * Gets the parent of this element or null if there is no parent.
     * 
     * @return the parent of this element or null
     */
    public XMLObject getParent();

    /**
     * Sets the parent of this element.
     * 
     * @param parent the parent of this element
     */
    public void setParent(XMLObject parent);

    /**
     * Checks if this XMLObject has children.
     * 
     * @return true if this XMLObject has children, false if not
     */
    public boolean hasChildren();

    /**
     * Gets an unmodifiable list of child elements in the order that they will appear in the DOM.
     * 
     * @return ordered list of child elements
     */
    public List<XMLObject> getOrderedChildren();
    
    /**
     * Gets the DOM representation of this XMLObject, if one exists.
     * 
     * @return the DOM representation of this XMLObject
     */
    public Element getDOM();

    /**
     * Sets the DOM representation of this XMLObject.
     * 
     * @param dom DOM representation of this XMLObject
     */
    public void setDOM(Element dom);

    /**
     * Releases the DOM representation of this XMLObject, if there is one.
     */
    public void releaseDOM();

    /**
     * Releases the DOM representation of this XMLObject's parent.
     * 
     * @param propagateRelease true if all ancestors of this element should release thier DOM
     */
    public void releaseParentDOM(boolean propagateRelease);

    /**
     * Releases the DOM representation of this XMLObject's children.
     * 
     * @param propagateRelease true if all descendants of this element should release thier DOM
     */
    public void releaseChildrenDOM(boolean propagateRelease);
    
    /**
     * Find the XMLObject which is identified by the specified ID attribute,
     * within the subtree of XMLObjects which has this XMLObject as its root.
     * 
     * @param id  the ID attribute to resolve to an XMLObject
     * @return the XMLObject identified by the specified ID attribute value
     */
    public XMLObject resolveID(String id);
    
    /**
     * Find the XMLObject which is identified by the specified ID attribute,
     * from the root of the tree of XMLObjects in which this XMLObject 
     * is a member.
     * 
     * @param id  the ID attribute to resolve to an XMLObject
     * @return the XMLObject identified by the specified ID attribute value
     */
    public XMLObject resolveIDFromRoot(String id);
    
    /**
     * Get the IDIndex holding the ID-to-XMLObject index mapping, rooted
     * at this XMLObject's subtree.
     * 
     * @return the IDIndex owned by this XMLObject
     */
    public IDIndex getIDIndex();
 

}