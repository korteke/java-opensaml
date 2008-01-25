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

/**
 * Is a xacml-context Request
 * 
 */
public interface XACMLRequest extends SAMLObject {

    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Request";

    /** Default element name.for XACML 2.0 */
    public static final QName DEFAULT_ELEMENT_NAME_XACML20 = new QName(XACMLConstants.XACML20CTX_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, XACMLConstants.XACMLCONTEXT_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "RequestType";

    /** QName of the XSI type.XACML2.0 */
    public static final QName TYPE_NAME_XACML20 = new QName(XACMLConstants.XACML20CTX_NS, TYPE_LOCAL_NAME,
            XACMLConstants.XACMLCONTEXT_PREFIX);

    /**
     * Gets the environment from the request
     * 
     * @return the environment from the request
     */
    public List<XACMLEnvironment> getEnvironment();

    /**
     * Gets the action from the request
     * 
     * @return the action from the request
     */
    public XACMLAction getAction();

    /**
     * Sets the action of the request
     */
    public void setAction(XACMLAction newAction);

    /**
     * Gets the subjects from the request
     * 
     * @return the subjects from the request
     */
    public List<XACMLSubject> getSubjects();

    /**
     * Gets the resources from the request
     * 
     * @return the resources from the request
     */
    public List<XACMLResource> getResources();
}
