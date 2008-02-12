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
package org.opensaml.ws.wstrust.impl;


import org.opensaml.ws.wstrust.BinaryExchange;
import org.opensaml.xml.util.AttributeMap;

/**
 * BinaryExchangeImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class BinaryExchangeImpl extends AbstractWSTrustObject implements
        BinaryExchange {

    /** The wst:BinaryExchange Base64 encoded binary content */
    String base64Binary_= null;

    /** The wst:BinaryExchange/@ValueType attribute value */
    String valueType_= null;

    /** The wst:BinaryExchange/@EncodingType attribute value */
    String encodingType_= null;

    /** xs:anyAttribute for this element. */
    private AttributeMap anyAttributes_;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public BinaryExchangeImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        anyAttributes_= new AttributeMap(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.schema.XSBase64Binary#getValue()
     */
    public String getValue() {
        return base64Binary_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.schema.XSBase64Binary#setValue(java.lang.String)
     */
    public void setValue(String newValue) {
        base64Binary_= prepareForAssignment(base64Binary_, newValue);
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
        encodingType_= prepareForAssignment(encodingType_, encodingType);
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
        valueType_= prepareForAssignment(valueType_, valueType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.AttributeExtensibleXMLObject#getUnknownAttributes()
     */
    public AttributeMap getUnknownAttributes() {
        return anyAttributes_;
    }

}
