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

import org.opensaml.xml.ElementExtensibleXMLObject;

/**
 * The &lt;wst:Participants&gt; element containing a &lt;wst:Primary&gt; and a
 * &lt;Participant&gt; child elements.
 * 
 * @see Primary
 * @see Participant
 * @see "WS-Trust 1.3, Chapter 9.5 Authorized Token Participants."
 * 
 */
public interface Participants extends ElementExtensibleXMLObject, WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "Participants";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /**
     * Returns the &lt;wst:Primary&gt; child element.
     * 
     * @return the {@link Primary} child element or <code>null</code>.
     */
    public Primary getPrimary();

    /**
     * Sets the &lt;wst:Primary&gt; child element.
     * 
     * @param primary
     *            the {@link Primary} child element to set.
     */
    public void setPrimary(Primary primary);

    /**
     * Returns the &lt;wst:Participant&gt; child element.
     * 
     * @return the {@link Participant} child element or <code>null</code>.
     */
    public Participant getParticipant();

    /**
     * Sets the &lt;wst:Participant&gt; child element.
     * 
     * @param participant
     *            the {@link Participant} child element to set.
     */
    public void setParticipant(Participant participant);
}
