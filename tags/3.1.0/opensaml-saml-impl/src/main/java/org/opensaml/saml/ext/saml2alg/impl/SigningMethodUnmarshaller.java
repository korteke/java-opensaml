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

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.w3c.dom.Attr;

/**
 * SigningMethod unmarshaller.
 */
public class SigningMethodUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull XMLObject parentXMLObject, @Nonnull XMLObject childXMLObject)
            throws UnmarshallingException {
        SigningMethod signingMethod = (SigningMethod) parentXMLObject;
        signingMethod.getUnknownXMLObjects().add(childXMLObject);
    }

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull XMLObject xmlObject, @Nonnull Attr attribute) 
            throws UnmarshallingException {
        SigningMethod signingMethod = (SigningMethod) xmlObject;
        
        if (attribute.getLocalName().equals(SigningMethod.ALGORITHM_ATTRIB_NAME)) {
            signingMethod.setAlgorithm(attribute.getValue());
        } else if (attribute.getLocalName().equals(SigningMethod.MIN_KEY_SIZE_ATTRIB_NAME)) {
            signingMethod.setMinKeySize(Integer.valueOf(attribute.getValue()));
        } else if (attribute.getLocalName().equals(SigningMethod.MAX_KEY_SIZE_ATTRIB_NAME)) {
            signingMethod.setMaxKeySize(Integer.valueOf(attribute.getValue()));
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}
