/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml;

import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.LazySet;
import org.opensaml.xml.util.XMLConstants;

/**
 * Test the NamespaceManger used by XMLObjects.
 */
public class NamespaceManagerTest extends XMLObjectBaseTestCase {
    
    private XSAny xsAny;
    private NamespaceManager nsManager;
    
    private static String ns1uri = "urn:test:ns1uri";
    private static String ns1Prefix = "testNS1";
    
    private static String ns2uri = "urn:test:ns2uri";
    private static String ns2Prefix = "testNS2";
    
    private static String ns3uri = "urn:test:ns3uri";
    private static String ns3Prefix = "testNS3";
    
    private static String ns4uri = "urn:test:ns4uri";
    private static String ns4Prefix = "testNS4";
    
    private static QName elementName = new QName(ns1uri, "TestElementName", ns1Prefix);
    private static QName typeName = new QName(ns2uri, "TestTypeName", ns2Prefix);
    
    private static QName xsiTypeName = new QName(XMLConstants.XSI_NS, "type", XMLConstants.XSI_PREFIX);
    
    private XMLObjectBuilder<XSAny> xsAnyBuilder;
    
    public NamespaceManagerTest() {
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        xsAnyBuilder = builderFactory.getBuilder(XSAny.TYPE_NAME);
        xsAny = xsAnyBuilder.buildObject(elementName);
        nsManager = xsAny.getNamespaceManager();
    }
    
    public void testObjectName() {
        checkNamespaces(xsAny, 1, elementName);
    }
    
    public void testObjectType() {
        xsAny = xsAnyBuilder.buildObject(elementName, typeName);
        checkNamespaces(xsAny, 3, elementName, typeName, xsiTypeName);
    }
    
    public void testQNameElementContent() {
        QName content = new QName(ns2uri, "TestElementContent", ns2Prefix);
        
        nsManager.registerContentValue(content);
        
        checkNamespaces(xsAny, 2, elementName, content);
        
        nsManager.deregisterContentValue();
        
        checkNamespaces(xsAny, 1, elementName);
    }
    
    public void testQualifiedAttributes() {
        QName attrName1 = new QName(ns1uri, "Attr1", ns1Prefix);
        QName attrName2 = new QName(ns2uri, "Attr2", ns2Prefix);
        QName attrName3 = new QName(ns3uri, "Attr3", ns3Prefix);
        
        nsManager.registerAttributeName(attrName1);
        checkNamespaces(xsAny, 1, elementName, attrName1);
        
        nsManager.registerAttributeName(attrName2);
        nsManager.registerAttributeName(attrName3);
        checkNamespaces(xsAny, 3, elementName, attrName1, attrName2, attrName3);
        
        nsManager.deregisterAttributeName(attrName2);
        checkNamespaces(xsAny, 2, elementName, attrName1, attrName3);
        
        nsManager.deregisterAttributeName(attrName1);
        nsManager.deregisterAttributeName(attrName3);
        checkNamespaces(xsAny, 1, elementName);
    }
    
    public void testQNameAttributeValue() {
        QName attrName = new QName(ns1uri, "Attr1", ns1Prefix);
        QName attrValue1 = new QName(ns2uri, "Attr2", ns2Prefix);
        QName attrValue2 = new QName(ns3uri, "Attr3", ns3Prefix);
        
        nsManager.registerAttributeValue(NamespaceManager.generateAttributeID(attrName), attrValue1);
        checkNamespaces(xsAny, 2, elementName, attrValue1);
        
        //Test overwriting the previous registration by a new one
        nsManager.registerAttributeValue(NamespaceManager.generateAttributeID(attrName), attrValue2);
        checkNamespaces(xsAny, 2, elementName, attrValue2);
        
        nsManager.deregisterAttributeValue(NamespaceManager.generateAttributeID(attrName));
        checkNamespaces(xsAny, 1, elementName);
    }
    
    public void testNSDeclaration() {
        Namespace ns1 = new Namespace(ns1uri, ns1Prefix);
        Namespace ns2 = new Namespace(ns2uri, ns2Prefix);
        
        //Will be there b/c it's the ns of the element
        assertNotNull(findNamespace(nsManager, ns1));
        assertEquals(1, nsManager.getNamespaces().size());
        assertFalse(findNamespace(nsManager, ns1).alwaysDeclare());
        
        nsManager.registerNamespaceDeclaration(ns1);
        assertEquals(1, nsManager.getNamespaces().size());
        assertNotNull(findNamespace(nsManager, ns1));
        assertTrue(findNamespace(nsManager, ns1).alwaysDeclare());
        
        nsManager.registerNamespaceDeclaration(ns2);
        assertEquals(2, nsManager.getNamespaces().size());
        assertNotNull(findNamespace(nsManager, ns2));
        assertTrue(findNamespace(nsManager, ns2).alwaysDeclare());
        
        // Should still be there b/c of element name, but no longer always declared
        nsManager.deregisterNamespaceDeclaration(ns1);
        assertEquals(2, nsManager.getNamespaces().size());
        assertNotNull(findNamespace(nsManager, ns1));
        assertFalse(findNamespace(nsManager, ns1).alwaysDeclare());
        
        nsManager.deregisterNamespaceDeclaration(ns2);
        assertEquals(1, nsManager.getNamespaces().size());
        assertNull(findNamespace(nsManager, ns2));
    }
    
