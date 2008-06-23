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
package org.opensaml.ws.wstrust;

import javax.xml.namespace.QName;

import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.ElementExtensibleXMLObject;
import org.opensaml.xml.encryption.EncryptedKey;

/**
 * The &lt;wst:Entropy&gt; element contains either a &lt;xenc:EncryptedKey&gt;
 * or a &lt;wst:BinarySecret&gt; element. Or other element as
 * <code>xs:any</code>.
 * 
 * @see BinarySecret
 * @see EncryptedKey
 * @see "WS-Trust 1.3, Chapter 4.1 Requesting a Security Token."
 * 
 */
public interface Entropy extends AttributeExtensibleXMLObject,
        ElementExtensibleXMLObject, WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "Entropy";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /**
     * Returns the &lt;wst:BinarySecret&gt; child element.
     * 
     * @return the {@link BinarySecret} child element or <code>null</code>.
     */
    public BinarySecret getBinarySecret();

    /**
     * Sets the &lt;wst:BinarySecret&gt; child element.
     * 
     * @param binarySecret
     *            the {@link BinarySecret} child element to set.
     */
    public void setBinarySecret(BinarySecret binarySecret);

    /**
     * Returns the &lt;xenc:EncryptedKey&gt; child element.
     * 
     * @return the {@link EncryptedKey} child element or <code>null</code>.
     */
    public EncryptedKey getEncryptedKey();

    /**
     * Sets the &lt;xenc:EncryptedKey&gt; child element.
     * 
     * @param encryptedKey
     *            the {@link EncryptedKey} child element to set.
     */
    public void setEncryptedKey(EncryptedKey encryptedKey);
}
