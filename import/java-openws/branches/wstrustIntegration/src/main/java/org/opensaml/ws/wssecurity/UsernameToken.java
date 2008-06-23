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

import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.ElementExtensibleXMLObject;

/**
 * The &lt;wsse:UsernameToken&gt; element.
 * 
 * @see "WS-Security UsernameToken Profile 1.1"
 * 
 */
public interface UsernameToken extends AttributedId,
        AttributeExtensibleXMLObject, ElementExtensibleXMLObject,
        WSSecurityObject {
    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "UsernameToken";

    /** Qualified element name */
    public final static QName ELEMENT_NAME= new QName(WSSecurityConstants.WSSE_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSSecurityConstants.WSSE_PREFIX);

    /**
     * Returns the &lt;wsse:Username&gt; child element.
     * 
     * @return the {@link Username} child element.
     */
    public Username getUsername();

    /**
     * Sets the &lt;wsse:Username&gt; child element.
     * 
     * @param username
     *            the {@link Username} child element to set.
     */
    public void setUsername(Username username);

    /**
     * Returns the &lt;wsse:Password&gt; child element.
     * 
     * @return the {@link Password} child element or <code>null</code>
     */
    public Password getPassword();

    /**
     * Sets the &lt;wsse:Password&gt; child element.
     * 
     * @param password
     *            the {@link Password} child element to set.
     */
    public void setPassword(Password password);

    /**
     * Returns the &lt;wsse:Nonce&gt; child element.
     * 
     * @return the {@link Nonce} child element or <code>null</code>
     */
    public Nonce getNonce();

    /**
     * Sets the &lt;wsse:Nonce&gt; child element.
     * 
     * @param nonce
     *            the {@link Nonce} child element to set.
     */
    public void setNonce(Nonce nonce);

    /**
     * Returns the &lt;wsse:Created&gt; child element.
     * 
     * @return the {@link Created} child element or <code>null</code>
     */
    public Created getCreated();

    /**
     * Sets the &lt;wsse:Created&gt; child element.
     * 
     * @param created
     *            the Created child element to set.
     */
    public void setCreated(Created created);

    /**
     * Returns the &lt;wsse11:Salt&gt; child element for key derivation.
     * 
     * @return the {@link Salt} child element or <code>null</code>
     */
    public Salt getSalt();

    /**
     * Sets the &lt;wsse11:Salt&gt; child element for key derivation.
     * 
     * @param salt
     *            the {@link Salt} child element to set.
     */
    public void setSalt(Salt salt);

    /**
     * Returns the &lt;wsse11:Iteration&gt; child element for key derivation.
     * 
     * @return the {@link Iteration} child element or <code>null</code>
     */
    public Iteration getIteration();

    /**
     * Sets the &lt;wsse11:Iteration&gt; child element for key derivation.
     * 
     * @param iteration
     *            the {@link Iteration} child element to set.
     */
    public void setIteration(Iteration iteration);

}
