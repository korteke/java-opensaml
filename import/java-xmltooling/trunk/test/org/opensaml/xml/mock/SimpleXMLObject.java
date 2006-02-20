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

/**
 * 
 */
package org.opensaml.xml.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.signature.AbstractSignableXMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * Simple XMLObject that can be used for testing
 */
public class SimpleXMLObject extends AbstractSignableXMLObject {
    
    public final static String NAMESAPACE = "http://www.example.org/testObjects";
    
    /** Element local name */
    public final static String LOCAL_NAME = "SimpleElement";
    
    /** Name attribute name */
    public final static String ID_ATTRIB_NAME = "Id";
    
    /** Name attribute */
    private String id;
    
    /** Child SimpleXMLObjects */
    private XMLObjectChildrenList<SimpleXMLObject> simpleXMLObjects;
    
    private Signature signature;
    
    /**
     * Constructor
     */
    public SimpleXMLObject() {
        super(NAMESAPACE, LOCAL_NAME);
        setElementNamespacePrefix("test");
        simpleXMLObjects = new XMLObjectChildrenList<SimpleXMLObject>(this);
    }
    
    /**
     * Gets the name attribute.
     * 
     * @return the name attribute
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the name attribute.
     * 
     * @param newId the name attribute
     */
    public void setId(String newId) {
        id = newId;
    }
    
    /**
     * Gets the list of child SimpleXMLObjects.
     * 
     * @return the list of child SimpleXMLObjects
     */
    public List<SimpleXMLObject> getSimpleXMLObjects(){
        return simpleXMLObjects;
    }
    
    public Signature getSignature() {
        return signature;
    }
    
    public void setSignature(Signature signature) {
        this.signature = prepareForAssignment(this.signature, signature);
    }

    /*
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
        
        children.addAll(simpleXMLObjects);
        children.add(signature);
        
        return Collections.unmodifiableList(children);
    }
}
