/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xmlsec.signature.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.DSAKeyValue;
import org.opensaml.xmlsec.signature.ECKeyValue;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;

/**
 * Concrete implementation of {@link org.opensaml.xmlsec.signature.KeyValue}.
 */
public class KeyValueImpl extends AbstractXMLObject implements KeyValue {
    
    /** DSAKeyValue child element. */
    private DSAKeyValue dsaKeyValue;
    
    /** RSAKeyValue child element. */
    private RSAKeyValue rsaKeyValue;

    /** ECKeyValue child element. */
    private ECKeyValue ecKeyValue;
    
    /** Wildcard &lt;any&gt; XMLObject child element. */
    private XMLObject unknownXMLObject;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected KeyValueImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public DSAKeyValue getDSAKeyValue() {
        return dsaKeyValue;
    }

    /** {@inheritDoc} */
    public void setDSAKeyValue(DSAKeyValue newDSAKeyValue) {
        dsaKeyValue = prepareForAssignment(dsaKeyValue, newDSAKeyValue);
    }

    /** {@inheritDoc} */
    public RSAKeyValue getRSAKeyValue() {
        return rsaKeyValue;
    }

    /** {@inheritDoc} */
    public void setRSAKeyValue(RSAKeyValue newRSAKeyValue) {
        rsaKeyValue = prepareForAssignment(rsaKeyValue, newRSAKeyValue);
    }

    /** {@inheritDoc} */
    public ECKeyValue getECKeyValue() {
        return ecKeyValue;
    }

    /** {@inheritDoc} */
    public void setECKeyValue(ECKeyValue newECKeyValue) {
        ecKeyValue = prepareForAssignment(ecKeyValue, newECKeyValue);
    }
    
    /** {@inheritDoc} */
    public XMLObject getUnknownXMLObject() {
        return unknownXMLObject;
    }

    /** {@inheritDoc} */
    public void setUnknownXMLObject(XMLObject newXMLObject) {
        unknownXMLObject = prepareForAssignment(unknownXMLObject, newXMLObject);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
        
        if (dsaKeyValue != null) {
            children.add(dsaKeyValue);
        }
        if (rsaKeyValue != null) {
            children.add(rsaKeyValue);
        }
        if (ecKeyValue != null) {
            children.add(ecKeyValue);
        }
        if (unknownXMLObject != null) {
            children.add(unknownXMLObject);
        }
        
        if (children.size() == 0) {
            return null;
        }
        
        return Collections.unmodifiableList(children);
    }

}