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
import org.opensaml.xacml.policy.ActionType;
import org.opensaml.xacml.policy.ActionsType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.ActionsType}.
 */
public class ActionsTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     */
    public ActionsTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/Actions.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/ActionsChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        ActionsType actions = (ActionsType) unmarshallElement(singleElementFile);

        Assert.assertEquals(actions.getActions().size(), 0);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        ActionsType actions = (new ActionsTypeImplBuilder()).buildObject();
        
        assertXMLEquals(expectedDOM, actions);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        ActionsType actions = (ActionsType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(actions.getActions().size(), 3);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        ActionsType actions = (new ActionsTypeImplBuilder()).buildObject();
        
        for (int i = 0; i < 3; i++) {
            actions.getActions().add((ActionType) buildXMLObject(ActionType.DEFAULT_ELEMENT_NAME));
        }
        assertXMLEquals(expectedChildElementsDOM, actions);
}
}