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

import org.opensaml.ws.wstrust.Renewing;

/**
 * RenewingImpl
 * 
 */
public class RenewingImpl extends AbstractWSTrustObject implements Renewing {

    /** Allow attribute value */
    private Boolean allow_ = null;

    /** OK attribute value */
    private Boolean ok_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public RenewingImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Renewing#getAllow()
     */
    public Boolean getAllow() {
        return allow_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Renewing#getOK()
     */
    public Boolean getOK() {
        return ok_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Renewing#setAllow(Boolean)
     */
    public void setAllow(Boolean allow) {
        allow_ = prepareForAssignment(allow_, allow);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Renewing#setOK(Boolean)
     */
    public void setOK(Boolean ok) {
        ok_ = prepareForAssignment(ok_, ok);
    }

}
