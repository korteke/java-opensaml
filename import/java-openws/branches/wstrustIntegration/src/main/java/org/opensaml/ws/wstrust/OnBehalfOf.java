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


import org.opensaml.ws.wsaddressing.EndpointReference;
import org.opensaml.ws.wssecurity.SecurityTokenReference;
import org.opensaml.xml.ElementExtensibleXMLObject;

/**
 * The &lt;wst:OnBehalfOf&gt; element, containing a security token (<code>xs:any</code>),
 * a &lt;wsse:SecurityTokenReference&gt; or a &lt;wsa:EndpointReference&gt;.
 * 
 * @see SecurityTokenReference
 * @see EndpointReference
 * 
 * @see "WS-Trust 1.3, Chapter 9.1 On-Behalf-Of Parameters."
 * 
 */
public interface OnBehalfOf extends ElementExtensibleXMLObject, WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "OnBehalfOf";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /**
     * Returns the &lt;wsse:SecurityTokenReference&gt; child element, if any.
     * 
     * @return the {@link SecurityTokenReference} or <code>null</code>.
     */
    public SecurityTokenReference getSecurityTokenReference();

    /**
     * Sets the &lt;wsse:SecurityTokenReference&gt; child element.
     * 
     * @param securityTokenReference
     *            the {@link SecurityTokenReference} child element to set.
     */
    public void setSecurityTokenReference(
            SecurityTokenReference securityTokenReference);

    /**
     * Returns the &lt;wsa:EndpointReference&gt; child element, if any.
     * 
     * @return the {@link EndpointReference} or <code>null</code>.
     */
    public EndpointReference getEndpointReference();

    /**
     * Sets the &lt;wsa:EndpointReference&gt; child element.
     * 
     * @param endpointReference
     *            The {@link EndpointReference} child element to set.
     */
    public void setEndpointReference(EndpointReference endpointReference);
}
