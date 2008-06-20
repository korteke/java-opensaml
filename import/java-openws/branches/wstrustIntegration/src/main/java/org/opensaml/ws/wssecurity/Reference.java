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

/**
 * The &lt;wsse:Reference&gt; empty element.
 * 
 * @see "WS-Security 2004, Chapter 7.2"
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface Reference extends AttributedValueType, WSSecurityObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "Reference";

    /** Qualified element name */
    public final static QName ELEMENT_NAME = new QName(WSSecurityConstants.WSSE_NS, ELEMENT_LOCAL_NAME,
            WSSecurityConstants.WSSE_PREFIX);

    /** The wsse:Reference/@URI attribute local name. */
    public static final String URI_ATTR_LOCAL_NAME = "URI";

    /** The wsse:Reference/@URI attribute unqualified name. */
    public static final QName URI_ATTR_NAME = new QName(URI_ATTR_LOCAL_NAME);

    /**
     * Returns the wsse:Reference/@URI attribute value.
     * 
     * @return the URI attribute value.
     */
    public String getURI();

    /**
     * Sets the wsse:Reference/@URI attribute value.
     * 
     * @param uri the URI to set.
     */
    public void setURI(String uri);
}
