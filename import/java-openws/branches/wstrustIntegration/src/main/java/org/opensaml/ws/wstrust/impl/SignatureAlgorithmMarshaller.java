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


import org.opensaml.ws.wstrust.SignatureAlgorithm;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSURI;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * Marshaller for the SignatureAlgorithm element.
 * 
 * @see SignatureAlgorithm
 * 
 */
public class SignatureAlgorithmMarshaller extends
        AbstractWSTrustObjectMarshaller {

    /**
     * Default constructor.
     * 
     */
    public SignatureAlgorithmMarshaller() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.impl.AbstractWSTrustObjectMarshaller#marshallElementContent(org.opensaml.xml.XMLObject,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void marshallElementContent(XMLObject xmlObject,
            Element domElement) throws MarshallingException {
        XSURI xsUri= (XSURI) xmlObject;
        String uri= xsUri.getValue();
        XMLHelper.appendTextContent(domElement, uri);
    }
}
