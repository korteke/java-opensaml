/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
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

import java.util.Collection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.LazyMap;
import org.opensaml.xml.util.LazySet;
import org.opensaml.xml.util.XMLConstants;

/**
 * A class which is responsible for managing XML namespace-related data for an {@link XMLObject}.
 * 
 * <p>
 * Code which mutates the state of an XMLObject such that XML namespace-related data is also logically changed,
 * should call the appropriate method, based on the type of change being made.
 * </p>
 */
public class NamespaceManager {
    
    /** The token used to represent the default namespace in {@link #getNonVisibleNamespacePrefixes()}. */
    public static final String DEFAULT_NS_TOKEN = "#default";
    
    /** The owning XMLObject. */
    private XMLObject owner;
    
    /** XMLObject name namespace. */
    private Namespace elementName;
    
    /** XMLObject type namespace. */
    private Namespace elementType;
    
    /** Explicitly declared namespaces. */
    private Set<Namespace> decls;
    
    /** Indeterminate namespace usage. */
    private Set<Namespace> usage;
    
    /** Registered namespaces of attribute names. */
    private Set<Namespace> attrNames;
    
    /** Registered namespaces of attribute values. */
    private Map<String, Namespace> attrValues;
    
    /** Registered namespaces of content values. */
    private Namespace contentValue;
    
    /**
     * Constructor.
     *
     * @param owningObject the XMLObject whose namespace info is to be managed
     */
    public NamespaceManager(XMLObject owningObject) {
        owner = owningObject;
        
        decls = new LazySet<Namespace>();
        usage = new LazySet<Namespace>();
        attrNames = new LazySet<Namespace>();
        attrValues = new LazyMap<String, Namespace>();
    }
    
    /**
     * From an QName representing a qualified attribute name, generate an attribute ID
     * suitable for use in {@link #registerAttributeValue(String, QName)} 
     * and {@link #deregisterAttributeValue(String)}.
     * 
     * @param name attribute name as a QName
     * @return a string attribute ID
     */
    public static String generateAttributeID(QName name) {
       return name.toString(); 
    }
    
    /**
     * Get the owning XMLObject instance.
     * 
     * @return the owning XMLObject
     */
    public XMLObject getOwner() {
        return owner;
    }
    
    /**
     * Get the set of namespaces currently in use on the owning XMLObject.
     * 
     * @return the set of namespaces
     */
    public Set<Namespace> getNamespaces() {
        Set<Namespace> namespaces = mergeNamespaceCollections(decls, usage, attrNames, attrValues.values());
        addNamespace(namespaces, getElementNameNamespace());
        addNamespace(namespaces, getElementTypeNamespace());
        addNamespace(namespaces, contentValue);
        return namespaces;
    }
    
    
    /**
     * Register usage of a namespace in some indeterminate fashion.
     * 
     * <p>
     * Other methods which indicate specific usage should be preferred over this one.  This
     * method exists primarily for backward-compatibility support for {@link XMLObject#addNamespace(Namespace)}.
     * </p>
     * 
     * @param namespace namespace to register
     */
    public void registerNamespace(Namespace namespace) {
        addNamespace(usage, namespace);
    }
    
    /**
     * Deregister usage of a namespace in some indeterminate fashion.
     * 
     * <p>
     * Other methods which indicate specific usage should be preferred over this one.  This
     * method exists primarily for backward-compatibility support for {@link XMLObject#removeNamespace(Namespace)}.
     * </p>
     * 
     * @param namespace namespace to deregister
     */
    public void deregisterNamespace(Namespace namespace) {
        removeNamespace(usage, namespace);
    }
    
    /**
     * Register a namespace declaration.
     * 
     * @param namespace the namespace to register
     */
    public void registerNamespaceDeclaration(Namespace namespace) {
        namespace.setAlwaysDeclare(true);
        addNamespace(decls, namespace);
    }
    
    /**
     * Deregister a namespace declaration.
     * 
     * @param namespace the namespace to deregister
     */
    public void deregisterNamespaceDeclaration(Namespace namespace) {
        removeNamespace(decls, namespace);
    }
    
    /**
     * Register a namespace-qualified attribute name.
     * 
     * @param attributeName the attribute name to register
     */
    public void registerAttributeName(QName attributeName) {
        if (checkQName(attributeName)) {
            addNamespace(attrNames, buildNamespace(attributeName));
        }
    }
    
    /**
     * Deregister a namespace-qualified attribute name.
     * 
     * @param attributeName the attribute name to deregister
     */
    public void deregisterAttributeName(QName attributeName) {
        if (checkQName(attributeName)) {
            removeNamespace(attrNames, buildNamespace(attributeName));
        }
    }
    
    /**
     * Register a QName attribute value.
     * 
     * @param attributeID unique identifier for the attribute within the XMLObject's content model
     * @param attributeValue the QName value to register
     */
    public void registerAttributeValue(String attributeID, QName attributeValue) {
        if (checkQName(attributeValue)) {
            attrValues.put(attributeID, buildNamespace(attributeValue));
        }
    }
    
