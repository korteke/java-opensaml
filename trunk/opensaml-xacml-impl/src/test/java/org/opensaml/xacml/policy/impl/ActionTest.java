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

package org.opensaml.xacml.policy.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xacml.policy.ActionMatchType;
import org.opensaml.xacml.policy.ActionType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.ActionType}.
 */
public class ActionTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     */
    public ActionTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/Action.xml";
        childElementsFile  = "/org/opensaml/xacml/policy/impl/ActionChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        ActionType action = (ActionType) unmarshallElement(singleElementFile);

        Assert.assertEquals(action.getActionMatches().size(), 0);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        ActionType action = (new ActionTypeImplBuilder()).buildObject();
        
        assertXMLEquals(expectedDOM, action);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        ActionType action = (ActionType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(action.getActionMatches().size(), 4);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        ActionType action = (new ActionTypeImplBuilder()).buildObject();
        
        for (int i = 0; i < 4; i++) {
            ActionMatchType actionMatch = (ActionMatchType) buildXMLObject(ActionMatchType.DEFAULT_ELEMENT_NAME);
            actionMatch.setMatchId("http://example.org");
            action.getActionMatches().add(actionMatch);
        }
        assertXMLEquals(expectedChildElementsDOM, action);
}
}