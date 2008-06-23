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

package org.opensaml.ws.wstrust.impl;

import java.util.Collections;
import java.util.List;

import org.opensaml.ws.wstrust.Authenticator;
import org.opensaml.ws.wstrust.RequestSecurityTokenResponse;
import org.opensaml.ws.wstrust.RequestedAttachedReference;
import org.opensaml.ws.wstrust.RequestedProofToken;
import org.opensaml.ws.wstrust.RequestedSecurityToken;
import org.opensaml.ws.wstrust.RequestedUnattachedReference;
import org.opensaml.ws.wstrust.Status;
import org.opensaml.xml.XMLObject;

/**
 * RequestSecurityTokenResponseImpl
 * 
 * @see RequestSecurityTokenResponse
 * 
 */
public class RequestSecurityTokenResponseImpl extends AbstractRequestSecurityTokenType implements
        RequestSecurityTokenResponse {

    /** the &lt;wst:Authenticator&gt; child element */
    private Authenticator authenticator_ = null;

    /** the &lt;wst:RequestedAttachedReference&gt; child element */
    private RequestedAttachedReference requestedAttachedReference_ = null;

    /** the &lt;wst:RequestedProofToken&gt; child element */
    private RequestedProofToken requestedProofToken_ = null;

    /** the &lt;wst:RequestedSecurityToken&gt; child element */
    private RequestedSecurityToken requestedSecurityToken_ = null;

    /** the &lt;wst:RequestedUnattachedReference&gt; child element */
    private RequestedUnattachedReference requestedUnattachedReference_ = null;

    /** the &lt;wst:Status&gt; child element */
    private Status status_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public RequestSecurityTokenResponseImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#getAuthenticator()
     */
    public Authenticator getAuthenticator() {
        return authenticator_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#getRequestedAttachedReference()
     */
    public RequestedAttachedReference getRequestedAttachedReference() {
        return requestedAttachedReference_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#getRequestedProofToken()
     */
    public RequestedProofToken getRequestedProofToken() {
        return requestedProofToken_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#getRequestedSecurityToken()
     */
    public RequestedSecurityToken getRequestedSecurityToken() {
        return requestedSecurityToken_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#getRequestedUnattachedReference()
     */
    public RequestedUnattachedReference getRequestedUnattachedReference() {
        return requestedUnattachedReference_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#getStatus()
     */
    public Status getStatus() {
        return status_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#setAuthenticator(org.opensaml.ws.wstrust.Authenticator)
     */
    public void setAuthenticator(Authenticator authenticator) {
        authenticator_ = prepareForAssignment(authenticator_, authenticator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#setRequestedAttachedReference(org.opensaml.ws.wstrust.RequestedAttachedReference)
     */
    public void setRequestedAttachedReference(RequestedAttachedReference requestedAttachedReference) {
        requestedAttachedReference_ = prepareForAssignment(requestedAttachedReference_, requestedAttachedReference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#setRequestedProofToken(org.opensaml.ws.wstrust.RequestedProofToken)
     */
    public void setRequestedProofToken(RequestedProofToken requestedProofToken) {
        requestedProofToken_ = prepareForAssignment(requestedProofToken_, requestedProofToken);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#setRequestedSecurityToken(org.opensaml.ws.wstrust.RequestedSecurityToken)
     */
    public void setRequestedSecurityToken(RequestedSecurityToken requestedSecurityToken) {
        requestedSecurityToken_ = prepareForAssignment(requestedSecurityToken_, requestedSecurityToken);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#setRequestedUnattachedReference(org.opensaml.ws.wstrust.RequestedUnattachedReference)
     */
    public void setRequestedUnattachedReference(RequestedUnattachedReference requestedUnattachedReference) {
        requestedUnattachedReference_ = prepareForAssignment(requestedUnattachedReference_,
                requestedUnattachedReference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponse#setStatus(org.opensaml.ws.wstrust.Status)
     */
    public void setStatus(Status status) {
        status_ = prepareForAssignment(status_, status);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children = getCommonChildren();
        if (authenticator_ != null) {
            children.add(authenticator_);
        }
        if (requestedAttachedReference_ != null) {
            children.add(requestedAttachedReference_);
        }
        if (requestedProofToken_ != null) {
            children.add(requestedProofToken_);
        }
        if (requestedSecurityToken_ != null) {
            children.add(requestedSecurityToken_);
        }
        if (requestedUnattachedReference_ != null) {
            children.add(requestedUnattachedReference_);
        }
        return Collections.unmodifiableList(children);
    }

}
