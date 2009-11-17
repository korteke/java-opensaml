/*
 * Copyright 2009 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.wspolicy;

import javax.xml.namespace.QName;

/**
 * The wsp:All element.
 * 
 * @see "WS-Policy (http://schemas.xmlsoap.org/ws/2004/09/policy)"
 */
public interface All extends OperatorContentType {
    
    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "All";

    /** Default element name. */
    public static final QName ELEMENT_NAME =
        new QName(WSPolicyConstants.WSP_NS, ELEMENT_LOCAL_NAME, WSPolicyConstants.WSP_PREFIX);

}
