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
package org.opensaml.ws.wstrust.impl;

import org.opensaml.ws.wstrust.RequestedAttachedReference;

/**
 * Marshaller for the RequestedAttachedReference element.
 * 
 * @see RequestedAttachedReference
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class RequestedAttachedReferenceMarshaller extends
        AbstractRequestedReferenceTypeMarshaller {

    /**
     * Default constructor.
     */
    public RequestedAttachedReferenceMarshaller() {
        super(RequestedAttachedReference.ELEMENT_NAME.getNamespaceURI(),
              RequestedAttachedReference.ELEMENT_NAME.getLocalPart());
    }

}
