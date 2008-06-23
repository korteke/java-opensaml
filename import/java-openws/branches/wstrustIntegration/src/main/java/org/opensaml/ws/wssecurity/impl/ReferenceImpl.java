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

import org.opensaml.ws.wssecurity.Reference;

/**
 * ReferenceImpl
 * 
 */
public class ReferenceImpl extends AbstractWSSecurityObject implements Reference {
    /** wsse:Reference/@URI attribute */
    private String uri_ = null;

    /** wsse:Reference/@ValueType attribute */
    private String valueType_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public ReferenceImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.Reference#getURI()
     */
    public String getURI() {
        return uri_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.Reference#setURI(java.lang.String)
     */
    public void setURI(String uri) {
        uri_ = prepareForAssignment(uri_, uri);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.schema.AttributedValueType#getValueType()
     */
    public String getValueType() {
        return valueType_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.schema.AttributedValueType#setValueType(java.lang.String)
     */
    public void setValueType(String valueType) {
        valueType_ = prepareForAssignment(valueType_, valueType);
    }

}
