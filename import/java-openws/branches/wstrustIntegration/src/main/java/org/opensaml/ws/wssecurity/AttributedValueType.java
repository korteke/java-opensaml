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
 * Interface AttributedValueType for element having a &lt;@ValueType&gt; attribute.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public abstract interface AttributedValueType {

    /** the wsse:ValueType attribute local name */
    public final static String VALUE_TYPE_ATTR_LOCAL_NAME = "ValueType";

    /** the wsse:ValueType unqualified attribute name */
    public final QName VALUE_TYPE_ATTR_NAME = new QName(WSSecurityConstants.WSSE_NS, VALUE_TYPE_ATTR_LOCAL_NAME);

    /**
     * The ValueType attribute UsernameToken URI value <code>#UsernameToken</code>.
     * 
     * @see Reference
     */
    public static final String VALUETYPE_USERNAME_TOKEN = WSSecurityConstants.WSSE_USERNAME_TOKEN_PROFILE_NS
            + "#UsernameToken";

    /**
     * The &lt;ValueType&gt; attribute X.509 URI value <code>#X509v3</code>.
     * <p>
     * A X.509 v3 certificate capable of signature-verification at a minimum.
     * 
     * @see BinarySecurityToken
     */
    public final static String VALUETYPE_X509_V3 = WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS + "#X509v3";

    /**
     * The &lt;ValueType&gt; attribute X.509 URI value <code>#X509v1</code>
     * <p>
     * A X.509 v1 certificate capable of signature-verification at a minimum.
     * 
     * @see BinarySecurityToken
     */
    public final static String VALUETYPE_X509_V1 = WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS + "#X509v1";

    /**
     * The &lt;ValueType&gt; attribute X.509 URI value <code>#X509PKIPathv1</code>
     * <p>
     * An ordered list of X.509 certificates packaged in a PKIPath.
     * 
     * @see BinarySecurityToken
     */
    public final static String VALUETYPE_X509_PKI_PATH_V1 = WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS
            + "#X509PKIPathv1";

    /**
     * The &lt;ValueType&gt; attribute X.509 URI value <code>#PKCS7</code>
     * <P>
     * A list of X.509 certificates and (optionally) CRLs packaged in a PKCS#7 wrapper.
     * 
     * @see BinarySecurityToken
     */
    public final static String VALUETYPE_X509_PKCS7 = WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS + "#PKCS7";

    /**
     * The &lt;ValueType&gt; attribute Kerberos URI value <code>#Kerberosv5_AP_REQ</code>
     * <p>
     * Kerberos v5 AP-REQ as defined in the in the Kerberos specification.
     * 
     * @see BinarySecurityToken
     */
    public final static String VALUETYPE_KERBEROS_AP_REQ = WSSecurityConstants.WSSE_KERBEROS_TOKEN_PROFILE_NS
            + "#Kerberosv5_AP_REQ";

    /**
     * The &lt;ValueType&gt; attribute Kerberos URI value <code>#GSS_Kerberosv5_AP_REQ</code>
     * <p>
     * A GSS-API Kerberos v5 mechanism token containing an KRB_AP_REQ message as defined in the in RFC-1964, Sec 1.1 and
     * its successor RFC-4121, Sec 4.1.
     * 
     * @see BinarySecurityToken
     */
    public final static String VALUETYPE_GSS_KERBEROS_AP_REQ = WSSecurityConstants.WSSE_KERBEROS_TOKEN_PROFILE_NS
            + "#GSS_Kerberosv5_AP_REQ";

    /**
     * The &lt;ValueType&gt; attribute Kerberos URI value <code>#Kerberosv5_AP_REQ1510</code>
     * <p>
     * Kerberos v5 AP-REQ as defined in RFC-1510.
     * 
     * @see BinarySecurityToken
     */
    public final static String VALUETYPE_KERBEROS_AP_REQ_1510 = WSSecurityConstants.WSSE_KERBEROS_TOKEN_PROFILE_NS
            + "#Kerberosv5_AP_REQ1510";

    /**
     * The &lt;ValueType&gt; attribute Kerberos URI value <code>#GSS_Kerberosv5_AP_REQ1510</code>
     * <p>
     * A GSS-API Kerberos v5 mechanism token containing an KRB_AP_REQ message as defined in the in RFC-1964, Sec 1.1 and
     * its successor RFC-4121, Sec 4.1. Per RFC-1510.
     * 
     * @see BinarySecurityToken
     */
    public final static String VALUETYPE_GSS_KERBEROS_AP_REQ_1510 = WSSecurityConstants.WSSE_KERBEROS_TOKEN_PROFILE_NS
            + "#GSS_Kerberosv5_AP_REQ1510";

    /**
     * The &lt;ValueType&gt; attribute X.509 URI value <code>#X509SubjectKeyIdentifier</code>
     * <p>
     * Value of the certificate's X.509 SubjectKeyIdentifier.
     * 
     * @see KeyIdentifier
     */
    public final static String VALUETYPE_X509_SUBJECT_KEY_IDENTIFIER = WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS
            + "#X509SubjectKeyIdentifier";

    /**
     * The &lt;ValueType&gt; attribute URI value <code>#ThumbPrintSHA1</code>
     * 
     * @see KeyIdentifier
     */
    public final static String VALUETYPE_THUMB_PRINT_SHA1 = WSSecurityConstants.WS_SECURITY_NS + "#ThumbPrintSHA1";

    /**
     * The &lt;ValueType&gt; attribute URI value <code>#EncyptedKeySHA1</code>
     * 
     * @see KeyIdentifier
     */
    public final static String VALUETYPE_ENCRYPTED_KEY_SHA1 = WSSecurityConstants.WS_SECURITY_NS + "#EncryptedKeySHA1";

    /**
     * Returns the &lt;@ValueType&gt; attribute URI value.
     * 
     * @return the ValueType attribute value or <code>null</code>.
     */
    public String getValueType();

    /**
     * Sets the &lt;@ValueType&gt; attribute URI value.
     * 
     * @param valueType the ValueType attribute value.
     */
    public void setValueType(String valueType);

}
