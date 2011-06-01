/*
 * Licensed to the University Corporation for Advanced Internet Development, Inc.
 * under one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache 
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

package org.opensaml.xml.util;

import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.mock.SimpleXMLObjectBuilder;

/**
 * Tests of XMLObjectHelper utility methods.
 */
public class XMLObjectHelperTest extends XMLObjectBaseTestCase {

    /** Tests cloning an XMLObject. */
    public void testXMLObjectClone() {
        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) Configuration.getBuilderFactory()
            .getBuilder(SimpleXMLObject.ELEMENT_NAME);
        
        SimpleXMLObject origChildObj = sxoBuilder.buildObject();
        origChildObj.setValue("FooBarBaz");
        
        SimpleXMLObject origParentObj = sxoBuilder.buildObject();
        origParentObj.getSimpleXMLObjects().add(origChildObj);
        
        SimpleXMLObject clonedParentObj = null;
        try {
            clonedParentObj = XMLObjectHelper.cloneXMLObject(origParentObj);
        } catch (MarshallingException e) {
            fail("Object cloning failed on marshalling: " + e.getMessage());
        } catch (UnmarshallingException e) {
            fail("Object cloning failed on unmarshalling: " + e.getMessage());
        }
        
        assertFalse("Parent XMLObjects were the same reference", origParentObj == clonedParentObj);
        assertFalse("Parent DOM node was not cloned properly",
                origParentObj.getDOM().isSameNode(clonedParentObj.getDOM()));
        
        assertFalse("Cloned parent had no children", clonedParentObj.getSimpleXMLObjects().isEmpty());
        SimpleXMLObject clonedChildObj = (SimpleXMLObject) clonedParentObj.getSimpleXMLObjects().get(0);
        
        assertFalse("Child XMLObjects were the same reference", origChildObj == clonedChildObj);
        assertFalse("Child DOM node was not cloned properly",
                origChildObj.getDOM().isSameNode(clonedChildObj.getDOM()));
        
        assertEquals("Text content of child was not the expected value", "FooBarBaz", clonedChildObj.getValue());
    }
    
    /** Tests cloning an XMLObject. */
    public void testXMLObjectCloneWithRootInNewDocument() {
        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) Configuration.getBuilderFactory()
            .getBuilder(SimpleXMLObject.ELEMENT_NAME);
        
        SimpleXMLObject origChildObj = sxoBuilder.buildObject();
        origChildObj.setValue("FooBarBaz");
        
        SimpleXMLObject origParentObj = sxoBuilder.buildObject();
        origParentObj.getSimpleXMLObjects().add(origChildObj);
        
        SimpleXMLObject clonedParentObj = null;
        try {
            clonedParentObj = XMLObjectHelper.cloneXMLObject(origParentObj, true);
        } catch (MarshallingException e) {
            fail("Object cloning failed on marshalling: " + e.getMessage());
        } catch (UnmarshallingException e) {
            fail("Object cloning failed on unmarshalling: " + e.getMessage());
        }
        
        assertFalse("Parent XMLObjects were the same reference", origParentObj == clonedParentObj);
        assertFalse("Parent DOM node was not cloned properly",
                origParentObj.getDOM().isSameNode(clonedParentObj.getDOM()));
        
        assertFalse("Cloned parent had no children", clonedParentObj.getSimpleXMLObjects().isEmpty());
        SimpleXMLObject clonedChildObj = (SimpleXMLObject) clonedParentObj.getSimpleXMLObjects().get(0);
        
        assertFalse("Child XMLObjects were the same reference", origChildObj == clonedChildObj);
        assertFalse("Child DOM node was not cloned properly",
                origChildObj.getDOM().isSameNode(clonedChildObj.getDOM()));
        
        assertEquals("Text content of child was not the expected value", "FooBarBaz", clonedChildObj.getValue());
        
        // Test rootInNewDocument requirements
        assertFalse("Cloned objects DOM's were owned by the same Document", 
                origParentObj.getDOM().getOwnerDocument().isSameNode(clonedParentObj.getDOM().getOwnerDocument()));
        assertTrue("Cloned object was not the new Document root", 
                clonedParentObj.getDOM().getOwnerDocument().getDocumentElement().isSameNode(clonedParentObj.getDOM()));
    }
    

}
