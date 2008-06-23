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

import org.opensaml.ws.soap.util.SOAPConstants;
import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.ElementExtensibleXMLObject;
import org.opensaml.xml.schema.XSBooleanValue;

/**
 * The &lt;wsse:Security&gt; header block.
 * 
 * @see "WS-Security 2004, Chapter 5 Security Header."
 * 
 */
public interface Security extends AttributeExtensibleXMLObject, ElementExtensibleXMLObject, WSSecurityObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "Security";

    /** Qualified element name */
    public final static QName ELEMENT_NAME = new QName(WSSecurityConstants.WSSE_NS, ELEMENT_LOCAL_NAME,
            WSSecurityConstants.WSSE_PREFIX);

    /**
     * The wsse:Security/@S11:mustUnderstand (1 or 0) or wsse:Security/@S12:mustUnderstand (1, true or 0, false)
     * attribute local name
     */
    public final static String MUST_UNDERSTAND_ATTR_LOCAL_NAME = "mustUnderstand";

    /** The wsse:Security/@S11:mustUnderstand qualified attribute name */
    public final static QName MUST_UNDERSTAND_ATTR_NAME = new QName(SOAPConstants.SOAP11_NS,
            MUST_UNDERSTAND_ATTR_LOCAL_NAME, SOAPConstants.SOAP11_PREFIX);

    /** The wsse:Security/@S12:role attribute local name */
    public final static String ROLE_ATTR_LOCAL_NAME = "role";

    /** The wsse:Security/@S12:role attribute local name */
    public final static QName ROLE_ATTR_NAME = new QName(SOAPConstants.SOAP12_NS, ROLE_ATTR_LOCAL_NAME,
            SOAPConstants.SOAP12_PREFIX);

    /** The wsse:Security/@S11:actor attribute local name */
    public final static String ACTOR_ATTR_LOCAL_NAME = "actor";

    /** The wsse:Security/@S11:actor qualified attribute name */
    public final static QName ACTOR_ATTR_NAME = new QName(SOAPConstants.SOAP11_NS, ACTOR_ATTR_LOCAL_NAME,
            SOAPConstants.SOAP11_PREFIX);

    /**
     * Returns the &lt;wsse:Security/@S11:mustUnderstand&gt; or &lt;wsse:Security/@S12:mustUnderstand&gt; attribute
     * value.
     * 
     * @return the effective {@link XSBooleanValue} or by default the FALSE value.
     */
    public XSBooleanValue getMustUnderstand();

    /**
     * Sets the &lt;wsse:Security/@S11:mustUnderstand&gt; or &lt;wsse:Security/@S12:mustUnderstand&gt; attribute value.
     * 
     * @param mustUnderstand the {@link XSBooleanValue} attribute value to set.
     */
    public void setMustUnderstand(XSBooleanValue mustUnderstand);

    /**
     * Returns the wsse:Security/@S12:role attribute value.
     * 
     * @return the attribute value or <code>null</code>.
     */
    public String getRole();

    /**
     * Sets the wsse:Security/@S12:role attribute value.
     * 
     * @param role the attribute value.
     */
    public void setRole(String role);

    /**
     * Returns the wsse:Security/@S11:actor attribute value.
     * 
     * @return the attribute value or <code>null</code>.
     */
    public String getActor();

    /**
     * Sets the wsse:Security/@S11:actor attribute value.
     * 
     * @param actor the attribute value.
     */
    public void setActor(String actor);

}
