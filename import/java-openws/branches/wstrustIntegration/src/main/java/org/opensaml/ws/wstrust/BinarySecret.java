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
import org.opensaml.xml.schema.XSBase64Binary;

/**
 * The &lt;wst:BinarySecret&gt; child element of a &lt;wst:Entropy&gt; element.
 * 
 * @see Entropy
 * @see "WS-Trust 1.3, Chapter 3.3 Binary Secrets."
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface BinarySecret extends XSBase64Binary, AttributeExtensibleXMLObject, WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "BinarySecret";

    /** Default element name */
    public final static QName ELEMENT_NAME = new QName(WSTrustConstants.WST_NS, ELEMENT_LOCAL_NAME,
            WSTrustConstants.WST_PREFIX);

    /** wst:BinarySecret/@Type attribute local name. */
    public static final String TYPE_ATTR_LOCAL_NAME = "Type";

    /** wst:BinarySecret/@Type attribute name. */
    public static final QName TYPE_ATTR_NAME = new QName(null, TYPE_ATTR_LOCAL_NAME);

    /** Type attribute AsymmetricKey URI */
    public static final String TYPE_ASYMMETRIC_KEY = WSTrustConstants.WST_NS + "/AsymmetricKey";

    /** Type attribute SymmetricKey URI */
    public static final String TYPE_SYMMETRIC_KEY = WSTrustConstants.WST_NS + "/SymmetricKey";

    /** Type attribute Nonce URI */
    public static final String TYPE_NONCE = WSTrustConstants.WST_NS + "/Nonce";

    /**
     * Returns the wst:BinarySecret/@Type attribute value.
     * 
     * @return the Type attribute value.
     */
    public String getType();

    /**
     * Sets the wst:BinarySecret/@Type attribute value.
     * 
     * @param type the Type attribute value to set.
     */
    public void setType(String type);

}
