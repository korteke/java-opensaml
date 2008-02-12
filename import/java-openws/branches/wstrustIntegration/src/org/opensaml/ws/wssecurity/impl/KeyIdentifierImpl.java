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

import java.util.List;

import javax.xml.namespace.QName;


import org.opensaml.ws.wssecurity.KeyIdentifier;
import org.opensaml.xml.XMLObject;

/**
 * KeyIdentifierImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class KeyIdentifierImpl extends AbstractWSSecurityObject implements
        KeyIdentifier {

    /** The &lt;wsse:KeyIdentifier/@wsu:Id&gt; attribute value */
    private String id_= null;

    /** The Base64 content */
    private String value_= null;

    /** The &lt;wsse:KeyIdentifier/@ValueType&gt; attribute value */
    private String valueType_= null;

    /** &lt;wsse:KeyIdentifier/@EncodingType&gt; attribute value */
    private String encodingType_= null;

    /**
     * Constructor. Default encoding type:
     * <code>KeyIdentifier.ENCODINGTYPE_BASE64_BINARY</code>
     * <p>
     * {@inheritDoc}
     */
    public KeyIdentifierImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        encodingType_= KeyIdentifier.ENCODINGTYPE_BASE64_BINARY;
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
    public void setValue(String value) {
        value_= prepareForAssignment(value_, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.schema.AttributedEncodingType#getEncodingType()
     */
    public String getEncodingType() {
        return encodingType_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.schema.AttributedEncodingType#setEncodingType(java.lang.String)
     */
    public void setEncodingType(String encodingType) {
        encodingType_= prepareForAssignment(encodingType_, encodingType);
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
        valueType_= prepareForAssignment(valueType_, valueType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.WSSecurityObject#getQName()
     */
    public QName getQName() {
        return ELEMENT_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        // no child
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.AttributedId#getId()
     */
    public String getId() {
        return id_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.AttributedId#setId(java.lang.String)
     */
    public void setId(String id) {
        id_= prepareForAssignment(id_, id);
    }

}
