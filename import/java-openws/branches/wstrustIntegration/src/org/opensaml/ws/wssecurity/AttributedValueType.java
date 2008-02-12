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
package org.opensaml.ws.wssecurity;

import javax.xml.namespace.QName;

/**
 * Interface AttributedValueType for element having a &lt;@wsse:ValueType&gt;
 * attribute.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public abstract interface AttributedValueType {

    /** the wsse:ValueType attribute local name */
    public final static String VALUE_TYPE_ATTR_LOCAL_NAME= "ValueType";

    /** the wsse:ValueType qualified attribute name */
    public final QName VALUE_TYPE_ATTR_NAME= new QName(WSSecurityConstants.WSSE_NS,
                                                       VALUE_TYPE_ATTR_LOCAL_NAME,
                                                       WSSecurityConstants.WSSE_PREFIX);

    /**
     * The wsse:Reference/@ValueType attribute URI value
     * <code>#UsernameToken</code>
     */
    public static final String VALUETYPE_USERNAME_TOKEN= WSSecurityConstants.WSSE_USERNAME_TOKEN_PROFILE_NS
            + "#UsernameToken";

    /**
     * The wsse:BinarySecurityToken/@wsse:ValueType attribute URI value
     * <code>#X509v3</code>.
     * <p>
     * A X.509 v3 certificate capable of signature-verification at a minimum.
     */
    public final static String VALUETYPE_X509_V3= WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS
            + "#X509v3";

    /**
     * The wsse:BinarySecurityToken/@wsse:ValueType attribute URI value
     * <code>#X509v1</code>
     * <p>
     * A X.509 v1 certificate capable of signature-verification at a minimum.
     */
    public final static String VALUETYPE_X509_V1= WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS
            + "#X509v1";

    /**
     * The wsse:BinarySecurityToken/@wsse:ValueType attribute URI value
     * <code>#X509PKIPathv1</code>
     * <p>
     * An ordered list of X.509 certificates packaged in a PKIPath.
     */
    public final static String VALUETYPE_X509_PKI_PATH_V1= WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS
            + "#X509PKIPathv1";

    /**
     * The wsse:BinarySecurityToken/@wsse:ValueType attribute URI value
     * <code>#PKCS7</code>
     * <P>
     * A list of X.509 certificates and (optionally) CRLs packaged in a PKCS#7
     * wrapper.
     */
    public final static String VALUETYPE_X509_PKCS7= WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS
            + "#PKCS7";

    /**
     * The wsse:KeyIdentifier/@wsse:ValueType attribute URI value
     * <code>#X509SubjectKeyIdentifier</code>
     * <p>
     * Value of the certificate's X.509 SubjectKeyIdentifier.
     */
    public final static String VALUETYPE_X509_SUBJECT_KEY_IDENTIFIER= WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS
            + "#X509SubjectKeyIdentifier";

    /**
     * The wsse:KeyIdentifier/@wsse:ValueType attribute URI value
     * <code>#ThumbPrintSHA1</code>
     */
    public final static String VALUETYPE_THUMB_PRINT_SHA1= WSSecurityConstants.WS_SECURITY_NS
            + "#ThumbPrintSHA1";

    /**
     * The wsse:KeyIdentifier/@wsse:ValueType attribute URI value
     * <code>#EncyptedKeySHA1</code>
     */
    public final static String VALUETYPE_ENCRYPTED_KEY_SHA1= WSSecurityConstants.WS_SECURITY_NS
            + "#EncryptedKeySHA1";

    /**
     * Returns the &lt;@wsse:ValueType&gt; attribute URI value.
     * 
     * @return the ValueType attribute value or <code>null</code>.
     */
    public String getValueType();

    /**
     * Sets the &lt;@wsse:ValueType&gt; attribute URI value.
     * 
     * @param valueType
     *            the ValueType attribute value.
     */
    public void setValueType(String valueType);

}
