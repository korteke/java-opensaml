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

package org.opensaml.xacml.profile.saml.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.core.impl.RequestAbstractTypeImpl;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.profile.saml.ReferencedPoliciesType;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQueryType;

/** A concrete implementation of {@link XACMLAuthzDecisionQueryType}. */
public class XACMLAuthzDecisionQueryTypeImpl extends RequestAbstractTypeImpl implements XACMLAuthzDecisionQueryType {

    /** Policy children. */
    private List<PolicyType> policies;

    /** PolicySet children. */
    private List<PolicySetType> policySets;

    /** ReeferencedPolicies child. */
    private ReferencedPoliciesType referencedPolicies;

    /** The xacml-context:Request. */
    private RequestType request;

    /** InputContextOnly attribute value. Default = false. */
    private XSBooleanValue inputContextOnly;

    /** ReturnContext attribute value.Default = false. */
    private XSBooleanValue returnContext;

    /** CombinePolicies attribute value. Default = true. */
    private XSBooleanValue combinePolicies;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected XACMLAuthzDecisionQueryTypeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        setElementNamespacePrefix(namespacePrefix);
        policies = new XMLObjectChildrenList<>(this);
        policySets = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    public XSBooleanValue getCombinePoliciesXSBooleanValue() {
        return combinePolicies;
    }

    /** {@inheritDoc} */
    public XSBooleanValue getInputContextOnlyXSBooleanValue() {
        return inputContextOnly;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<>();

        if (super.getOrderedChildren() != null) {
            children.addAll(super.getOrderedChildren());
        }
        if (request != null) {
            children.add(request);
        }

        if (!policies.isEmpty()) {
            children.addAll(policies);
        }

        if (!policySets.isEmpty()) {
            children.addAll(policySets);
        }

        if (referencedPolicies != null) {
            children.add(referencedPolicies);
        }

        return Collections.unmodifiableList(children);
    }

    /** {@inheritDoc} */
    public RequestType getRequest() {
        return request;
    }

    /** {@inheritDoc} */
    public XSBooleanValue getReturnContextXSBooleanValue() {
        return returnContext;
    }

    /** {@inheritDoc} */
    public Boolean isCombinePolicies() {
        if (combinePolicies != null) {
            return combinePolicies.getValue();
        }

        return Boolean.TRUE;
    }

    /** {@inheritDoc} */
    public Boolean isInputContextOnly() {
        if (inputContextOnly != null) {
            return inputContextOnly.getValue();
        }

        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public Boolean isReturnContext() {
        if (returnContext != null) {
            return returnContext.getValue();
        }

        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    public void setCombinePolicies(XSBooleanValue combine) {
        combinePolicies = prepareForAssignment(combinePolicies, combine);
    }

    /** {@inheritDoc} */
    public void setCombinePolicies(Boolean combine) {
        if (combine != null) {
            combinePolicies = prepareForAssignment(combinePolicies, new XSBooleanValue(combine, false));
        } else {
            combinePolicies = prepareForAssignment(combinePolicies, null);
        }

    }

    /** {@inheritDoc} */
    public void setInputContextOnly(XSBooleanValue flag) {
        inputContextOnly = prepareForAssignment(inputContextOnly, flag);
    }

    /** {@inheritDoc} */
    public void setInputContextOnly(Boolean flag) {
        if (flag != null) {
            inputContextOnly = prepareForAssignment(inputContextOnly, new XSBooleanValue(flag, false));
        } else {
            inputContextOnly = prepareForAssignment(inputContextOnly, null);
        }
    }

    /** {@inheritDoc} */
    public void setRequest(RequestType req) {
        request = prepareForAssignment(this.request, req);
    }

    /** {@inheritDoc} */
    public void setReturnContext(XSBooleanValue flag) {
        returnContext = prepareForAssignment(returnContext, flag);
    }

    /** {@inheritDoc} */
    public void setReturnContext(Boolean flag) {
        if (flag != null) {
            returnContext = prepareForAssignment(returnContext, new XSBooleanValue(flag, false));
        } else {
            returnContext = prepareForAssignment(returnContext, null);
        }
    }

    /** {@inheritDoc} */
    public List<PolicyType> getPolicies() {
        return policies;
    }

    /** {@inheritDoc} */
    public List<PolicySetType> getPolicySets() {
        return policySets;
    }

    /** {@inheritDoc} */
    public ReferencedPoliciesType getReferencedPolicies() {
        return referencedPolicies;
    }

    /** {@inheritDoc} */
    public void setReferencedPolicies(ReferencedPoliciesType pols) {
        referencedPolicies = prepareForAssignment(referencedPolicies, pols);
    }
}