    /**
     * Deregister a QName attribute value.
     * 
     * @param attributeID unique identifier for the attribute within the XMLObject's content model
     */
    public void deregisterAttributeValue(String attributeID) {
        attrValues.remove(attributeID);
    }
    
    /**
     * Register a QName element content value.
     * 
     * @param content the QName value to register
     */
    public void registerContentValue(QName content) {
        if (checkQName(content)) {
            contentValue = buildNamespace(content);
        }
    }
    
    /**
     * Deregister a QName content value.
     * 
     */
    public void deregisterContentValue() {
        contentValue = null;
    }
    
    /**
     * Obtain the set of namespace prefixes used in a non-visible manner on owning XMLObject
     * and its children.
     * 
     * <p>
     * The primary use case for this information is to support the inclusive prefixes
     * information that may optionally be supplied as a part of XML exclusive canonicalization.
     * </p>
     * 
     * @return the set of non-visibly used namespace prefixes
     */
    public Set<String> getNonVisibleNamespacePrefixes() {
        LazySet<String> nonVisibleCandidates = new LazySet<String>();

        // Collect each child's non-visible prefixes.
        List<XMLObject> children = getOwner().getOrderedChildren();
        if (children != null) {
            for(XMLObject child : getOwner().getOrderedChildren()) {
                if (child != null) {
                    Set<String> childPrefixes = child.getNamespaceManager().getNonVisibleNamespacePrefixes();
                    if (childPrefixes != null && ! childPrefixes.isEmpty()) {
                        nonVisibleCandidates.addAll(childPrefixes);
                    }
                }
            }
        }

        // Collect this node's non-visible candidate prefixes.
        nonVisibleCandidates.addAll(getNonVisibleNamespacePrefixCandidates());

        // Now subtract this object's visible prefixes.
        nonVisibleCandidates.removeAll(getVisibleNamespacePrefixes());
        
        // As a special case, never return the 'xml' prefix.
        nonVisibleCandidates.remove(XMLConstants.XML_PREFIX);

        // What remains is this effective set of non-visible prefixes 
        // for the subtree rooted at this node.
        return nonVisibleCandidates;

    }
    
    /**
     * Register the owning XMLObject's element name.
     * 
     * @param name the element name to register
     */
    public void registerElementName(QName name) {
        if (checkQName(name)) {
            elementName = buildNamespace(name);
        }
    }

    /**
     * Register the owning XMLObject's element type, if explicitly declared via an xsi:type.
     * 
     * @param type the element type to register
     */
    public void registerElementType(QName type) {
        if (type != null) {
            if (checkQName(type)) {
                elementType = buildNamespace(type);
            }
        } else {
            elementType = null;
        }
    }
    
    /**
     * Return a Namespace instance representing the namespace of the element name.
     * 
     * @return the element name's namespace
     */
    private Namespace getElementNameNamespace() {
        if (elementName == null && checkQName(owner.getElementQName())) {
            elementName = buildNamespace(owner.getElementQName());
        }
        return elementName;
    }

    /**
     * Return a Namespace instance representing the namespace of the element type, if known.
     * 
     * @return the element type's namespace
     */
    private Namespace getElementTypeNamespace() {
        if (elementType == null) {
            QName type = owner.getSchemaType();
            if (type != null && checkQName(type)) {
                elementType = buildNamespace(type);
            }
        }
        return elementType;
    }
    
    /**
     * Build a {@link Namespace} instance from a {@link QName}.
     * 
     * @param name the source QName 
     * @return a Namespace built using the information in the QName
     */
    private Namespace buildNamespace(QName name) {
        String uri = DatatypeHelper.safeTrimOrNullString(name.getNamespaceURI());
        if (uri == null) {
            throw new IllegalArgumentException("A non-empty namespace URI must be supplied");
        }
        String prefix = DatatypeHelper.safeTrimOrNullString(name.getPrefix());
        return new Namespace(uri, prefix);
    }
    
    /**
     * Add a Namespace to a set of Namespaces.  Namespaces with identical URI and prefix will be treated as equivalent.
     * An <code>alwaysDeclare</code> property of true will take precedence over a value of false.
     * 
     * @param namespaces the set of namespaces
     * @param newNamespace the namespace to add to the set
     */
    private void addNamespace(Set<Namespace> namespaces, Namespace newNamespace) {
        if (newNamespace == null) {
            return;
        }
        
        if (namespaces.size() == 0) {
            namespaces.add(newNamespace);
            return;
        }
        
        for (Namespace namespace : namespaces) {
            if (DatatypeHelper.safeEquals(namespace.getNamespaceURI(), newNamespace.getNamespaceURI()) &&
                    DatatypeHelper.safeEquals(namespace.getNamespacePrefix(), newNamespace.getNamespacePrefix())) {
                if (newNamespace.alwaysDeclare() && !namespace.alwaysDeclare()) {
                    // An alwaysDeclare=true trumps false.
                    // Don't modify the existing object in the set, merely swap them.
                    namespaces.remove(namespace);
                    namespaces.add(newNamespace);
                    return;
                } else {
                    // URI and prefix match, alwaysDeclare does also, so just leave the original
                    return;
                }
            }
        }
        
        namespaces.add(newNamespace);
    }
    
