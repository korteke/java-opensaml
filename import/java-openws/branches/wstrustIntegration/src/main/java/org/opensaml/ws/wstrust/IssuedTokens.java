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

import java.util.List;

import javax.xml.namespace.QName;

/**
 * The &lt;wst:IssuedTokens&gt; element used to return security tokens in the
 * SOAP header. Contains a list of &lt;wst:RequestSecurityTokenResponse&gt;s.
 * 
 * @see RequestSecurityTokenResponse
 * @see "WS-Trust 1.3, Chapter 4.5 Returning Security Tikens in Headers."
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface IssuedTokens extends WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "IssuedTokens";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /**
     * Returns the list of &lt;wst:RequestSecurityTokenResponse&gt; child
     * elements.
     * 
     * @return the list of {@link RequestSecurityTokenResponse} child elements.
     */
    public List<RequestSecurityTokenResponse> getRequestSecurityTokenResponses();

    /**
     * Sets the list of &lt;wst:RequestSecurityTokenResponse&gt; child elements.
     * 
     * @param requestSecurityTokenResponses
     *            the list of {@link RequestSecurityTokenResponse} child
     *            elements.
     */
    public void setRequestSecurityTokenResponses(
            List<RequestSecurityTokenResponse> requestSecurityTokenResponses);
}
