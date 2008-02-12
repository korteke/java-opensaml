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

import org.opensaml.ws.wsaddressing.EndpointReference;

/**
 * The &lt;wst:Issuer&gt; element.
 * 
 * @see "WS-Trust 1.3, Chapter 9.1 On-Behalf-Of Parameters."
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface Issuer extends EndpointReference, WSTrustObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME= "Issuer";

    /** Default element name */
    public final static QName ELEMENT_NAME= new QName(WSTrustConstants.WST_NS,
                                                      ELEMENT_LOCAL_NAME,
                                                      WSTrustConstants.WST_PREFIX);
}
