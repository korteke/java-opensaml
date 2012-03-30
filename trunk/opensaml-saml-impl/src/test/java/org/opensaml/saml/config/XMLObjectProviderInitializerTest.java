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

package org.opensaml.saml.config;

import javax.xml.namespace.QName;

import org.opensaml.core.config.Initializer;
import org.opensaml.core.xml.config.XMLObjectProviderInitializerBaseTestCase;
import org.opensaml.saml.ext.idpdisco.DiscoveryResponse;
import org.opensaml.saml.ext.saml1md.SourceID;
import org.opensaml.saml.ext.saml2delrestrict.Delegate;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.ext.saml2mdquery.AttributeQueryDescriptorType;
import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.opensaml.saml.ext.samlpthrpty.RespondTo;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.core.RespondWith;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.ecp.RelayState;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

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
