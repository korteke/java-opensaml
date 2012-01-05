/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.wssecurity;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XmlConstants;

import org.opensaml.xml.schema.XSInteger;

/**
 * The &lt;wsse11:Iteration&gt; element within a &lt;wsse:UsernameToken&gt;
 * element.
 * 
 * @see "WS-Security UsernameToken Profile 1.1, 4. Key Derivation."
 * 
 */
public interface Iteration extends XSInteger, WSSecurityObject {
    
    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "Iteration";

    /** Qualified element name. */
    public static final QName ELEMENT_NAME =
        new QName(WSSecurityConstants.WSSE11_NS, ELEMENT_LOCAL_NAME, WSSecurityConstants.WSSE11_PREFIX);
    
    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "unsignedInt"; 
        
    /** QName of the XSI type. */
    public static final QName TYPE_NAME = new QName(XmlConstants.XSD_NS, TYPE_LOCAL_NAME, XmlConstants.XSD_PREFIX);

}
