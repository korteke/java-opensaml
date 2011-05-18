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

package org.opensaml.xacml.profile.saml.config;

import javax.xml.namespace.QName;

import org.opensaml.core.config.Initializer;
import org.opensaml.core.config.XMLObjectProviderInitializerBaseTestCase;
import org.opensaml.xacml.profile.saml.XACMLPolicyStatementType;

/**
 * Test XMLObject provider initializer for module "xacml-saml-impl".
 */
public class XMLObjectProviderInitializerTest extends XMLObjectProviderInitializerBaseTestCase {

    /** {@inheritDoc} */
    protected Initializer getTestedInitializer() {
        return new XMLObjectProviderInitializer();
    }

    /** {@inheritDoc} */
    protected QName[] getTestedProviders() {
        return new QName[] {
                XACMLPolicyStatementType.DEFAULT_ELEMENT_NAME_XACML10,
                XACMLPolicyStatementType.DEFAULT_ELEMENT_NAME_XACML11,
                XACMLPolicyStatementType.DEFAULT_ELEMENT_NAME_XACML20,
                XACMLPolicyStatementType.DEFAULT_ELEMENT_NAME_XACML30,
        };
    }
    
}
