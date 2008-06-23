/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.wssecurity.impl;

import org.opensaml.ws.wssecurity.BinarySecurityToken;

/**
 * BinarySecurityTokenImpl
 * 
 */
public class BinarySecurityTokenImpl extends AbstractAttributedId implements BinarySecurityToken {

    /** wsse:BinarySecurityToken Base64 content */
    private String value_ = null;

    /** wsse:BinarySecurityToken/@ValueType attribute */
    private String valueType_ = null;

    /** wsse:BinarySecurityToken/@EncodingType attribute */
    private String encodingType_ = null;

    /**
     * Constructor. Default EncodingType is <code>BinarySecurityToken.ENCODINGTYPE_BASE64_BINARY</code>
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public BinarySecurityTokenImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        // default encoding type
        encodingType_ = BinarySecurityToken.ENCODINGTYPE_BASE64_BINARY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.schema.XSBase64Binary#getValue()
     */
    public String getValue() {
        return value_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.schema.XSBase64Binary#setValue(java.lang.String)
     */
    public void setValue(String newValue) {
        value_ = prepareForAssignment(value_, newValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.AttributedEncodingType#getEncodingType()
     */
    public String getEncodingType() {
        return encodingType_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.AttributedEncodingType#setEncodingType(java.lang.String)
     */
    public void setEncodingType(String encodingType) {
        encodingType_ = prepareForAssignment(encodingType_, encodingType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.AttributedValueType#getValueType()
     */
    public String getValueType() {
        return valueType_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.AttributedValueType#setValueType(java.lang.String)
     */
    public void setValueType(String valueType) {
        valueType_ = prepareForAssignment(valueType_, valueType);
    }

}