    /**
     * Remove a Namespace from a set of Namespaces.  Equivalence of Namespace instances will be based 
     * on namespace URI and prefix only. The <code>alwaysDeclare</code> property will be ignored for
     * purpose of equivalence.
     * 
     * @param namespaces the set of namespaces
     * @param oldNamespace the namespace to add to the set
     */
    private void removeNamespace(Set<Namespace> namespaces, Namespace oldNamespace) {
        if (oldNamespace == null) {
            return;
        }
        
        Iterator<Namespace> iter = namespaces.iterator();
        while (iter.hasNext()) {
            Namespace namespace = iter.next();
            if (DatatypeHelper.safeEquals(namespace.getNamespaceURI(), oldNamespace.getNamespaceURI()) &&
                    DatatypeHelper.safeEquals(namespace.getNamespacePrefix(), oldNamespace.getNamespacePrefix())) {
                iter.remove();
            }
        }
        
    }
    
    /**
     * Merge 2 or more Namespace collections into a single set, with equivalence semantics as described
     * in {@link #addNamespace(Set, Namespace)}.
     * 
     * @param namespaces list of Namespaces to merge
     * @return the a new set of merged Namespaces
     */
    private Set<Namespace> mergeNamespaceCollections(Collection<Namespace> ... namespaces) {
        LazySet<Namespace> newNamespaces = new LazySet<Namespace>();
        
        for (Collection<Namespace> nsCollection : namespaces) {
            for (Namespace ns : nsCollection) {
                if (ns != null) {
                    addNamespace(newNamespaces, ns);
                }
            }
        }
        
        return newNamespaces;
    }
    
    /**
     * Get the set of namespace prefixes which are currently visibly used on the owning XMLObject (only the owner,
     * not its children).
     * 
     * @return the set of visibly used namespace prefixes
     */
    private Set<String> getVisibleNamespacePrefixes() {
        LazySet<String> prefixes = new LazySet<String>();

        // Add prefix from element name.
        if (getElementNameNamespace() != null) {
            String elementNamePrefix = DatatypeHelper.safeTrimOrNullString(
                    getElementNameNamespace().getNamespacePrefix());
            if (elementNamePrefix == null) {
                elementNamePrefix = DEFAULT_NS_TOKEN;
            }
            prefixes.add(elementNamePrefix);
        }

        // Add xsi attribute prefix, if element carries an xsi:type.
        if (getElementTypeNamespace() != null) {
            prefixes.add(XMLConstants.XSI_PREFIX);
        }
        
        // Add prefixes from attribute names
        addPrefixes(prefixes, attrNames);

        return prefixes;
    }

    /**
     * Get the set of non-visibly used namespace prefixes used on the owning XMLObject (only the owner,
     * not the owner's children).
     * 
     * @return the set of non-visilby used namespace prefixes
     */
    private Set<String> getNonVisibleNamespacePrefixCandidates() {
        LazySet<String> prefixes = new LazySet<String>();

        // Add xsi:type value's prefix, if element carries an xsi:type
        if (getElementTypeNamespace() != null) {
            String schemaTypePrefix = DatatypeHelper.safeTrimOrNullString(
                    getElementTypeNamespace().getNamespacePrefix());
            if (schemaTypePrefix == null) {
                schemaTypePrefix = DEFAULT_NS_TOKEN;
            }
            prefixes.add(schemaTypePrefix);
        }
        
        // Add prefixes from attribute and content values
        addPrefixes(prefixes, attrValues.values());
        if (contentValue != null) {
            String contentValuePrefix = DatatypeHelper.safeTrimOrNullString(contentValue.getNamespacePrefix());
            if (contentValuePrefix == null) {
                contentValuePrefix = DEFAULT_NS_TOKEN;
            }
            prefixes.add(contentValuePrefix);
        }

        return prefixes;
    }
    
    /**
     * Add the prefixes from a collection of namespaces to a set of prefixes. The 
     * value used to represent the default namespace will be normalized to {@link NamespaceManager#DEFAULT_NS_TOKEN}.
     * 
     * @param prefixes the set of prefixes to which to add
     * @param namespaces the source set of Namespaces
     */
    private void addPrefixes(Set<String> prefixes, Collection<Namespace> namespaces) {
        for (Namespace ns : namespaces) {
            String prefix = DatatypeHelper.safeTrimOrNullString(ns.getNamespacePrefix());
            if (prefix == null) {
                prefix = DEFAULT_NS_TOKEN;
            }
            prefixes.add(prefix);
        }
    }
    
    /**
     * Check whether the supplied QName contains non-empty namespace info and should
     * be managed by the namespace manager.
     * 
     * @param name the QName to check
     * @return true if the QName contains non-empty namespace info and should be managed, false otherwise
     */
    private boolean checkQName(QName name) {
        return !DatatypeHelper.isEmpty(name.getNamespaceURI());
    }
    
}
