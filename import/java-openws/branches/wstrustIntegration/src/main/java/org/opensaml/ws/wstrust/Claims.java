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

import org.opensaml.ws.wssecurity.BinarySecurityToken;
import org.opensaml.ws.wssecurity.UsernameToken;
import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.ElementExtensibleXMLObject;

/**
 * The &lt;wst:Claims&gt; element containing a list of security tokens (<code>xs:any</code>).
 * 
 * @see UsernameToken
 * @see BinarySecurityToken
 * @see "WS-Trust 1.3, Chapter 4.1 Requesting a Security Token."
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface Claims extends AttributeExtensibleXMLObject, ElementExtensibleXMLObject, WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "Claims";

    /** Default element name */
    public final static QName ELEMENT_NAME = new QName(WSTrustConstants.WST_NS, ELEMENT_LOCAL_NAME,
            WSTrustConstants.WST_PREFIX);

    /** The wst:Claims/@Dialect attribute local name */
    public final static String DIALECT_ATTR_LOCAL_NAME = "Dialect";

    /** The wst:Claims/@Dialect attribute name */
    public final static QName DIALECT_ATTR_NAME = new QName(null, DIALECT_ATTR_LOCAL_NAME);

    /**
     * Returns the wst:Claims/@Dialect attribute value.
     * 
     * @return the Dialect attribute value
     */
    public String getDialect();

    /**
     * Sets the wst:Claims/@Dialect attribute value.
     * 
     * @param dialect the Dialect attribute value.
     */
    public void setDialect(String dialect);

}
