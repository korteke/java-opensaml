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

import org.opensaml.xml.AttributeExtensibleXMLObject;

/**
 * The &lt;wst:RequestSecurityTokenResponseCollection&gt; element (RSTRC)
 * <b>must</b> be used to return a response to a security token request (RST or
 * RSTC) on the final response.
 * 
 * @see RequestSecurityTokenResponse
 * @see "WS-Trust 1.3 Specification"
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface RequestSecurityTokenResponseCollection extends
        AttributeExtensibleXMLObject, WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "RequestSecurityTokenResponseCollection";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /**
     * Returns the list of &lt;wst:RequestSecurityTokenResponse&gt; child
     * elements.
     * 
     * @return The list of {@link RequestSecurityTokenResponse}s.
     */
    public List<RequestSecurityTokenResponse> getRequestSecurityTokenResponses();

    /**
     * Sets he list of &lt;wst:RequestSecurityTokenResponse&gt; child elements.
     * 
     * @param requestSecurityTokenResponses
     *            the list of {@link RequestSecurityTokenResponse}s.
     */
    public void setRequestSecurityTokenResponses(
            List<RequestSecurityTokenResponse> requestSecurityTokenResponses);

}
