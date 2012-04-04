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

package org.opensaml.saml.saml1.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.StatusCode;

/**
 * Test class for org.opensaml.saml.saml1.core.StatusCode.
 */
public class StatusCodeTest extends XMLObjectProviderBaseTestCase {

    /** name used to generate objects. */
    private final QName qname;

    /**Constructor. */
    public StatusCodeTest() {
        childElementsFile = "/data/org/opensaml/saml/saml1/impl/FullStatusCode.xml";
        singleElementFile = "/data/org/opensaml/saml/saml1/impl/singleStatusCode.xml";
        
        qname = new QName(SAMLConstants.SAML10P_NS, StatusCode.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {

        StatusCode code = (StatusCode) unmarshallElement(singleElementFile);

        Assert.assertEquals(code.getValue(), StatusCode.SUCCESS, "Single Element Value wrong");
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsUnmarshall() {

        StatusCode code = (StatusCode) unmarshallElement(childElementsFile);

        Assert.assertNotNull(code.getStatusCode(), "Child StatusCode");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        StatusCode code = (StatusCode) buildXMLObject(qname);

        code.setValue(StatusCode.SUCCESS);

        assertXMLEquals(expectedDOM, code);
    }

    /** {@inheritDoc} */

    @Test
    public void testChildElementsMarshall() {

        StatusCode code = (StatusCode) buildXMLObject(qname);

        code.setValue(StatusCode.REQUESTER);

        code.setStatusCode((StatusCode) buildXMLObject(qname));

        code.getStatusCode().setValue(StatusCode.VERSION_MISMATCH);

        assertXMLEquals(expectedChildElementsDOM, code);
    }
}
