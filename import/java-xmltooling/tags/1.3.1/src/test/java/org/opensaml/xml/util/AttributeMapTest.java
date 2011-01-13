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

package org.opensaml.xml.util;

import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.schema.XSAny;

/**
 * Test the NamespaceManger used by XMLObjects.
 */
public class AttributeMapTest extends XMLObjectBaseTestCase {
    
    private AttributeExtensibleXMLObject owner;
    private AttributeMap attributeMap;
    
    private static String ns1 = "urn:test:ns1";
    private static String ns1Prefix = "testNS1";
    
    private static String ns2 = "urn:test:ns2";
    private static String ns2Prefix = "testNS2";
    
    private static String ns3 = "urn:test:ns3";
    private static String ns3Prefix = "testNS3";
    
    private static QName elementName = new QName(ns1, "TestElementName", ns1Prefix);
    private static QName typeName = new QName(ns2, "TestTypeName", ns2Prefix);
    
    private XMLObjectBuilder<XSAny> xsAnyBuilder;
    
    
    public AttributeMapTest() {
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        xsAnyBuilder = builderFactory.getBuilder(XSAny.TYPE_NAME);
        owner = xsAnyBuilder.buildObject(elementName);
        attributeMap = owner.getUnknownAttributes();
    }
    
    public void testClear() {
        QName attrName1 = new QName(ns1, "Attr1", ns1Prefix);
        QName attrName2 = new QName(ns2, "Attr2", ns2Prefix);
        QName attrName3 = new QName(ns3, "Attr3", ns3Prefix);
        
        attributeMap.put(attrName1, "foo");
        attributeMap.put(attrName2, "foo");
        attributeMap.put(attrName3, "foo");
        
        assertEquals("Wrong map size", 3, attributeMap.size());
        
        owner.getUnknownAttributes().clear();
        
        assertEquals("Wrong map size", 0, attributeMap.size());
    }
    
    public void testAttributeMapQualifiedAttributes() {
        QName attrName1 = new QName(ns1, "Attr1", ns1Prefix);
        QName attrName2 = new QName(ns2, "Attr2", ns2Prefix);
        QName attrName3 = new QName(ns3, "Attr3", ns3Prefix);
        
        // Attr 1 is from same namespace as element, so not unique
        attributeMap.put(attrName1, "foo");
        checkNamespaces(owner, 1, elementName, attrName1);
        
        attributeMap.remove(attrName1);
        checkNamespaces(owner, 1, elementName);
        
        attributeMap.put(attrName2, "foo");
        checkNamespaces(owner, 2, elementName, attrName2);
        
        attributeMap.remove(attrName2);
        checkNamespaces(owner, 1, elementName);
        
        attributeMap.put(attrName2, "foo");
        attributeMap.put(attrName3, "foo");
        checkNamespaces(owner, 3, elementName, attrName2, attrName3);
        
        attributeMap.clear();
        checkNamespaces(owner, 1, elementName);
    }
    
    public void testQNameAttributeValueAsString() {
        QName attrName = new QName(ns2, "Attr2", ns2Prefix);
        QName attrValue = new QName(ns3, "foo", ns3Prefix);
        
        String attrValueString = attrValue.getPrefix() + ":" + attrValue.getLocalPart();
        
        //  Using this mechanism have to "pre-register" the namespace so that the attr value is detected properly
        owner.getNamespaceManager().registerNamespace(buildNamespace(attrValue));
        
        owner.getUnknownAttributes().put(attrName, attrValueString);
        checkNamespaces(owner, 3, elementName, attrName, attrValue);
    }
    
    public void testQNameAttributeValueAsQName() {
        QName attrName = new QName(ns2, "Attr2", ns2Prefix);
        QName attrValue = new QName(ns3, "foo", ns3Prefix);
        
        owner.getUnknownAttributes().put(attrName, attrValue);
        
        checkNamespaces(owner, 3, elementName, attrName, attrValue);
        
        owner.getUnknownAttributes().put(attrName, (QName) null);
        
        checkNamespaces(owner, 1, elementName);
    }
    
    
    /*****************************************************/
    
    /**
     * Check the namespaces produced by the object against the supplied list of QNames.
     * 
     * @param xo the XMLObject to evaluate
     * @param nsSize the expected size of the XMLObject's namespace set 
     *     (may be different than the size of the names list due to duplicates in the latter)
     * @param names the list of names to check
     */
    private void checkNamespaces(XMLObject xo, Integer nsSize, QName ... names) {
        Set<Namespace> namespacesPresent = xo.getNamespaces();
        
        if (nsSize != null) {
            int size = nsSize.intValue();
            assertEquals("Wrong number of unique namespaces", size, xo.getNamespaces().size());
        }
        
        outer: 
        for (QName name : names) {
            Namespace nsExpected = buildNamespace(name);
            for (Namespace nsPresent : namespacesPresent) {
                if (equals(nsExpected, nsPresent)) {
                    continue outer;
                }
            }
            fail("Did not find expected namespace in object from QName: " +  name.toString());
        }
        
    }
    
    private Set<Namespace> buildNamespaceSet(QName ... names) {
        LazySet<Namespace> namespaces = new LazySet<Namespace>();
        for (QName name : names) {
            if (name != null) {
                namespaces.add(buildNamespace(name));
            }
        }
        return namespaces;
    }
    
    private Namespace buildNamespace(QName name) {
        String uri = DatatypeHelper.safeTrimOrNullString(name.getNamespaceURI());
        if (uri == null) {
            throw new IllegalArgumentException("A non-empty namespace URI must be supplied");
        }
        String prefix = DatatypeHelper.safeTrimOrNullString(name.getPrefix());
        return new Namespace(uri, prefix);
    }
    
    private boolean equals(Namespace ns1, Namespace ns2) {
        if (DatatypeHelper.safeEquals(ns1.getNamespaceURI(), ns1.getNamespaceURI()) 
                && DatatypeHelper.safeEquals(ns2.getNamespacePrefix(), ns2.getNamespacePrefix())) {
            return true;
        } else {
            return false;
        }
    }

}
