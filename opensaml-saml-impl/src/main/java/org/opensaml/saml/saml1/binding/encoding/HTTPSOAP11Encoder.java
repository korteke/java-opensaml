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

package org.opensaml.saml.saml1.binding.encoding;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML 1.X HTTP SOAP 1.1 binding message encoder.
 */
public class HTTPSOAP11Encoder extends org.opensaml.soap.soap11.encoder.http.HTTPSOAP11Encoder<SAMLObject> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPSOAP11Encoder.class);

    /** Constructor. */
    public HTTPSOAP11Encoder() {
        super();
    }

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML1_SOAP11_BINDING_URI;
    }
    
    /** {@inheritDoc} */
    protected String getSOAPAction() {
        return "http://www.oasis-open.org/committees/security";
    }

}