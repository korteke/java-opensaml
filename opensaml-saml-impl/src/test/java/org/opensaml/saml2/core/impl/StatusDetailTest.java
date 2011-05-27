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
package org.opensaml.saml2.core.impl;

import javax.xml.namespace.QName;

import org.opensaml.common.BaseSAMLObjectProviderTestCase;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.StatusDetail;
import org.opensaml.xml.schema.impl.XSAnyBuilder;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml2.core.impl.StatusDetailImpl}.
 */
public class StatusDetailTest extends BaseSAMLObjectProviderTestCase {

    /**
     * Constructor.
     *
     */
    public StatusDetailTest() {
       singleElementFile = "/data/org/opensaml/saml2/core/impl/StatusDetail.xml";
       childElementsFile = "/data/org/opensaml/saml2/core/impl/StatusDetailChildElements.xml";
    }
    
    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        StatusDetail statusDetail = (StatusDetail) buildXMLObject(StatusDetail.DEFAULT_ELEMENT_NAME);
        
        assertEquals(expectedDOM, statusDetail);
    }

    /** {@inheritDoc} */
    public void testChildElementsMarshall() {
        StatusDetail statusDetail = (StatusDetail) buildXMLObject(StatusDetail.DEFAULT_ELEMENT_NAME);
        QName childQname = new QName("http://www.example.org/testObjects", "SimpleElement", "test");
        
        statusDetail.getUnknownXMLObjects().add(buildXMLObject(childQname));
        statusDetail.getUnknownXMLObjects().add(buildXMLObject(childQname));
        statusDetail.getUnknownXMLObjects().add(buildXMLObject(childQname));
        
        assertEquals(expectedChildElementsDOM, statusDetail);
    }



    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        StatusDetail statusDetail = (StatusDetail) unmarshallElement(singleElementFile);
        
        assertNotNull(statusDetail);
    }

    /** {@inheritDoc} */
    public void testChildElementsUnmarshall() {
        StatusDetail statusDetail = (StatusDetail) unmarshallElement(childElementsFile);
        
        assertNotNull(statusDetail);
        assertEquals(3, statusDetail.getUnknownXMLObjects().size());
    }
    
}