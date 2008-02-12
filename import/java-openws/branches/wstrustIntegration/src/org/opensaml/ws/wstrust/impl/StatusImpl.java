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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.opensaml.ws.wstrust.Code;
import org.opensaml.ws.wstrust.Reason;
import org.opensaml.ws.wstrust.Status;
import org.opensaml.xml.XMLObject;

/**
 * StatusImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class StatusImpl extends AbstractWSTrustObject implements Status {

    /** The {@link Code} child element */
    private Code code_= null;

    /** The {@link Reason} child element */
    private Reason reason_= null;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public StatusImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.trust.Status#getCode()
     */
    public Code getCode() {
        return code_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.trust.Status#getReason()
     */
    public Reason getReason() {
        return reason_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.trust.Status#setCode(org.glite.xml.trust.Code)
     */
    public void setCode(Code code) {
        code_= prepareForAssignment(code_, code);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.trust.Status#setReason(org.glite.xml.trust.Reason)
     */
    public void setReason(Reason reason) {
        reason_= prepareForAssignment(reason_, reason);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children= new ArrayList<XMLObject>();
        if (code_ != null) {
            children.add(code_);
        }
        if (reason_ != null) {
            children.add(reason_);
        }
        return Collections.unmodifiableList(children);
    }

}
