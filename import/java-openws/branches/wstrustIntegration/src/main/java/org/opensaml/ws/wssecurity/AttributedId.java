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
 * Interface AttributedId for element having a &lt;@wsu:Id&gt; attribute.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public abstract interface AttributedId {

    /** the <code>Id</code> attribute local name */
    public final static String ID_ATTR_LOCAL_NAME = "Id";

    /** the <code>wsu:Id</code> qualified attribute name */
    public final static QName ID_ATTR_NAME = new QName(WSSecurityConstants.WSU_NS, ID_ATTR_LOCAL_NAME,
            WSSecurityConstants.WSU_PREFIX);

    /**
     * Returns the &lt;wsu:Id&gt; attribute value
     * 
     * @return The &lt;wsu:Id&gt; attribute value or <code>null</code>.
     */
    public String getId();

    /**
     * Sets the &lt;wsu:Id&gt; attribute value
     * 
     * @param id The &lt;wsu:Id&gt; attribute value
     */
    public void setId(String id);

}
