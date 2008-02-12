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

import org.opensaml.ws.wssecurity.Created;
import org.opensaml.ws.wssecurity.Expires;

/**
 * The &lt;wst:Lifetime&gt; element.
 * 
 * @see Created
 * @see Expires
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface Lifetime extends WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "Lifetime";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /**
     * Returns the &lt;wsu:Created&gt; child element.
     * 
     * @return the {@link Created} child element or <code>null</code>.
     */
    public Created getCreated();

    /**
     * Sets the &lt;wsu:Created&gt; child element.
     * 
     * @param created
     *            the {@link Created} child element to set.
     */
    public void setCreated(Created created);

    /**
     * Returns the &lt;wsu:Expires&gt; child element.
     * 
     * @return the {@link Expires} child element or <code>null</code>.
     */
    public Expires getExpires();

    /**
     * Sets the &lt;wsu:Expires&gt; child element.
     * 
     * @param expires
     *            the {@link Expires} child element.
     */
    public void setExpires(Expires expires);

}
