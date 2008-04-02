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

import org.opensaml.xml.ElementExtensibleXMLObject;

/**
 * The &lt;wst:UseKey&gt; element contains a security token (<code>xs:any</code>).
 * 
 * @see "WS-Trust 1.3, Chapter 9.2 Key and Encryption Requirements."
 * 
 * @author Valery Tschopp <tschopp@switch.ch>
 * @version $Revision$
 */
public interface UseKey extends ElementExtensibleXMLObject, WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "UseKey";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);

    /** the wst:UseKey/@Sig attribute local name */
    public final static String SIG_ATTR_LOCAL_NAME= "Sig";

    /** the wst:UseKey/@Sig attribute name */
    public final static QName SIG_ATTR_NAME= new QName(WSTrustConstants.WST_NS,
                                                       SIG_ATTR_LOCAL_NAME,
                                                       WSTrustConstants.WST_PREFIX);

    /**
     * Returns the wst:UseKey/@Sig attribute value.
     * 
     * @return the Sig attribute value or <code>null</code>
     */
    public String getSig();

    /**
     * Sets the wst:UseKey/@Sig attribute value.
     * 
     * @param sig
     *            the Sig attribute value to set.
     */
    public void setSig(String sig);

}
