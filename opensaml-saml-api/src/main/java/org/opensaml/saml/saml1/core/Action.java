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

/**
 * 
 */

package org.opensaml.saml.saml1.core;

import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * Interface describing how a SAML1.1 <code> Action </code> element behaves.
 */
public interface Action extends SAMLObject {

    /** Default element name, no namespace. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Action";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML1_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML1_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "ActionType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML1_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);

    /** Name for the Namespace attribute. */
    public static final String NAMESPACEATTRIB_NAME = "Namespace";

    /** Return the value of Namespace.
     * @return the namespace*/
    public String getNamespace();

    /** Set the value of Namespace.
     * @param namespace what to set */
    public void setNamespace(String namespace);

    /** Return the contents.
     * @return the action contents. */
    public String getContents();

    /** Set the contents.
     * @param contents what to set. */
    public void setContents(String contents);
}