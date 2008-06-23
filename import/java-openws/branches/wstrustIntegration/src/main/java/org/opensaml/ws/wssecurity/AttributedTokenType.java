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
 * Interface AttributedTokenType for element having a &lt;@wsse11:TokenType&gt; attribute.
 * 
 * @see "WS-Security 2004, Chapter 7.1 SecurityTokenReference Element."
 * 
 */
public abstract interface AttributedTokenType {

    /**
     * the <code>@wsse11:TokenType</code> attribute local name
     */
    public final static String TOKEN_TYPE_ATTR_LOCAL_NAME = "TokenType";

    /**
     * the <code>@wsse11:TokenType</code> qualified attribute name
     */
    public final static QName TOKEN_TYPE_ATTR_NAME = new QName(WSSecurityConstants.WSSE11_NS,
            TOKEN_TYPE_ATTR_LOCAL_NAME, WSSecurityConstants.WSSE11_PREFIX);

    /**
     * Returns the &lt;@wsse11:TokenType&gt; attribute value.
     * 
     * @return the &lt;@wsse11:TokenType&gt; attribute value or <code>null</code>.
     */
    public String getTokenType();

    /**
     * Sets the &lt;@wsse11:TokenType&gt; attribute value.
     * 
     * @param tokenType the &lt;@wsse11:TokenType&gt; attribute value to set.
     */
    public void setTokenType(String tokenType);

}
