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
package org.opensaml.saml.ext.saml2mdrpi.impl;

import org.joda.time.format.ISODateTimeFormat;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationInfo;
import org.w3c.dom.Element;


/**
 * A marshaller for {@link RegistrationInfo}.
 */
public class RegistrationInfoMarshaller extends AbstractSAMLObjectMarshaller {
    /** {@inheritDoc} */
    protected void marshallAttributes(XMLObject samlObject, Element domElement) throws MarshallingException {
        RegistrationInfo info = (RegistrationInfo) samlObject;

        if (info.getRegistrationAuthority() != null) {
            domElement.setAttributeNS(null, RegistrationInfo.REGISTRATION_AUTHORITY_ATTRIB_NAME,
                    info.getRegistrationAuthority());
        }

        if (info.getRegistrationInstant() != null) {
            String registrationInstant = ISODateTimeFormat.dateTime().print(info.getRegistrationInstant());
            domElement.setAttributeNS(null, RegistrationInfo.REGISTRATION_INSTANT_ATTRIB_NAME, registrationInstant);
        }
    }
}