    public void testNSUnspecifiedUsage() {
        Namespace ns1 = new Namespace(ns1uri, ns1Prefix);
        Namespace ns2 = new Namespace(ns2uri, ns2Prefix);
        
        assertEquals(1, nsManager.getNamespaces().size());
        
        nsManager.registerNamespace(ns1);
        assertEquals(1, nsManager.getNamespaces().size());
        assertNotNull(findNamespace(nsManager, ns1));
        assertFalse(findNamespace(nsManager, ns1).alwaysDeclare());
        
        nsManager.registerNamespace(ns2);
        assertEquals(2, nsManager.getNamespaces().size());
        assertNotNull(findNamespace(nsManager, ns2));
        assertFalse(findNamespace(nsManager, ns2).alwaysDeclare());
        
        // Should still be there b/c of element name
        nsManager.deregisterNamespace(ns1);
        assertEquals(2, nsManager.getNamespaces().size());
        assertNotNull(findNamespace(nsManager, ns1));
        assertFalse(findNamespace(nsManager, ns1).alwaysDeclare());
        
        nsManager.deregisterNamespace(ns2);
        assertEquals(1, nsManager.getNamespaces().size());
        assertNull(findNamespace(nsManager, ns2));
    }
    
    public void testNonVisibleNamespacePrefixes() {
        QName name1 = new QName(ns1uri, "Test1", ns1Prefix);
        QName name2 = new QName(ns2uri, "Test2", ns2Prefix);
        QName name3 = new QName(ns3uri, "Test3", ns3Prefix);
        QName name4 = new QName(ns4uri, "Test4", ns4Prefix);
        Namespace ns = new Namespace(ns3uri, ns3Prefix);
        
        checkPrefixes(nsManager);
        
        nsManager.registerAttributeName(name1);
        checkPrefixes(nsManager);
        nsManager.deregisterAttributeName(name1);
        
        nsManager.registerAttributeValue("foo", name1);
        checkPrefixes(nsManager);
        nsManager.deregisterAttributeValue("foo");
        
        nsManager.registerAttributeName(name2);
        checkPrefixes(nsManager);
        nsManager.deregisterAttributeName(name2);
        
        nsManager.registerAttributeValue("foo", name2);
        checkPrefixes(nsManager, ns2Prefix);
        
        nsManager.registerContentValue(name3);
        checkPrefixes(nsManager, ns2Prefix, ns3Prefix);
        nsManager.deregisterAttributeValue("foo");
        nsManager.deregisterContentValue();
        
        checkPrefixes(nsManager);
        
        nsManager.registerNamespaceDeclaration(ns);
        checkPrefixes(nsManager);
        nsManager.deregisterNamespaceDeclaration(ns);
        
        nsManager.registerNamespace(ns);
        checkPrefixes(nsManager);
        nsManager.deregisterNamespace(ns);
        
        checkPrefixes(nsManager);
        
        XSAny typedXSAny = xsAnyBuilder.buildObject(elementName, typeName);
        checkPrefixes(typedXSAny.getNamespaceManager(), ns2Prefix);
        
        typedXSAny.getUnknownAttributes().put(name4, name3);
        checkPrefixes(typedXSAny.getNamespaceManager(), ns2Prefix, ns3Prefix);
        
        typedXSAny.getUnknownAttributes().put(name2, name3);
        checkPrefixes(typedXSAny.getNamespaceManager(), ns3Prefix);
        
        typedXSAny.getUnknownAttributes().remove(name2);
        checkPrefixes(typedXSAny.getNamespaceManager(), ns2Prefix, ns3Prefix);
        
        // Check that children are working vis-a-vis parent object
        xsAny.getUnknownXMLObjects().add(typedXSAny);
        checkPrefixes(nsManager, ns2Prefix, ns3Prefix);
        
        xsAny.getUnknownAttributes().put(name2, "foo");
        checkPrefixes(nsManager, ns3Prefix);
    }
    
    
    /**********************/
    
    private void checkPrefixes(NamespaceManager manager, String ... controlPrefixes) {
        Set<String> nonVisiblePrefixes = manager.getNonVisibleNamespacePrefixes();
        
        assertEquals("Wrong number of not visible prefixes", controlPrefixes.length, nonVisiblePrefixes.size());
        
        for (String prefix : controlPrefixes) {
            assertTrue("Expected prefix not seen: " + prefix, nonVisiblePrefixes.contains(prefix));
        }
    }
    
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
        if (DatatypeHelper.safeEquals(ns1.getNamespaceURI(), ns2.getNamespaceURI()) 
                && DatatypeHelper.safeEquals(ns1.getNamespacePrefix(), ns2.getNamespacePrefix())) {
            return true;
        } else {
            return false;
        }
    }
    
    private Namespace findNamespace(NamespaceManager manager, Namespace ns) {
        for (Namespace namespace : manager.getNamespaces()) {
            if (equals(namespace, ns)) {
                return namespace;
            }
        }
        return null;
    }


}
