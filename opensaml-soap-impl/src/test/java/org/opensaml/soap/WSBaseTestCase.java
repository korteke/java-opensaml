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

package org.opensaml.soap;


import org.testng.Assert;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.custommonkey.xmlunit.Diff;
import net.shibboleth.utilities.java.support.xml.XMLAssertTestNG;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import org.w3c.dom.Element;

/**
 * WSBaseTestCase is the base test case for the WS-* packages.
 * 
 */
public abstract class WSBaseTestCase extends XMLObjectBaseTestCase {

    @SuppressWarnings("unchecked")
    protected <T extends XMLObject> T marshallAndUnmarshall(T object)
            throws Exception {
        QName name= object.getElementQName();

        Marshaller marshaller= getMarshaller(name);
        Unmarshaller unmarshaller= getUnmarshaller(name);

        // Go ahead and release the cached DOM, just for good measure
        object.releaseDOM();
        object.releaseChildrenDOM(true);
        Element element= marshaller.marshall(object);
        Assert.assertNotNull(element);

        System.out.println(SerializeSupport.nodeToString(element));

        T object2= (T) unmarshaller.unmarshall(element);
        Assert.assertNotNull(object2);

        // Have to release the DOM before re-marshalling, otherwise the already cached
        // Element just gets adopted into a new Document, and the test below
        // is comparing the same Element/Document and is therefore always true
        // and therefore an invalid test.
        object2.releaseDOM();
        object2.releaseChildrenDOM(true);
        Element element2= marshaller.marshall(object2);
        Assert.assertNotNull(element2);

        System.out.println(SerializeSupport.nodeToString(element2));

        // These need to be false, otherwise the test below is invalid
        //System.out.println("Element equals: " + element.isSameNode(element2)); 
        //System.out.println("Document equals: " + element.getOwnerDocument().isSameNode(element2.getOwnerDocument())); 
        
        // compare XML content
        XMLAssertTestNG.assertXMLIdentical(new Diff(element.getOwnerDocument(), element2.getOwnerDocument()), true);

        return object2;

    }

}
