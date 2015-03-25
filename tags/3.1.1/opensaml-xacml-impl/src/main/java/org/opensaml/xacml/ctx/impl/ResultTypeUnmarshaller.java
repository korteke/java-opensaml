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

package org.opensaml.xacml.ctx.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xacml.ctx.DecisionType;
import org.opensaml.xacml.ctx.ResultType;
import org.opensaml.xacml.ctx.StatusType;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.opensaml.xacml.policy.ObligationsType;
import org.w3c.dom.Attr;

/** Unmarshaller for {@link ResultType} objects. */
public class ResultTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** Constructor. */
    public ResultTypeUnmarshaller() {
        super();
    }

    /** {@inheritDoc} */
    protected void processAttribute(XMLObject xmlObject, Attr attribute) throws UnmarshallingException {
        ResultType result = (ResultType) xmlObject;
        if (attribute.getLocalName().equals(ResultType.RESOURCE_ID_ATTTRIB_NAME)) {
            result.setResourceId(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(XMLObject parentObject, XMLObject childObject) throws UnmarshallingException {
        ResultType result = (ResultType) parentObject;

        if (childObject instanceof ObligationsType) {
            result.setObligations((ObligationsType) childObject);
        } else if (childObject instanceof StatusType) {
            result.setStatus((StatusType) childObject);
        } else if (childObject instanceof DecisionType) {
            result.setDecision((DecisionType) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

}