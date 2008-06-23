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
 * The &lt;wst:RequestSecurityTokenResponse&gt; element (RSTR).
 * <p>
 * The element have the following additional possible child elements:
 * <ul>
 * <li>{@link Authenticator}
 * <li>{@link RequestedAttachedReference}
 * <li>{@link RequestedUnattachedReference}
 * <li>{@link RequestedProofToken}
 * <li>{@link RequestedSecurityToken}
 * <li>{@link Status}
 * </ul>
 * 
 * @see RequestSecurityTokenType
 * @see "WS-Trust 1.3 Specification"
 * 
 */
public interface RequestSecurityTokenResponse extends RequestSecurityTokenType,
        WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "RequestSecurityTokenResponse";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /**
     * Returns the &lt;wst:RequestedSecurityToken&gt; child element.
     * 
     * @return the {@link RequestedSecurityToken} element or <code>null</code>.
     */
    public RequestedSecurityToken getRequestedSecurityToken();

    /**
     * Sets the &lt;wst:RequestedSecurityToken&gt; child element.
     * 
     * @param requestedSecurityToken
     *            the {@link RequestSecurityToken} to set.
     */
    public void setRequestedSecurityToken(
            RequestedSecurityToken requestedSecurityToken);

    /**
     * Returns the &lt;wst:RequestedAttachedReference&gt; child element.
     * 
     * @return the {@link RequestedAttachedReference} child element or
     *         <code>null</code>.
     */
    public RequestedAttachedReference getRequestedAttachedReference();

    /**
     * Sets the &lt;wst:RequestedAttachedReference&gt; child element.
     * 
     * @param requestedAttachedReference
     *            the {@link RequestedAttachedReference} child element.
     */
    public void setRequestedAttachedReference(
            RequestedAttachedReference requestedAttachedReference);

    /**
     * Returns the &lt;wst:RequestedUnattachedReference&gt; child element.
     * 
     * @return the {@link RequestedUnattachedReference} child element or
     *         <code>null</code>
     */
    public RequestedUnattachedReference getRequestedUnattachedReference();

    /**
     * Sets the &lt;wst:RequestedUnattachedReference&gt; child element.
     * 
     * @param requestedUnattachedReference
     *            the {@link RequestedUnattachedReference} child element.
     */
    public void setRequestedUnattachedReference(
            RequestedUnattachedReference requestedUnattachedReference);

    /**
     * Returns the &lt;wst:RequestedProofToken&gt; child element.
     * 
     * @return the {@link RequestedProofToken} child element or
     *         <code>null</code>.
     */
    public RequestedProofToken getRequestedProofToken();

    /**
     * Sets the &lt;wst:RequestedProofToken&gt; child element.
     * 
     * @param requestedProofToken
     *            the {@link RequestedProofToken} child element.
     */
    public void setRequestedProofToken(RequestedProofToken requestedProofToken);

    /**
     * Returns the &lt;wst:Status&gt; child element.
     * 
     * @return the {@link Status} child element or <code>null</code>.
     */
    public Status getStatus();

    /**
     * Sets the &lt;wst:Status&gt; child element.
     * 
     * @param status
     *            the {@link Status} child element.
     */
    public void setStatus(Status status);

    /**
     * Returns the &lt;wst:Authenticator&gt; child element.
     * 
     * @return {@link Authenticator} child element or <code>null</code>.
     */
    public Authenticator getAuthenticator();

    /**
     * Sets &lt;wst:Authenticator&gt; child element.
     * 
     * @param authenticator
     *            the {@link Authenticator} child element to set.
     */
    public void setAuthenticator(Authenticator authenticator);

}
