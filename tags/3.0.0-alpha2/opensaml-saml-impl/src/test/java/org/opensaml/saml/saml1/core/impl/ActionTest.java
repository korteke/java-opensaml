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

/**
 * 
 */
package org.opensaml.saml.saml1.core.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Action;

/**
 * Test for {@link org.opensaml.saml.saml1.core.impl.ActionImpl}
 */
public class ActionTest extends XMLObjectProviderBaseTestCase {

    private final String expectedContents;
    private final String expectedNamespace;
    private final QName qname;

    /**
     * Constructor
     */
    public ActionTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml/saml1/impl/singleAction.xml";
        singleElementOptionalAttributesFile  = "/data/org/opensaml/saml/saml1/impl/singleActionAttributes.xml";    
        expectedNamespace = "namespace";
        expectedContents = "Action Contents";
        qname = new QName(SAMLConstants.SAML1_NS, Action.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    }
    

    /** {@inheritDoc} */

    @Test
    public void testSingleElementUnmarshall() {
        Action action = (Action) unmarshallElement(singleElementFile);
        Assert.assertNull(action.getNamespace(), "namespace attribute present");
        Assert.assertNull(action.getContents(), "Contents present");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Action action = (Action) unmarshallElement(singleElementOptionalAttributesFile);
        Assert.assertEquals(action.getNamespace(), expectedNamespace, "namespace attribute ");
        Assert.assertEquals(action.getContents(), expectedContents, "Contents ");
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementMarshall() {
        assertXMLEquals(expectedDOM, buildXMLObject(qname));
    }

    /** {@inheritDoc} */

    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        Action action =(Action) buildXMLObject(qname);
        action.setNamespace(expectedNamespace);
        action.setContents(expectedContents);
        assertXMLEquals(expectedOptionalAttributesDOM, action);
    }
}
