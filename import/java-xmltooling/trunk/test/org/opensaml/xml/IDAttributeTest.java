/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

import org.opensaml.xml.encryption.EncryptedData;
import org.opensaml.xml.encryption.EncryptedKey;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.signature.KeyInfo;

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
    public void testSimpleUnmarshall() {
        SimpleXMLObject sxObject =  (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/IDAttribute.xml");

        assertEquals("ID lookup failed", sxObject, sxObject.resolveID("IDLevel1"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NonExistent"));
        
        sxObject.setId(null);
        assertNull("Lookup of removed ID (formerly extant) didn't return null", sxObject.resolveID("IDLevel1"));
    }
    
    /**
     * Test of ID attributes on complex nested unmarshalled elements 
     * where children are singletons.
     */
    public void testComplexUnmarshall() {
        SimpleXMLObject sxObject = 
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/IDAttributeWithChildren.xml");
        
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NonExistent"));
        
        assertEquals("ID lookup failed", sxObject, 
                sxObject.resolveID("SimpleElementID"));
        assertEquals("ID lookup failed", sxObject.getEncryptedData(), 
                sxObject.resolveID("EncryptedDataID"));
        assertEquals("ID lookup failed", sxObject.getEncryptedData().getKeyInfo(), 
                sxObject.resolveID("KeyInfoID"));
        assertEquals("ID lookup failed", sxObject.getEncryptedData().getKeyInfo().getEncryptedKeys().get(0), 
                sxObject.resolveID("EncryptedKeyID"));
    }
    
    /**
     *  Test propagation of various changes to ID attribute lookup
     *  where children are singletons.
     */
    public void testChangePropagation() {
        SimpleXMLObject sxObject =  
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/IDAttributeWithChildren.xml");
        
        EncryptedData encData = sxObject.getEncryptedData();
        KeyInfo keyInfo = sxObject.getEncryptedData().getKeyInfo();
        EncryptedKey encKey = sxObject.getEncryptedData().getKeyInfo().getEncryptedKeys().get(0);
        
        encKey.setID("Foo");
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("EncryptedKeyID"));
        assertEquals("ID lookup failed", encKey, sxObject.resolveID("Foo"));
        encKey.setID("EncryptedKeyID");
        assertEquals("ID lookup failed", encKey, sxObject.resolveID("EncryptedKeyID"));
        
        encKey.setID(null);
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("EncryptedKeyID"));
        encKey.setID("EncryptedKeyID");
        assertEquals("ID lookup failed", encKey, sxObject.resolveID("EncryptedKeyID"));
        
        encData.setKeyInfo(null);
        assertEquals("ID lookup failed", sxObject, sxObject.resolveID("SimpleElementID"));
        assertEquals("ID lookup failed", encData, sxObject.resolveID("EncryptedDataID"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("KeyInfoID"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("EncryptedKeyID"));
        
        encData.setKeyInfo(keyInfo);
        assertEquals("ID lookup failed", sxObject, sxObject.resolveID("SimpleElementID"));
        assertEquals("ID lookup failed", encData, sxObject.resolveID("EncryptedDataID"));
        assertEquals("ID lookup failed", keyInfo, sxObject.resolveID("KeyInfoID"));
        assertEquals("ID lookup failed", encKey, sxObject.resolveID("EncryptedKeyID"));
        
        KeyInfo newKeyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        newKeyInfo.setID("NewKeyInfoID");
        sxObject.getEncryptedData().setKeyInfo(newKeyInfo);
        assertEquals("ID lookup failed", sxObject, sxObject.resolveID("SimpleElementID"));
        assertEquals("ID lookup failed", encData, sxObject.resolveID("EncryptedDataID"));
        assertEquals("ID lookup failed", newKeyInfo, sxObject.resolveID("NewKeyInfoID"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("EncryptedKeyID"));
    }
    
    /**
     * Test of ID attributes on complex nested unmarshalled elements 
     * where children are stored in an XMLObjectChildren list.
     */
    public void testComplexUnmarshallInList() {
        SimpleXMLObject sxObject = 
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/IDAttributeWithChildrenList.xml");
        
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NonExistent"));
        
        // Resolving from top-level root
        assertEquals("ID lookup failed", sxObject, 
                sxObject.resolveID("IDLevel1"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0), 
                sxObject.resolveID("IDLevel2A"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(1), 
                sxObject.resolveID("IDLevel2B"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(3), 
                sxObject.resolveID("IDLevel2C"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0), 
                sxObject.resolveID("IDLevel3A"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(2), 
                sxObject.resolveID("IDLevel3B"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3), 
                sxObject.resolveID("IDLevel3C"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0)
                .getSimpleXMLObjects().get(0), 
                sxObject.resolveID("IDLevel4A"));
        
        // Resolving from secondary level root
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0), 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel2A"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0), 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel3A"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(2), 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel3B"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3), 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel3C"));
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0)
                .getSimpleXMLObjects().get(0), 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel4A"));
        assertNull("Lookup of non-existent ID didn't return null", 
                sxObject.getSimpleXMLObjects().get(0).resolveID("IDLevel1"));
    }
        
        
    /**
     *  Test propagation of various changes to ID attribute mappings due to attribute value changes
     *  where children are stored in an XMLObjectChildren list. 
     */
    public void testChangePropagationInList() {
        SimpleXMLObject sxObject =  
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/IDAttributeWithChildrenList.xml");
        
        // Test propagation of attribute value change up the tree 
        sxObject.getSimpleXMLObjects().get(1).setId("NewIDLevel2B");
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(1), 
                sxObject.resolveID("NewIDLevel2B"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel2B"));
        
        sxObject.getSimpleXMLObjects().get(1).setId(null);
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NewIDLevel2B"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel2B"));
        
        sxObject.getSimpleXMLObjects().get(1).setId("IDLevel2B");
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(1), 
                sxObject.resolveID("IDLevel2B"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NewIDLevel2B"));
        
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3).setId("NewIDLevel3C");
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(3), 
                sxObject.resolveID("NewIDLevel3C"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3C"));
        
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0).setId(null);
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3A"));
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0).setId("IDLevel3A");
        assertEquals("ID lookup failed", sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(0), 
                sxObject.resolveID("IDLevel3A"));
    }
        
    /**
     *  Test propagation of various changes to ID attribute mappings due to list operations
     *  where children are stored in an XMLObjectChildren list. 
     */
    public void testListOpChangePropagation() {
        
        SimpleXMLObject sxObject =  
            (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/IDAttributeWithChildrenList.xml");
        
        SimpleXMLObject targetIDLevel3B = sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().get(2);
        assertEquals("ID lookup failed", targetIDLevel3B, sxObject.resolveID("IDLevel3B"));
        
        // remove(int)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().remove(2);
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3B"));
        // add(XMLObject)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().add(targetIDLevel3B);
        assertEquals("ID lookup failed", targetIDLevel3B, sxObject.resolveID("IDLevel3B"));
        // remove(XMLObject)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().remove(targetIDLevel3B);
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3B"));
        // set(int, XMLObject)
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().set(1, targetIDLevel3B);
        assertEquals("ID lookup failed", targetIDLevel3B, sxObject.resolveID("IDLevel3B"));
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().remove(targetIDLevel3B);
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3B"));
        
        // Ops using new object
        SimpleXMLObject newSimpleObject = (SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME);
        newSimpleObject.setId("NewSimpleElement");
        
        sxObject.getSimpleXMLObjects().get(3).getSimpleXMLObjects().add(newSimpleObject);
        assertEquals("ID lookup failed", newSimpleObject, sxObject.resolveID("NewSimpleElement"));
        sxObject.getSimpleXMLObjects().get(3).getSimpleXMLObjects().remove(newSimpleObject);
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("NewSimpleElement"));
        
        // clear
        sxObject.getSimpleXMLObjects().get(0).getSimpleXMLObjects().clear();
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3A"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3B"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel3C"));
        assertNull("Lookup of non-existent ID didn't return null", sxObject.resolveID("IDLevel4A"));
    }
}