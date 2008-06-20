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
 * Interface AttributedEncodingType for element having an &lt;@EncodingType&gt; attribute.
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public abstract interface AttributedEncodingType {

    /** the EncodingType attribute local name */
    public final static String ENCODING_TYPE_ATTR_LOCAL_NAME = "EncodingType";

    /** the EncodingType unqualified attribute name */
    public final static QName ENCODING_TYPE_ATTR_NAME = new QName(ENCODING_TYPE_ATTR_LOCAL_NAME);

    /**
     * The EncodingType attribute value <code>#Base64Binary</code>
     */
    public final static String ENCODINGTYPE_BASE64_BINARY = WSSecurityConstants.WS_SECURITY_NS + "#Base64Binary";

    /**
     * Returns the EncodingType attribute value.
     * 
     * @return the EncodingType attribute value.
     */
    public String getEncodingType();

    /**
     * Sets the EncodingType attribute value.
     * 
     * @param encodingType the EncodingType attribute value.
     */
    public void setEncodingType(String encodingType);

}
