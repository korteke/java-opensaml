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

import javax.xml.namespace.QName;

import org.opensaml.common.SAMLObject;
import org.opensaml.xml.util.XMLObjectChildrenList;

/**
 * Interface for xacml:context StatusCode 
 *
 */
public interface XACMLStatusCode extends SAMLObject {

	/** Local name of the StatusCode element. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "StatusCode";
      
    /** Default element name XACML20. */
    public static final QName DEFAULT_ELEMENT_NAME_20 = new QName(
    		XACMLConstants.XACML20CTX_NS, 
    		DEFAULT_ELEMENT_LOCAL_NAME,
    		XACMLConstants.XACMLCONTEXT_PREFIX);
    
    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "StatusCodeType";
    
    /** QName of the XSI type XACML20. */
    public static final QName TYPE_NAME_20 = new QName(
    		XACMLConstants.XACML20CTX_NS, 
    		TYPE_LOCAL_NAME,
    		XACMLConstants.XACMLCONTEXT_PREFIX);
    
    /** Name of the Value attribute. */
    public static final String Value_ATTTRIB_NAME = "Value";
    
    /**
     * Gets the status codes
     * @return The status codes
     */
    public XMLObjectChildrenList<XACMLStatusCode> getStatusCodes();

    /**
     * Gets the value of the atttribute named value of the status element 
     * @return The value of the atttribute named value for the status element
     */
    public String getValue();

    /**
     * Sets the attribute named value of the status elements
     * @param value The wanted value for the attribute value
     */
	public void setValue(String value);
}
