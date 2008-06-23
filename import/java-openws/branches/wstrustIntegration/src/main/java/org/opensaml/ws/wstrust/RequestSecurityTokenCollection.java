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
 * The &lt;wst:RequestSecurityTokenCollection&gt; (RSTC) element is used to
 * provide more than one RST requests.
 * <p>
 * One or more RSTR elements in a RSTRC element are returned in the response to
 * the RequestSecurityTokenCollection.
 * 
 * @see RequestSecurityToken.
 * @see "WS-Trust 1.3 Specification"
 * 
 */
public interface RequestSecurityTokenCollection extends WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "RequestSecurityTokenCollection";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /**
     * Returns the list of &lt;wst:RequestSecurityToken&gt; child elements
     * contained in the RSTC.
     * 
     * @return the list of {@link RequestSecurityToken}s.
     */
    public List<RequestSecurityToken> getRequestSecurityTokens();

    /**
     * Sets the list of &lt;wst:RequestSecurityToken&gt; child elements
     * contained in the RSTC.
     * 
     * @param requestSecurityTokens
     *            the list of {@link RequestSecurityToken}s.
     */
    public void setRequestSecurityTokens(
            List<RequestSecurityToken> requestSecurityTokens);

}
