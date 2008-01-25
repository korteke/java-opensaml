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

import javax.xml.namespace.QName;

import org.opensaml.common.SAMLObject;
import org.opensaml.xacml.ctx.XACMLConstants;

/**
 *Interface for xacml:policy Obligation PolicyIdReference 
 *
 */
public interface XACMLPolicyIdReference extends SAMLObject {

	/** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "PolicyIdReferance";
        
    /** Default element name for XACML 2.0*/
    public static final QName DEFAULT_ELEMENT_NAME_XACML20 = new QName(
    		XACMLConstants.XACML20_NS, 
    		DEFAULT_ELEMENT_LOCAL_NAME, 
    		XACMLConstants.XACML_PREFIX);
   
    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "PolicyIdReferanceType";
    
    /** QName of the XSI type.XACML2.0 */
    public static final QName TYPE_NAME_XACML20 = new QName(
    		XACMLConstants.XACML20_NS,
    		TYPE_LOCAL_NAME,
    		XACMLConstants.XACML_PREFIX);
 
	
}
