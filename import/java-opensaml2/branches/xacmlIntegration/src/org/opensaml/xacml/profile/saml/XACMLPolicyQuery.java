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
package org.opensaml.xacml.profile.saml;

import javax.xml.namespace.QName;

import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.xacml.ctx.XACMLRequest;
import org.opensaml.xacml.policy.XACMLPolicyIdReference;
import org.opensaml.xacml.policy.XACMLPolicySetIdReference;

/**
 * XACMLPolicyQuery 
 *
 */

public interface XACMLPolicyQuery extends RequestAbstractType{
	
	 /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "XACMLPolicyQuery";

    /** Default element name for XACML 1.0*/
    public static final QName DEFAULT_ELEMENT_NAME_XACML10 = new QName(SAML2XACML2ProfileConstants.SAML20XACML10P_NS, 
    		DEFAULT_ELEMENT_LOCAL_NAME, SAML2XACML2ProfileConstants.SAML20XACMLPROTOCOl_PREFIX);
    
    /** Default element name for XACML 1.1*/
    public static final QName DEFAULT_ELEMENT_NAME_XACML1_1 = new QName(SAML2XACML2ProfileConstants.SAML20XACML1_1P_NS, 
    		DEFAULT_ELEMENT_LOCAL_NAME, SAML2XACML2ProfileConstants.SAML20XACMLPROTOCOl_PREFIX);
    
    /** Default element name for XACML 2.0*/
    public static final QName DEFAULT_ELEMENT_NAME_XACML20 = new QName(SAML2XACML2ProfileConstants.SAML20XACML20P_NS, 
    		DEFAULT_ELEMENT_LOCAL_NAME, SAML2XACML2ProfileConstants.SAML20XACMLPROTOCOl_PREFIX);

    /** Default element nam for XACML 3.0*/
    public static final QName DEFAULT_ELEMENT_NAME_XACML30 = new QName(SAML2XACML2ProfileConstants.SAML20XACML30P_NS,
    		DEFAULT_ELEMENT_LOCAL_NAME, SAML2XACML2ProfileConstants.SAML20XACMLPROTOCOl_PREFIX);
    
    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "XACMLPolicyQueryType";

    /** QName of the XSI type.XACML1.0 */
    public static final QName TYPE_NAME_XACML10 = new QName(SAML2XACML2ProfileConstants.SAML20XACML10P_NS, TYPE_LOCAL_NAME,
    		SAML2XACML2ProfileConstants.SAML20XACMLPROTOCOl_PREFIX);
    
    /** QName of the XSI type.XACML1.1 */
    public static final QName TYPE_NAME_XACML1_1 = new QName(SAML2XACML2ProfileConstants.SAML20XACML1_1P_NS, TYPE_LOCAL_NAME,
    		SAML2XACML2ProfileConstants.SAML20XACMLPROTOCOl_PREFIX);
    
    
    /** QName of the XSI type.XACML2.0 */
    public static final QName TYPE_NAME_XACML20 = new QName(SAML2XACML2ProfileConstants.SAML20XACML20P_NS, TYPE_LOCAL_NAME,
    		SAML2XACML2ProfileConstants.SAML20XACMLPROTOCOl_PREFIX);
  
    /** QName of the XSI type.XACML3.0 */
    public static final QName TYPE_NAME_XACML30 = new QName(SAML2XACML2ProfileConstants.SAML20XACML30P_NS, TYPE_LOCAL_NAME,
    		SAML2XACML2ProfileConstants.SAML20XACMLPROTOCOl_PREFIX);
    
    /**
     * Gets the Request inside the policy query
	 * @return the XACML Request
	 */
    public XACMLRequest getRequest();

   
    /**
     * Gets the policy id of the query
     * @return The XACMLPolicyIdReference inside the policy query
     */
    public XACMLPolicyIdReference getXACMLPolicyIdReference();
    
    /**
     * Gets the policyset id of the query
     * @return The XACMLPolicySetIdReference of the policy query 
     */
    public XACMLPolicySetIdReference getXACMLPolicySetIdReference();
     
    /**
     * Set's the XACML Request
     * @param request  Set's the XACML Request
     */
    public void setRequest(XACMLRequest request);
    
    /**
     * Sets the policyIdReferance of this policy query
     * @param policyIdRef Sets the policyIdReferance of this policy query
     */  
    public void setXACMLPolicyIdReference(XACMLPolicyIdReference policyIdRef);
    
    /**
     * Sets the policyIdReferance of this policy query
     * @param policySetIdRef Sets the policyIdReferance of this policy query
     */   
    public void setXACMLPolicySetIdReference(XACMLPolicySetIdReference policySetIdRef);
    
}
