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

import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.LazySet;

/**
 * Test the NamespaceManger used by XMLObjects.
 */
public class NamespaceManagerTest extends XMLObjectBaseTestCase {
    
    private XSAny xsAny;
    
    private static String ns1 = "urn:test:ns1";
    private static String ns1Prefix = "testNS1";
    
    private static String ns2 = "urn:test:ns2";
    private static String ns2Prefix = "testNS2";
    
    private static String ns3 = "urn:test:ns3";
    private static String ns3Prefix = "testNS3";
    
    private static QName elementName = new QName(ns1, "TestElementName", ns1Prefix);
    private static QName typeName = new QName(ns2, "TestTypeName", ns2Prefix);
    
    private XMLObjectBuilder<XSAny> xsAnyBuilder;
    
    public NamespaceManagerTest() {
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        xsAnyBuilder = builderFactory.getBuilder(XSAny.TYPE_NAME);
        xsAny = xsAnyBuilder.buildObject(elementName);
    }
    
    public void testObjectName() {
        checkNamespaces(xsAny, 1, elementName);
    }
    
    public void testObjectType() {
        xsAny = xsAnyBuilder.buildObject(elementName, typeName);
        checkNamespaces(xsAny, 2, elementName, typeName);
    }
    
    public void testQNameElementContent() {
        //TODO
    }
    
    public void testQualifiedAttributes() {
        //TODO
    }
    
    public void testQNameAttributeValue() {
        //TODO
    }
    
    public void testNSDeclaration() {
       //TODO 
    }
    
    public void testNSGeneralUsage() {
       //TODO 
    }
    
    public void testAttributeMapQualifiedAttributes() {
        QName attrName1 = new QName(ns1, "Attr1", ns1Prefix);
        QName attrName2 = new QName(ns2, "Attr2", ns2Prefix);
        QName attrName3 = new QName(ns3, "Attr3", ns3Prefix);
        
        // Attr 1 is from same namespace as element, so not unique
        xsAny.getUnknownAttributes().put(attrName1, "foo");
        checkNamespaces(xsAny, 1, elementName, attrName1);
        
        xsAny.getUnknownAttributes().remove(attrName1);
        checkNamespaces(xsAny, 1, elementName);
        
        xsAny.getUnknownAttributes().put(attrName2, "foo");
        checkNamespaces(xsAny, 2, elementName, attrName2);
        
        xsAny.getUnknownAttributes().remove(attrName2);
        checkNamespaces(xsAny, 1, elementName);
        
        xsAny.getUnknownAttributes().put(attrName2, "foo");
        xsAny.getUnknownAttributes().put(attrName3, "foo");
        checkNamespaces(xsAny, 3, elementName, attrName2, attrName3);
        
        //TODO there is unrelated bug in AttributeMap on concurrent modification over attributes map
        //xsAny.getUnknownAttributes().clear();
        //checkNamespaces(xsAny, 1, elementName);
    }
    
    public void testAttributeMapQNameAttributeValue() {
        QName attrName = new QName(ns2, "Attr2", ns2Prefix);
        QName attrValue = new QName(ns3, "foo", ns3Prefix);
        
        String attrValueString = attrValue.getPrefix() + ":" + attrValue.getLocalPart();
        
        // TODO right now have to "pre-register" the namespace so that the attr value is detected properly
        // Need a better way.
        xsAny.getNamespaceManager().registerNamespace(buildNamespace(attrValue));
        
        xsAny.getUnknownAttributes().put(attrName, attrValueString);
        checkNamespaces(xsAny, 3, elementName, attrName, attrValue);
    }
    

    
    /**********************/
    
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
            assertEquals("Wrong number of unique namespaces", size, xsAny.getNamespaces().size());
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
