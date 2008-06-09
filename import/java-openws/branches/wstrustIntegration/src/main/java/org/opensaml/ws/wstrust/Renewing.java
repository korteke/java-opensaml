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
 * The &lt;wst:Renewing&gt; empty element.
 * 
 * @see "WS-Trust 1.3, Chapter 5 Renewal Binding."
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface Renewing extends WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "Renewing";

    /** Default element name */
    public final static QName ELEMENT_NAME = new QName(WSTrustConstants.WST_NS, ELEMENT_LOCAL_NAME,
            WSTrustConstants.WST_PREFIX);

    /** The wst:Renewing/@Allow attribute local name */
    public static final String ALLOW_ATTR_LOCAL_NAME = "Allow";

    /** The wst:Renewing/@Allow attribute name */
    public static final QName ALLOW_ATTR_NAME = new QName(WSTrustConstants.WST_NS, ALLOW_ATTR_LOCAL_NAME);

    /** The wst:Renewing/@OK attribute local name */
    public static final String OK_ATTR_LOCAL_NAME = "OK";

    /** The wst:Renewing/@OK attribute name */
    public static final QName OK_ATTR_NAME = new QName(WSTrustConstants.WST_NS, OK_ATTR_LOCAL_NAME);

    /**
     * Returns the wst:Renewing/@Allow attribute value.
     * 
     * @return the Allow attribute value or <code>null</code>.
     */
    public Boolean getAllow();

    /**
     * Sets the wst:Renewing/@Allow attribute value.
     * 
     * @param allow the Allow attribute value.
     */
    public void setAllow(Boolean allow);

    /**
     * Returns the wst:Renewing/@OK attribute value.
     * 
     * @return the OK attribute value or <code>null</code>
     */
    public Boolean getOK();

    /**
     * Sets the wst:Renewing/@OK attribute value.
     * 
     * @param ok the OK attribute value.
     */
    public void setOK(Boolean ok);

}
