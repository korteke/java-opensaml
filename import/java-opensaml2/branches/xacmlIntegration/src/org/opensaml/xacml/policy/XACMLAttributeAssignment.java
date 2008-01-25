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
import org.opensaml.xacml.ctx.XACMLAttributeValue;
import org.opensaml.xacml.ctx.XACMLConstants;

/**
 * Interface for xacml:policy AttributeAssignment
 * 
 */
public interface XACMLAttributeAssignment extends SAMLObject, XACMLAttributeValue {

    /** Element name */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "AttributeAssignment";

    /** Default element name XACML20. */
    public static final QName DEFAULT_ELEMENT_NAME_20 = new QName(XACMLConstants.XACML20_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, XACMLConstants.XACML_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "AttributeAssignmentType";

    /** QName of the XSI type XACML20. */
    public static final QName TYPE_NAME_20 = new QName(XACMLConstants.XACML20_NS, TYPE_LOCAL_NAME,
            XACMLConstants.XACML_PREFIX);

    /** Name of the Name attribute. */
    public static final String ATTRIBUTEID_ATTTRIB_NAME = "AttributeId";

    /**
     * Gets the attributeId
     * 
     * @return The attributeId
     */
    public String getAttributeId();

    /**
     * Sets the attribute id
     * 
     * @param newAttributeID The new attribute id
     */
    public void setAttributeId(String newAttributeID);

    /**
     * Gets the value
     * 
     * @return The value
     */
    public String getValue();

    /**
     * Sets the new value
     * 
     * @param newValue The new value
     */
    public void setValue(String newValue);
}
