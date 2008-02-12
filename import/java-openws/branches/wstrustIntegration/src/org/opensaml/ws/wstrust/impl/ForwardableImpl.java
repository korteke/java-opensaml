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


import org.opensaml.ws.wstrust.Forwardable;
import org.opensaml.xml.schema.XSBooleanValue;

/**
 * ForwardableImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class ForwardableImpl extends AbstractWSTrustObject implements
        Forwardable {

    /** The wst:Forwardable content. */
    private XSBooleanValue forwardable_= null;

    /**
     * Constructor. Default value is <code>true</code>.
     * <p>
     * {@inheritDoc}
     */
    public ForwardableImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        forwardable_= new XSBooleanValue(true, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.schema.XSBoolean#getValue()
     */
    public XSBooleanValue getValue() {
        return forwardable_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.schema.XSBoolean#setValue(org.opensaml.xml.schema.XSBooleanValue)
     */
    public void setValue(XSBooleanValue value) {
        forwardable_= prepareForAssignment(forwardable_, value);
    }

}
