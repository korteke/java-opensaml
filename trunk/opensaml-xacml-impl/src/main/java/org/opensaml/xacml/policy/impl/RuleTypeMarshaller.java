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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xacml.impl.AbstractXACMLObjectMarshaller;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.RuleType;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * Marshaller for {@link RuleType}.
 */
public class RuleTypeMarshaller extends AbstractXACMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        RuleType ruleType = (RuleType) xmlObject;

        if (!Strings.isNullOrEmpty(ruleType.getRuleId())) {
            domElement.setAttributeNS(null, RuleType.RULE_ID_ATTRIB_NAME, ruleType.getRuleId());
        }
        
        if (!Strings.isNullOrEmpty(ruleType.getEffect().toString())) {
            if(ruleType.getEffect().equals(EffectType.Deny)){
                domElement.setAttributeNS(null, RuleType.EFFECT_ATTRIB_NAME,EffectType.Deny.toString());
            }else{
                domElement.setAttributeNS(null, RuleType.EFFECT_ATTRIB_NAME,EffectType.Permit.toString());
            }
        }
    }

}