/*
Copyright 2008 Members of the EGEE Collaboration.
Copyright 2008 University Corporation for Advanced Internet Development,
Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.opensaml.xacml.policy;

import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.common.SAMLObject;
import org.opensaml.xacml.ctx.XACMLConstants;
/**
 *Interface for xacml:policy Obligation 
 *
 */
public interface XACMLObligation extends SAMLObject {
	
	/** Local name of the element. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Obligation";

    /** Default element name for for XACML20. */
    public static final QName DEFAULT_ELEMENT_NAME_20 = new QName(
    		XACMLConstants.XACML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
    		XACMLConstants.XACML_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "ObligationType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME_20 = new QName(
    		XACMLConstants.XACML20_NS, TYPE_LOCAL_NAME,
    		XACMLConstants.XACML_PREFIX);

    /** Name of the FulfilOn attribute. */
    public static final String FulfillOn_ATTTRIB_NAME = "FulfillOn";

    /** Name of the ObligationId attribute. */
    public static final String ObligationId_ATTTRIB_NAME = "ObligationId";
    
    /**Constant for the Permit decision*/
    public static final String PERMIT = "Permit";
    
    /**Constant for the Deny decision*/
    public static final String DENY = "Deny";
    
    /**
     * Gets the value  of FulfillOn for the obligation
     * @return The obligationId of the obligation
     */
    public String getFulfillOn();
    
    /**
     * Sets the attribute FulfillOn 
     * @param newFulfillOn XACMLEffect for a setting the FulfillOn value
     */
    public void setFulFillOn(String newFulfillOn);
    
    
    /**
     * Gets the obligationId of the obligation
     * @return The obligationId of the obligation
     */
    public String getObligationId();
    
    /**
     * Sets the obligationId
     *@param newObligationId is the obligationId
     *
     */
    public void setobligationId(String newObligationId);

    /**
     * Returns the list of attributesAssignments in the obligation
     * @return  the list of attributesAssignments in the obligation
     */
    public List<XACMLAttributeAssignment> getAttributesAssignments();

}
