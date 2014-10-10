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

package org.opensaml.core.xml.util;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;

import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.util.AttributeMap;

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
    
    private XMLObjectBuilder<XSAny> xsAnyBuilder;
    
    
    public AttributeMapTest() {
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        xsAnyBuilder = (XMLObjectBuilder<XSAny>) builderFactory.getBuilder(XSAny.TYPE_NAME);
        owner = xsAnyBuilder.buildObject(elementName);
        attributeMap = owner.getUnknownAttributes();
    }
    
    @Test
    public void testClear() {
        QName attrName1 = new QName(ns1, "Attr1", ns1Prefix);
        QName attrName2 = new QName(ns2, "Attr2", ns2Prefix);
        QName attrName3 = new QName(ns3, "Attr3", ns3Prefix);
        
        attributeMap.put(attrName1, "foo");
        attributeMap.put(attrName2, "foo");
        attributeMap.put(attrName3, "foo");
        
        Assert.assertEquals(attributeMap.size(), 3, "Wrong map size");
        
        owner.getUnknownAttributes().clear();
        
        Assert.assertEquals(attributeMap.size(), 0, "Wrong map size");
    }
    
    @Test
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
    
    @Test
    public void testQNameAttributeValueAsString() {
        QName attrName = new QName(ns2, "Attr2", ns2Prefix);
        QName attrValue = new QName(ns3, "foo", ns3Prefix);
        
        String attrValueString = attrValue.getPrefix() + ":" + attrValue.getLocalPart();
        
        //  Using this mechanism have to "pre-register" the namespace so that the attr value is detected properly
        owner.getNamespaceManager().registerNamespaceDeclaration(buildNamespace(attrValue));
        
        owner.getUnknownAttributes().put(attrName, attrValueString);
        checkNamespaces(owner, 3, elementName, attrName, attrValue);
    }
    
    @Test
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
            Assert.assertEquals(xo.getNamespaces().size(), size, "Wrong number of unique namespaces");
        }
        
        outer: 
        for (QName name : names) {
            Namespace nsExpected = buildNamespace(name);
            for (Namespace nsPresent : namespacesPresent) {
                if (equals(nsExpected, nsPresent)) {
                    continue outer;
                }
            }
            Assert.fail("Did not find expected namespace in object from QName: " +  name.toString());
        }
        
    }
    
    private Namespace buildNamespace(QName name) {
        String uri = StringSupport.trimOrNull(name.getNamespaceURI());
        if (uri == null) {
            throw new IllegalArgumentException("A non-empty namespace URI must be supplied");
        }
        String prefix = StringSupport.trimOrNull(name.getPrefix());
        return new Namespace(uri, prefix);
    }
    
    private boolean equals(Namespace ns1, Namespace ns2) {
        if (Objects.equals(ns1.getNamespaceURI(), ns1.getNamespaceURI()) 
                && Objects.equals(ns2.getNamespacePrefix(), ns2.getNamespacePrefix())) {
            return true;
        } else {
            return false;
        }
    }

}