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
package org.opensaml.xacml.ctx;

import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.common.SAMLObject;
import org.opensaml.xacml.XACMLConstants;
import org.opensaml.xacml.policy.XACMLObligations;

/**
 * Interface for xacml:context Result
 */

public interface XACMLResult extends SAMLObject {

	/** Local name of the element. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Result";
   
    /** Default element name XACML20. */
    public static final QName DEFAULT_ELEMENT_NAME_20 = new QName(
    		XACMLConstants.XACML20CTX_NS,
    		DEFAULT_ELEMENT_LOCAL_NAME,
    		XACMLConstants.XACMLCONTEXT_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "ResultType";
    
    /** QName of the XSI type. */
    public static final QName TYPE_NAME_20 = new QName(
    		XACMLConstants.XACML20CTX_NS, 
    		TYPE_LOCAL_NAME,
    		XACMLConstants.XACMLCONTEXT_PREFIX);

    /** Name of the attribute. */
    public static final String ResourceId_ATTTRIB_NAME = "ResourceId";

    
    /**
     * Returns the decision in the result
     * @return XACMLDecision the decision in the result
     */
    public XACMLDecision getDecision();
    
    /**
     * Returns the list of Obligations in the result
     * @return  the list of Obligations in the result
     */
    public List<XACMLObligations> getObligations();
   
    /**
     * Gets the ResourceId of the result
     * @return The ResourceId of the subject
     */
    public String getResourceId();
    
    /**
     * Returns the status in the result
     * @return  the status in the result
     */
    public List<XACMLStatus>  getStatus();
    
    /**
     * Sets the decision in the result
     * @param newDecision The decison in the result
     */
    public void setDecision(XACMLDecision newDecision);
  
    /**
     * Sets the ResourceId
     *@param resourceId is the ResourceId
     *
     */
    public void setResourceId(String resourceId);    
}
