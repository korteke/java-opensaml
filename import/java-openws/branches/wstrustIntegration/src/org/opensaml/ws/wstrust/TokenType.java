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


import org.opensaml.ws.wssecurity.WSSecurityConstants;
import org.opensaml.xml.schema.XSURI;

/**
 * The &lt;wst:TokenType&gt; element.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface TokenType extends XSURI, WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "TokenType";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /** Username Token identifier URI */
    public static final String USERNAME_TOKEN_URI= WSSecurityConstants.WSSE_USERNAME_TOKEN_PROFILE_NS;

    /** X509 Token identifier URI */
    public static final String X509_TOKEN_URI= WSSecurityConstants.WSSE_X509_TOKEN_PROFILE_NS
            + "#X509v3";

    /** SAML 2 Token identifier URI */
    public static final String SAML2_TOKEN_URI= WSSecurityConstants.WSSE11_SAML_TOKEN_PROFILE_NS
            + "#SAMLV2.0";

}
