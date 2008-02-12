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


import org.opensaml.ws.wstrust.BinarySecret;
import org.opensaml.xml.util.AttributeMap;

/**
 * BinarySecretImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class BinarySecretImpl extends AbstractWSTrustObject implements
        BinarySecret {

    /** The &lt;wst:Type&gt; attribute value */
    private String type_= null;

    /** xs:anyAttribute for this element. */
    private AttributeMap anyAttributes_;

    /** The base64 content */
    private String value_= null;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public BinarySecretImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        anyAttributes_= new AttributeMap(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.BinarySecret#getType()
     */
    public String getType() {
        return type_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.BinarySecret#setType(java.lang.String)
     */
    public void setType(String type) {
        type_= prepareForAssignment(type_, type);
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
     * @see org.opensaml.xml.AttributeExtensibleXMLObject#getUnknownAttributes()
     */
    public AttributeMap getUnknownAttributes() {
        return anyAttributes_;
    }

}
