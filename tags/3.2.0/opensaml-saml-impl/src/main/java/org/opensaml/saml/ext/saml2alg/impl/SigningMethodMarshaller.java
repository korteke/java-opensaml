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

package org.opensaml.saml.ext.saml2alg.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.w3c.dom.Element;

/**
 * SigningMethod marshaller.
 */
public class SigningMethodMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject xmlObject, Element domElement) throws MarshallingException {
        SigningMethod signingMethod = (SigningMethod) xmlObject;
        
        if (signingMethod.getAlgorithm() != null) {
            domElement.setAttributeNS(null, SigningMethod.ALGORITHM_ATTRIB_NAME, signingMethod.getAlgorithm());
        }
        
        if (signingMethod.getMinKeySize() != null) {
            domElement.setAttributeNS(null, SigningMethod.MIN_KEY_SIZE_ATTRIB_NAME, 
                    signingMethod.getMinKeySize().toString());
        }
        
        if (signingMethod.getMaxKeySize() != null) {
            domElement.setAttributeNS(null, SigningMethod.MAX_KEY_SIZE_ATTRIB_NAME, 
                    signingMethod.getMaxKeySize().toString());
        }
    }

}
