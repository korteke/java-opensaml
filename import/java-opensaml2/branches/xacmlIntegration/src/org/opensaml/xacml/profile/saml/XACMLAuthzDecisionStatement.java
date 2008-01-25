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

import org.opensaml.saml2.core.Statement;
import org.opensaml.xacml.ctx.XACMLRequest;
import org.opensaml.xacml.ctx.XACMLResponse;


/**
 * Represents a xacml-samlp:XACMLAuthzDecisionStatement
 */
public interface XACMLAuthzDecisionStatement extends Statement
{
    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "XACMLAuthzDecisionStatement";

    /** Default element name for XACML 1.0*/
    public static final QName DEFAULT_ELEMENT_NAME_XACML10 = new QName(SAML2XACML2ProfileConstants.SAML20XACML10_NS, 
    		DEFAULT_ELEMENT_LOCAL_NAME, SAML2XACML2ProfileConstants.SAML20XACMLASSERTION_PREFIX);
    
    /** Default element name for XACML 1.1*/
    public static final QName DEFAULT_ELEMENT_NAME_XACML1_1 = new QName(SAML2XACML2ProfileConstants.SAML20XACML1_1_NS, 
    		DEFAULT_ELEMENT_LOCAL_NAME, SAML2XACML2ProfileConstants.SAML20XACMLASSERTION_PREFIX);
    
    /** Default element name for XACML 2.0*/
    public static final QName DEFAULT_ELEMENT_NAME_XACML20 = new QName(SAML2XACML2ProfileConstants.SAML20XACML20_NS, 
    		DEFAULT_ELEMENT_LOCAL_NAME, SAML2XACML2ProfileConstants.SAML20XACMLASSERTION_PREFIX);
    
    /** Default element name for XACML 3.0*/
    public static final QName DEFAULT_ELEMENT_NAME_XACML30 = new QName(SAML2XACML2ProfileConstants.SAML20XACML30_NS, 
    		DEFAULT_ELEMENT_LOCAL_NAME, SAML2XACML2ProfileConstants.SAML20XACMLASSERTION_PREFIX);
    
    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "XACMLAuthzDecisionStatementType";

    /** QName of the XSI type.XACML1.0 */
    public static final QName TYPE_NAME_XACML10 = new QName(SAML2XACML2ProfileConstants.SAML20XACML10_NS, TYPE_LOCAL_NAME,
    		SAML2XACML2ProfileConstants.SAML20XACMLASSERTION_PREFIX);
    
    /** QName of the XSI type.XACML1.1 */
    public static final QName TYPE_NAME_XACML1_1 = new QName(SAML2XACML2ProfileConstants.SAML20XACML1_1_NS, TYPE_LOCAL_NAME,
    		SAML2XACML2ProfileConstants.SAML20XACMLASSERTION_PREFIX);
    
    /** QName of the XSI type.XACML2.0 */
    public static final QName TYPE_NAME_XACML20 = new QName(SAML2XACML2ProfileConstants.SAML20XACML20_NS, TYPE_LOCAL_NAME,
    		SAML2XACML2ProfileConstants.SAML20XACMLASSERTION_PREFIX);
  
    /** QName of the XSI type.XACML3.0 */
    public static final QName TYPE_NAME_XACML30 = new QName(SAML2XACML2ProfileConstants.SAML20XACML30_NS, TYPE_LOCAL_NAME,
    		SAML2XACML2ProfileConstants.SAML20XACMLASSERTION_PREFIX);
     

    /**
     * Get's the {@link org.opensaml.xacml.ctx.XACMLRequest} from the <code>XACMLAuthzDecisionStatement</code>
     *   
	 * @return the {@link org.opensaml.xacml.ctx.XACMLRequest} inside the <code>XACMLAuthzDecisionStatement</code>
	 */
    
    public XACMLRequest getRequest();
    

    /**
     * Get's the {@link org.opensaml.xacml.ctx.XACMLResponse} from the <code>XACMLAuthzDecisionStatement</code>
     *   
	 * @return the {@link org.opensaml.xacml.ctx.XACMLResponse} inside the <code>XACMLAuthzDecisionStatement</code>
	 */
    
    public XACMLResponse getResponse();
   
    /**
     * Sets a {@link org.opensaml.xacml.ctx.XACMLResponse} to the <code>XACMLAuthzDecisionStatement</code>
     * 
     * @param request {@link org.opensaml.xacml.ctx.XACMLRequest}  
     */
    public void setRequest(XACMLRequest request);
    
    /**
	 *  Sets a {@link org.opensaml.xacml.ctx.XACMLResponse} to the <code>XACMLAuthzDecisionStatement</code>
	 *  
	 *   @param response {@link org.opensaml.xacml.ctx.XACMLResponse}  
	 */
    public void setResponse(XACMLResponse response);
}
