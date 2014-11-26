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

import java.math.BigInteger;
import java.util.Objects;

import org.opensaml.core.xml.schema.impl.XSBase64BinaryImpl;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.CryptoBinary;

import com.google.common.base.Strings;

/**
 * Concrete implementation of {@link org.opensaml.xmlsec.signature.CryptoBinary}.
 */
public class CryptoBinaryImpl extends XSBase64BinaryImpl implements CryptoBinary {
    
    /** The cached BigInteger representation of the element's base64-encoded value. */
    private BigInteger bigIntValue;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected CryptoBinaryImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Override
    public BigInteger getValueBigInt() {
        if (bigIntValue == null && !Strings.isNullOrEmpty(getValue())) {
            bigIntValue = KeyInfoSupport.decodeBigIntegerFromCryptoBinary(getValue());
        }
        return bigIntValue;
    }

    /** {@inheritDoc} */
    @Override
    public void setValueBigInt(BigInteger bigInt) {
        if (bigInt == null) {
            setValue(null);
        } else {
            setValue(KeyInfoSupport.encodeCryptoBinaryFromBigInteger(bigInt));
        }
        bigIntValue = bigInt;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setValue(String newValue) {
        if (bigIntValue != null 
                && (!Objects.equals(getValue(), newValue) || newValue == null)) {
            // Just clear the cached value, my not be needed in big int form again,
            // let it be lazily recreated if necessary
            bigIntValue = null;
        }
        super.setValue(newValue);
    }

}