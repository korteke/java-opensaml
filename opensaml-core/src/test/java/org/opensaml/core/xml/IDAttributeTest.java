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
import org.testng.AssertJUnit;
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
        SimpleXMLObject sxObject =  (SimpleXMLObject) unmarshallElement("/data/org/opensaml/core/xml/IDAttribute.xml");

        AssertJUnit.assertEquals("ID lookup failed", sxObject, sxObject.resolveID("IDLevel1"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject, sxObject.resolveIDFromRoot("IDLevel1"));
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NonExistent"));
        
        sxObject.setId(null);
        AssertJUnit.assertNull("Lookup of removed ID (formerly extant) didn't return null", sxObject.resolveID("IDLevel1"));
        AssertJUnit.assertNull("Lookup of removed ID (formerly extant) didn't return null", sxObject.resolveIDFromRoot("IDLevel1"));
    }
    
    /**
     * Test of ID attributes on complex nested unmarshalled elements 
     * where children are stored in an XMLObjectChildren list.
     */
    @Test
    public void testComplexUnmarshallInList() {
        SimpleXMLObject sxObject = 
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/core/xml/IDAttributeWithChildrenList.xml");
        
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NonExistent"));
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveIDFromRoot("NonExistent"));
        
        // Resolving from top-level root
        AssertJUnit.assertEquals("ID lookup failed", sxObject, 
                sxObject.resolveID("IDLevel1"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0), 
                sxObject.resolveID("IDLevel2A"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(1), 
                sxObject.resolveID("IDLevel2B"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(3), 
                sxObject.resolveID("IDLevel2C"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0), 
                sxObject.resolveID("IDLevel3A"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(2), 
                sxObject.resolveID("IDLevel3B"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3), 
                sxObject.resolveID("IDLevel3C"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0)
                .getSimpleXMLObjects().get(0), 
                sxObject.resolveID("IDLevel4A"));
        
        // Resolving from secondary level root
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0), 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel2A"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0), 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel3A"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(2), 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel3B"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3), 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel3C"));
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0)
                .getSimpleXMLObjects().get(0), 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel4A"));
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel1"));
        
        // Resolving from lower-level child to a non-ancestor object using resolveIDFromRoot.
        SimpleXMLObject sxoIDLevel4A = sxObject.getSimpleXMLObjects().get(0)
            .getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0); 
        SimpleXMLObject sxoIDLevel2C = sxObject.getSimpleXMLObjects().get(3);
        AssertJUnit.assertEquals("ID lookup failed", sxoIDLevel2C, sxoIDLevel4A.resolveIDFromRoot("IDLevel2C"));
    }
        
        
    /**
     *  Test propagation of various changes to ID attribute mappings due to attribute value changes
     *  where children are stored in an XMLObjectChildren list. 
     */
    @Test
    public void testChangePropagationInList() {
        SimpleXMLObject sxObject =  
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/core/xml/IDAttributeWithChildrenList.xml");
        
        // Test propagation of attribute value change up the tree 
        sxObject.getSimpleXMLObjects().get(1).setId("NewIDLevel2B");
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(1), 
                sxObject.resolveID("NewIDLevel2B"));
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel2B"));
        
        sxObject.getSimpleXMLObjects().get(1).setId(null);
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NewIDLevel2B"));
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel2B"));
        
        sxObject.getSimpleXMLObjects().get(1).setId("IDLevel2B");
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(1), 
                sxObject.resolveID("IDLevel2B"));
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NewIDLevel2B"));
        
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3).setId("NewIDLevel3C");
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3), 
                sxObject.resolveID("NewIDLevel3C"));
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3C"));
        
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0).setId(null);
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3A"));
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0).setId("IDLevel3A");
        AssertJUnit.assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0), 
                sxObject.resolveID("IDLevel3A"));
    }
        
    /**
     *  Test propagation of various changes to ID attribute mappings due to list operations
     *  where children are stored in an XMLObjectChildren list. 
     */
    @Test
    public void testListOpChangePropagation() {
        
        SimpleXMLObject sxObject =  
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/core/xml/IDAttributeWithChildrenList.xml");
        
        SimpleXMLObject targetIDLevel3B = sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(2);
        AssertJUnit.assertEquals("ID lookup failed", targetIDLevel3B, sxObject.resolveID("IDLevel3B"));
        
        // remove(int)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().remove(2);
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3B"));
        // add(XMLObject)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().add(targetIDLevel3B);
        AssertJUnit.assertEquals("ID lookup failed", targetIDLevel3B, sxObject.resolveID("IDLevel3B"));
        // remove(XMLObject)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().remove(targetIDLevel3B);
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3B"));
        // set(int, XMLObject)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().set(1, targetIDLevel3B);
        AssertJUnit.assertEquals("ID lookup failed", targetIDLevel3B, sxObject.resolveID("IDLevel3B"));
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().remove(targetIDLevel3B);
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3B"));
        
        // Ops using new object
        SimpleXMLObject newSimpleObject = (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME);
        newSimpleObject.setId("NewSimpleElement");
        
        sxObject.getSimpleXMLObjects().get(3).getSimpleXMLObjects().add(newSimpleObject);
        AssertJUnit.assertEquals("ID lookup failed", newSimpleObject, sxObject.resolveID("NewSimpleElement"));
        sxObject.getSimpleXMLObjects().get(3).getSimpleXMLObjects().remove(newSimpleObject);
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NewSimpleElement"));
        
        // clear
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().clear();
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3A"));
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3B"));
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3C"));
        AssertJUnit.assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel4A"));
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
        String documentLocation = "/data/org/opensaml/core/xml/IDAttributeWithAttributeMap.xml";
        Document document = parserPool.parse(IDAttributeTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(XMLObjectProviderRegistrySupport.getDefaultProviderQName());
        XMLObject xmlobject = unmarshaller.unmarshall(document.getDocumentElement());
        
        XSAny epParent = (XSAny) xmlobject;
        AssertJUnit.assertNotNull("Cast of parent to XSAny failed", epParent);
        
        XSAny epChild0 = (XSAny) epParent.getUnknownXMLObjects().get(0);
        AssertJUnit.assertNotNull("Cast of child 0 to XSAny failed", epChild0);
        
        XSAny epChild1 = (XSAny) epParent.getUnknownXMLObjects().get(1);
        AssertJUnit.assertNotNull("Cast of child 1 to XSAny failed", epChild1);
        
        // Since not doing schema validation, etc, the parser won't register the ID type in the DOM
        // (i.e. DOM Attribute.isId() will fail) and so the unmarshaller won't be able to register 
        // the "id" attribute as an ID type. This is expected.
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", epParent.resolveID("1144"));
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", epParent.resolveID("1166"));
        
        // Now manually register the "id" attribute in the AttributeMap of child 0 as being an ID type.
        // This should cause the expected ID-to-XMLObject mapping behaviour to take place.
        QName idName = QNameSupport.constructQName(null, "id", null);
        epChild0.getUnknownAttributes().registerID(idName);
        AssertJUnit.assertEquals("Lookup of ID mapping failed", epChild0, epParent.resolveID("1144"));
        
        // Resolving from child1 to child0, which is not an ancestor of child1, using resolveIDFromRoot
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", epChild1.resolveID("1144"));
        AssertJUnit.assertEquals("Lookup of ID mapping failed", epChild0, epChild1.resolveIDFromRoot("1144"));
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
        String documentLocation = "/data/org/opensaml/core/xml/IDAttributeWithAttributeMap.xml";
        Document document = parserPool.parse(IDAttributeTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(XMLObjectProviderRegistrySupport.getDefaultProviderQName());
        XMLObject xmlobject = unmarshaller.unmarshall(document.getDocumentElement());
        
        XSAny epParent = (XSAny) xmlobject;
        AssertJUnit.assertNotNull("Cast of parent to XSAny failed", epParent);
        
        XSAny epChild0 = (XSAny) epParent.getUnknownXMLObjects().get(0);
        AssertJUnit.assertNotNull("Cast of child 0 to XSAny failed", epChild0);
        
        // Now manually register the "id" attribute in the AttributeMap of child 0 as being an ID type.
        // This should cause the expected ID-to-XMLObject mapping behaviour to take place.
        QName idName = QNameSupport.constructQName(null, "id", null);
        epChild0.getUnknownAttributes().registerID(idName);
        AssertJUnit.assertEquals("Lookup of ID mapping failed", epChild0, epParent.resolveID("1144"));
        
        // AttributeMap op tests
        // put
        epChild0.getUnknownAttributes().put(idName, "9999");
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", epParent.resolveID("1144"));
        AssertJUnit.assertEquals("Lookup of ID mapping failed", epChild0, epParent.resolveID("9999"));
        // remove
        epChild0.getUnknownAttributes().remove(idName);
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", epParent.resolveID("9999"));
        // putAll
        Map<QName, String> attribs = new HashMap<QName, String>();
        attribs.put(idName, "1967");
        epChild0.getUnknownAttributes().putAll(attribs);
        AssertJUnit.assertEquals("Lookup of ID mapping failed", epChild0, epParent.resolveID("1967"));
        // clear
        epChild0.getUnknownAttributes().clear();
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", epParent.resolveID("1967"));
        // deregisterID
        epChild0.getUnknownAttributes().put(idName, "abc123");
        AssertJUnit.assertEquals("Lookup of ID mapping failed", epChild0, epParent.resolveID("abc123"));
        epChild0.getUnknownAttributes().deregisterID(idName);
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", epParent.resolveID("abc123"));
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
        
        String documentLocation = "/data/org/opensaml/core/xml/IDAttributeGlobal.xml";
        Document document = parserPool.parse(IDAttributeTest.class.getResourceAsStream(documentLocation));
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(XMLObjectProviderRegistrySupport.getDefaultProviderQName());
        
        // With no registration
        xmlObject = unmarshaller.unmarshall(document.getDocumentElement());
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", xmlObject.resolveID("GlobalID1"));
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", xmlObject.resolveID("GlobalID2"));
        
        // Now register the attribute QName in the global config
        XMLObjectProviderRegistrySupport.registerIDAttribute(attribQName);
        document = parserPool.parse(IDAttributeTest.class.getResourceAsStream(documentLocation));
        xmlObject = unmarshaller.unmarshall(document.getDocumentElement());
        AssertJUnit.assertEquals("Lookup of ID mapping failed", xmlObject, xmlObject.resolveID("GlobalID1"));
        AssertJUnit.assertEquals("Lookup of ID mapping failed", ((XSAny) xmlObject).getUnknownXMLObjects().get(0),
                xmlObject.resolveID("GlobalID2"));
        
        // After deregistration
        XMLObjectProviderRegistrySupport.deregisterIDAttribute(attribQName);
        document = parserPool.parse(IDAttributeTest.class.getResourceAsStream(documentLocation));
        xmlObject = unmarshaller.unmarshall(document.getDocumentElement());
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", xmlObject.resolveID("GlobalID1"));
        AssertJUnit.assertNull("Lookup of non-existent ID mapping didn't return null", xmlObject.resolveID("GlobalID2"));
    }
        
}