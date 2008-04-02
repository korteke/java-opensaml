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

/**
 * The &lt;wst:RequestSecurityToken&gt; element (RST).
 * <p>
 * The element have the following additional possible child elements:
 * <ul>
 * <li>{@link Claims}
 * </ul>
 * 
 * @see RequestSecurityTokenType
 * @see "WS-Trust 1.3 Specification"
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision$
 */
public interface RequestSecurityToken extends RequestSecurityTokenType,
        WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "RequestSecurityToken";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /**
     * Returns the &lt;wst:Claims&gt; child element.
     * 
     * @return the {@link Claims} child element.
     */
    public Claims getClaims();

    /**
     * Sets the &lt;wst:Claims&gt; child element.
     * 
     * @param claims
     *            the {@link Claims} child element to set.
     */
    public void setClaims(Claims claims);

}
