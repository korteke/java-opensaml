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
 * The &lt;wst:Status&gt; element containing a &lt;wst:Code&gt; and a
 * &lt;Reason&gt; elements.
 * 
 * @see Code
 * @see Reason
 * @see "WS-Trust 1.3, Chapter 7 Validation Binding."
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface Status extends WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "Status";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /**
     * Returns the &lt;wst:Code&gt; child element.
     * 
     * @return the {@link Code} child element or <code>null</code>
     */
    public Code getCode();

    /**
     * Sets the &lt;wst:Code&gt; child element.
     * 
     * @param code
     *            the {@link Code} child element to set.
     */
    public void setCode(Code code);

    /**
     * Returns the {@link Reason} child element.
     * 
     * @return the {@link Reason} child element or <code>null</code>.
     */
    public Reason getReason();

    /**
     * Sets the {@link Reason} child element.
     * 
     * @param reason
     *            the {@link Reason} child element to set.
     */
    public void setReason(Reason reason);

}
