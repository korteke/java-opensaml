/*
 * Copyright 2011 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.saml.config;

import javax.xml.namespace.QName;

import org.opensaml.core.config.XMLObjectProviderInitializerBaseTestCase;
import org.opensaml.core.config.Initializer;
import org.opensaml.saml1.core.AuthenticationStatement;
import org.opensaml.saml1.core.RespondWith;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.ecp.RelayState;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.samlext.idpdisco.DiscoveryResponse;
import org.opensaml.samlext.saml1md.SourceID;
import org.opensaml.samlext.saml2delrestrict.Delegate;
import org.opensaml.samlext.saml2mdattr.EntityAttributes;
import org.opensaml.samlext.saml2mdquery.AttributeQueryDescriptorType;
import org.opensaml.samlext.saml2mdui.UIInfo;
import org.opensaml.samlext.samlpthrpty.RespondTo;
import org.opensaml.xml.encryption.EncryptedData;
import org.opensaml.xml.signature.Signature;

/**
 * Test XMLObject provider initializer for module "saml-impl".
 */
public class XMLObjectProviderInitializerTest extends XMLObjectProviderInitializerBaseTestCase {

    /** {@inheritDoc} */
    protected Initializer getTestedInitializer() {
        return new XMLObjectProviderInitializer();
    }

    /** {@inheritDoc} */
    protected QName[] getTestedProviders() {
        return new QName[] {
                AuthenticationStatement.DEFAULT_ELEMENT_NAME,
                SourceID.DEFAULT_ELEMENT_NAME,
                RespondWith.DEFAULT_ELEMENT_NAME,
                AuthnStatement.DEFAULT_ELEMENT_NAME,
                Delegate.DEFAULT_ELEMENT_NAME,
                RelayState.DEFAULT_ELEMENT_NAME,
                EntityAttributes.DEFAULT_ELEMENT_NAME,
                EntityDescriptor.DEFAULT_ELEMENT_NAME,
                DiscoveryResponse.DEFAULT_ELEMENT_NAME,
                AttributeQueryDescriptorType.TYPE_NAME,
                UIInfo.DEFAULT_ELEMENT_NAME,
                AuthnRequest.DEFAULT_ELEMENT_NAME,
                RespondTo.DEFAULT_ELEMENT_NAME,
                
        };
    }
    
}
