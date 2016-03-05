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

package org.opensaml.core.xml;

import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.QNameSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.w3c.dom.Document;

/**
 * Unit test for unmarshalling functions.
 */
public class IDAttributeTest extends XMLObjectBaseTestCase {
    
    /**
     * Constructor.
     */
    public IDAttributeTest() {
        super();
    }

    /**
     * Simple test of ID attribute on a single object.
     */
    @Test
    public void testSimpleUnmarshall() {
        SimpleXMLObject sxObject =  (SimpleXMLObject) unmarshallElement("/org/opensaml/core/xml/IDAttribute.xml");

        Assert.assertEquals(sxObject.resolveID("IDLevel1"), sxObject, "ID lookup failed");
        Assert.assertEquals(sxObject.resolveIDFromRoot("IDLevel1"), sxObject, "ID lookup failed");
        Assert.assertNull(sxObject.resolveID("NonExistent"), "Lookup of non-existent ID didn't return null");
        
        sxObject.setId(null);
        Assert.assertNull(sxObject.resolveID("IDLevel1"), "Lookup of removed ID (formerly extant) didn't return null");
        Assert.assertNull(sxObject.resolveIDFromRoot("IDLevel1"), "Lookup of removed ID (formerly extant) didn't return null");
    }
    
    /**
     * Test of ID attributes on complex nested unmarshalled elements 
     * where children are stored in an XMLObjectChildren list.
     */
    @Test
    public void testComplexUnmarshallInList() {
        SimpleXMLObject sxObject = 
            (SimpleXMLObject) unmarshallElement("/org/opensaml/core/xml/IDAttributeWithChildrenList.xml");
        
        Assert.assertNull(sxObject.resolveID("NonExistent"), "Lookup of non-existent ID didn't return null");
        Assert.assertNull(sxObject.resolveIDFromRoot("NonExistent"), "Lookup of non-existent ID didn't return null");
        
        // Resolving from top-level root
        Assert.assertEquals(sxObject.resolveID("IDLevel1"), sxObject, 
                "ID lookup failed");
        Assert.assertEquals(sxObject.resolveID("IDLevel2A"), sxObject.getSimpleXMLObjects().get(0), 
                "ID lookup failed");
        Assert.assertEquals(sxObject.resolveID("IDLevel2B"), sxObject.getSimpleXMLObjects().get(1), 
                "ID lookup failed");
        Assert.assertEquals(sxObject.resolveID("IDLevel2C"), sxObject.getSimpleXMLObjects().get(3), 
                "ID lookup failed");
        Assert.assertEquals(sxObject.resolveID("IDLevel3A"), sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0), 
                "ID lookup failed");
        Assert.assertEquals(sxObject.resolveID("IDLevel3B"), sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(2), 
                "ID lookup failed");
        Assert.assertEquals(sxObject.resolveID("IDLevel3C"), sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3), 
                "ID lookup failed");
        Assert.assertEquals(sxObject.resolveID("IDLevel4A"), sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0)
                .getSimpleXMLObjects().get(0), 
                "ID lookup failed");
        
        // Resolving from secondary level root
        Assert.assertEquals(sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel2A"), sxObject.getSimpleXMLObjects().get(0), 
                "ID lookup failed");
        Assert.assertEquals(sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel3A"), sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0), 
                "ID lookup failed");
        Assert.assertEquals(sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel3B"), sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(2), 
                "ID lookup failed");
        Assert.assertEquals(sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel3C"), sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3), 
                "ID lookup failed");
        Assert.assertEquals(sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel4A"), sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0)
                .getSimpleXMLObjects().get(0), 
                "ID lookup failed");
        Assert.assertNull(sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel1"), 
                "Lookup of non-existent ID didn't return null");
        
        // Resolving from lower-level child to a non-ancestor object using resolveIDFromRoot.
        SimpleXMLObject sxoIDLevel4A = sxObject.getSimpleXMLObjects().get(0)
            .getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0); 
        SimpleXMLObject sxoIDLevel2C = sxObject.getSimpleXMLObjects().get(3);
        Assert.assertEquals(sxoIDLevel4A.resolveIDFromRoot("IDLevel2C"), sxoIDLevel2C, "ID lookup failed");
    }
        
        
    /**
     *  Test propagation of various changes to ID attribute mappings due to attribute value changes
     *  where children are stored in an XMLObjectChildren list. 
     */
    @Test
    public void testChangePropagationInList() {
        SimpleXMLObject sxObject =  
            (SimpleXMLObject) unmarshallElement("/org/opensaml/core/xml/IDAttributeWithChildrenList.xml");
        
        // Test propagation of attribute value change up the tree 
        sxObject.getSimpleXMLObjects().get(1).setId("NewIDLevel2B");
        Assert.assertEquals(sxObject.resolveID("NewIDLevel2B"), sxObject.getSimpleXMLObjects().get(1), 
                "ID lookup failed");
        Assert.assertNull(sxObject.resolveID("IDLevel2B"), "Lookup of non-existent ID didn't return null");
        
        sxObject.getSimpleXMLObjects().get(1).setId(null);
        Assert.assertNull(sxObject.resolveID("NewIDLevel2B"), "Lookup of non-existent ID didn't return null");
        Assert.assertNull(sxObject.resolveID("IDLevel2B"), "Lookup of non-existent ID didn't return null");
        
        sxObject.getSimpleXMLObjects().get(1).setId("IDLevel2B");
        Assert.assertEquals(sxObject.resolveID("IDLevel2B"), sxObject.getSimpleXMLObjects().get(1), 
                "ID lookup failed");
        Assert.assertNull(sxObject.resolveID("NewIDLevel2B"), "Lookup of non-existent ID didn't return null");
        
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3).setId("NewIDLevel3C");
        Assert.assertEquals(sxObject.resolveID("NewIDLevel3C"), sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3), 
                "ID lookup failed");
        Assert.assertNull(sxObject.resolveID("IDLevel3C"), "Lookup of non-existent ID didn't return null");
        
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0).setId(null);
        Assert.assertNull(sxObject.resolveID("IDLevel3A"), "Lookup of non-existent ID didn't return null");
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0).setId("IDLevel3A");
        Assert.assertEquals(sxObject.resolveID("IDLevel3A"), sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0), 
                "ID lookup failed");
    }
        
    /**
     *  Test propagation of various changes to ID attribute mappings due to list operations
     *  where children are stored in an XMLObjectChildren list. 
     */
    @Test
    public void testListOpChangePropagation() {
        
        SimpleXMLObject sxObject =  
            (SimpleXMLObject) unmarshallElement("/org/opensaml/core/xml/IDAttributeWithChildrenList.xml");
        
        SimpleXMLObject targetIDLevel3B = sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(2);
        Assert.assertEquals(sxObject.resolveID("IDLevel3B"), targetIDLevel3B, "ID lookup failed");
        
        // remove(int)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().remove(2);
        Assert.assertNull(sxObject.resolveID("IDLevel3B"), "Lookup of non-existent ID didn't return null");
        // add(XMLObject)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().add(targetIDLevel3B);
        Assert.assertEquals(sxObject.resolveID("IDLevel3B"), targetIDLevel3B, "ID lookup failed");
        // remove(XMLObject)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().remove(targetIDLevel3B);
        Assert.assertNull(sxObject.resolveID("IDLevel3B"), "Lookup of non-existent ID didn't return null");
        // set(int, XMLObject)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().set(1, targetIDLevel3B);
        Assert.assertEquals(sxObject.resolveID("IDLevel3B"), targetIDLevel3B, "ID lookup failed");
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().remove(targetIDLevel3B);
        Assert.assertNull(sxObject.resolveID("IDLevel3B"), "Lookup of non-existent ID didn't return null");
        
        // Ops using new object
        SimpleXMLObject newSimpleObject = (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME);
        newSimpleObject.setId("NewSimpleElement");
        
        sxObject.getSimpleXMLObjects().get(3).getSimpleXMLObjects().add(newSimpleObject);
        Assert.assertEquals(sxObject.resolveID("NewSimpleElement"), newSimpleObject, "ID lookup failed");
        sxObject.getSimpleXMLObjects().get(3).getSimpleXMLObjects().remove(newSimpleObject);
        Assert.assertNull(sxObject.resolveID("NewSimpleElement"), "Lookup of non-existent ID didn't return null");
        
        // clear
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().clear();
        Assert.assertNull(sxObject.resolveID("IDLevel3A"), "Lookup of non-existent ID didn't return null");
        Assert.assertNull(sxObject.resolveID("IDLevel3B"), "Lookup of non-existent ID didn't return null");
        Assert.assertNull(sxObject.resolveID("IDLevel3C"), "Lookup of non-existent ID didn't return null");
        Assert.assertNull(sxObject.resolveID("IDLevel4A"), "Lookup of non-existent ID didn't return null");
    }
    
    /**
     * Tests registering ID-to-XMLObject mapping when unmarshalling unknown content,
     * using the AttributeMap supplied by way of the AttributeExtensibleXMLObject interface.
     * This tests general AttributeMap functionality on unmarshalling.
     * 
     * For purposes of this test, the attribute in the control XML file with
     * local name "id" on element "product" will be treated as an ID type.
     * 
     * @throws XMLParserException when parser encounters an error
     * @throws UnmarshallingException when unmarshaller encounters an error
     */
    @Test
    public void testAttributeMap() throws XMLParserException, UnmarshallingException{
        String documentLocation = "/org/opensaml/core/xml/IDAttributeWithAttributeMap.xml";
        Document document = parserPool.parse(IDAttributeTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(XMLObjectProviderRegistrySupport.getDefaultProviderQName());
        XMLObject xmlobject = unmarshaller.unmarshall(document.getDocumentElement());
        
        XSAny epParent = (XSAny) xmlobject;
        Assert.assertNotNull(epParent, "Cast of parent to XSAny failed");
        
        XSAny epChild0 = (XSAny) epParent.getUnknownXMLObjects().get(0);
        Assert.assertNotNull(epChild0, "Cast of child 0 to XSAny failed");
        
        XSAny epChild1 = (XSAny) epParent.getUnknownXMLObjects().get(1);
        Assert.assertNotNull(epChild1, "Cast of child 1 to XSAny failed");
        
        // Since not doing schema validation, etc, the parser won't register the ID type in the DOM
        // (i.e. DOM Attribute.isId() will fail) and so the unmarshaller won't be able to register 
        // the "id" attribute as an ID type. This is expected.
        Assert.assertNull(epParent.resolveID("1144"), "Lookup of non-existent ID mapping didn't return null");
        Assert.assertNull(epParent.resolveID("1166"), "Lookup of non-existent ID mapping didn't return null");
        
        // Now manually register the "id" attribute in the AttributeMap of child 0 as being an ID type.
        // This should cause the expected ID-to-XMLObject mapping behaviour to take place.
        QName idName = QNameSupport.constructQName(null, "id", null);
        epChild0.getUnknownAttributes().registerID(idName);
        Assert.assertEquals(epParent.resolveID("1144"), epChild0, "Lookup of ID mapping failed");
        
        // Resolving from child1 to child0, which is not an ancestor of child1, using resolveIDFromRoot
        Assert.assertNull(epChild1.resolveID("1144"), "Lookup of non-existent ID mapping didn't return null");
        Assert.assertEquals(epChild1.resolveIDFromRoot("1144"), epChild0, "Lookup of ID mapping failed");
    }
    
    /**
     * Tests registering ID-to-XMLObject mapping when unmarshalling unknown content,
     * using the AttributeMap supplied by way of the AttributeExtensibleXMLObject interface.
     * This test tests propagation of changes on the various AttributeMap operations.
     * 
     * For purposes of this test, the attribute in the control XML file with
     * local name "id" on element "product" will be treated as an ID type.
     * 
     * @throws XMLParserException when parser encounters an error
     * @throws UnmarshallingException when unmarshaller encounters an error
     */
    @Test
    public void testAttributeMapOps() throws XMLParserException, UnmarshallingException{
        String documentLocation = "/org/opensaml/core/xml/IDAttributeWithAttributeMap.xml";
        Document document = parserPool.parse(IDAttributeTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(XMLObjectProviderRegistrySupport.getDefaultProviderQName());
        XMLObject xmlobject = unmarshaller.unmarshall(document.getDocumentElement());
        
        XSAny epParent = (XSAny) xmlobject;
        Assert.assertNotNull(epParent, "Cast of parent to XSAny failed");
        
        XSAny epChild0 = (XSAny) epParent.getUnknownXMLObjects().get(0);
        Assert.assertNotNull(epChild0, "Cast of child 0 to XSAny failed");
        
        // Now manually register the "id" attribute in the AttributeMap of child 0 as being an ID type.
        // This should cause the expected ID-to-XMLObject mapping behaviour to take place.
        QName idName = QNameSupport.constructQName(null, "id", null);
        epChild0.getUnknownAttributes().registerID(idName);
        Assert.assertEquals(epParent.resolveID("1144"), epChild0, "Lookup of ID mapping failed");
        
        // AttributeMap op tests
        // put
        epChild0.getUnknownAttributes().put(idName, "9999");
        Assert.assertNull(epParent.resolveID("1144"), "Lookup of non-existent ID mapping didn't return null");
        Assert.assertEquals(epParent.resolveID("9999"), epChild0, "Lookup of ID mapping failed");
        // remove
        epChild0.getUnknownAttributes().remove(idName);
        Assert.assertNull(epParent.resolveID("9999"), "Lookup of non-existent ID mapping didn't return null");
        // putAll
        Map<QName, String> attribs = new HashMap<>();
        attribs.put(idName, "1967");
        epChild0.getUnknownAttributes().putAll(attribs);
        Assert.assertEquals(epParent.resolveID("1967"), epChild0, "Lookup of ID mapping failed");
        // clear
        epChild0.getUnknownAttributes().clear();
        Assert.assertNull(epParent.resolveID("1967"), "Lookup of non-existent ID mapping didn't return null");
        // deregisterID
        epChild0.getUnknownAttributes().put(idName, "abc123");
        Assert.assertEquals(epParent.resolveID("abc123"), epChild0, "Lookup of ID mapping failed");
        epChild0.getUnknownAttributes().deregisterID(idName);
        Assert.assertNull(epParent.resolveID("abc123"), "Lookup of non-existent ID mapping didn't return null");
    }
    
    /**
     * Tests that attributes registered globally on {@link org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport} are being
     * handled properly in the AttributeMap.
     * @throws XMLParserException 
     * @throws UnmarshallingException 
     */
    @Test
    public void testGlobalIDRegistration() throws XMLParserException, UnmarshallingException {
        XMLObject xmlObject;
        QName attribQName = new QName("http://www.example.org", "id", "test");
        
        String documentLocation = "/org/opensaml/core/xml/IDAttributeGlobal.xml";
        Document document = parserPool.parse(IDAttributeTest.class.getResourceAsStream(documentLocation));
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(XMLObjectProviderRegistrySupport.getDefaultProviderQName());
        
        // With no registration
        xmlObject = unmarshaller.unmarshall(document.getDocumentElement());
        Assert.assertNull(xmlObject.resolveID("GlobalID1"), "Lookup of non-existent ID mapping didn't return null");
        Assert.assertNull(xmlObject.resolveID("GlobalID2"), "Lookup of non-existent ID mapping didn't return null");
        
        // Now register the attribute QName in the global config
        XMLObjectProviderRegistrySupport.registerIDAttribute(attribQName);
        document = parserPool.parse(IDAttributeTest.class.getResourceAsStream(documentLocation));
        xmlObject = unmarshaller.unmarshall(document.getDocumentElement());
        Assert.assertEquals(xmlObject.resolveID("GlobalID1"), xmlObject, "Lookup of ID mapping failed");
        Assert.assertEquals(xmlObject.resolveID("GlobalID2"), ((XSAny) xmlObject).getUnknownXMLObjects().get(0),
                "Lookup of ID mapping failed");
        
        // After deregistration
        XMLObjectProviderRegistrySupport.deregisterIDAttribute(attribQName);
        document = parserPool.parse(IDAttributeTest.class.getResourceAsStream(documentLocation));
        xmlObject = unmarshaller.unmarshall(document.getDocumentElement());
        Assert.assertNull(xmlObject.resolveID("GlobalID1"), "Lookup of non-existent ID mapping didn't return null");
        Assert.assertNull(xmlObject.resolveID("GlobalID2"), "Lookup of non-existent ID mapping didn't return null");
    }
        
}