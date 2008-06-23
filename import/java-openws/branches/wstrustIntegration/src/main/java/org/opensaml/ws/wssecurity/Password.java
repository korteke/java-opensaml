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

import org.opensaml.xml.schema.XSString;

/**
 * The &lt;wsse:Password&gt; element within a &lt;wsse:UsernameToken&gt; element.
 * 
 * @see "WS-Security UsernameToken Profile 1.1"
 * 
 */
public interface Password extends AttributedId, XSString, WSSecurityObject {
    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "Password";

    /** Qualified element name */
    public final static QName ELEMENT_NAME = new QName(WSSecurityConstants.WSSE_NS, ELEMENT_LOCAL_NAME,
            WSSecurityConstants.WSSE_PREFIX);

    /** The <code>Type</code> attribute local name */
    public final static String TYPE_ATTR_LOCAL_NAME = "Type";

    /** The &lt;wsse:Password/@Type&gt; attribute unqualified name */
    public final static QName TYPE_ATTR_NAME = new QName(TYPE_ATTR_LOCAL_NAME);

    /**
     * The &lt;wsse:Password/@Type&gt; attribute URI value <code>#PasswordText</code> (DEFAULT)
     */
    public final static String TYPE_PASSWORD_TEXT = WSSecurityConstants.WSSE_NS + "#PasswordText";

    /**
     * The &lt;wsse:Password/@Type&gt; attribute URI value <code>#PasswordDigest</code>
     */
    public final static String TYPE_PASSWORD_DIGEST = WSSecurityConstants.WSSE_NS + "#PasswordDigest";

    /**
     * Returns the &lt;wsse:Password/@Type&gt; attribute URI value.
     * 
     * @return the <code>Type</code> attribute URI value.
     */
    public String getType();

    /**
     * Sets the &lt;wsse:Password/@Type&gt; attribute URI value.
     * 
     * @param type the <code>Type</code> attribute URI value to set.
     */
    public void setType(String type);

}
