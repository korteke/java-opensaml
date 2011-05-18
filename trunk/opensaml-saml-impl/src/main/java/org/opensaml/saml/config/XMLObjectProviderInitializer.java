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

import org.opensaml.xml.AbstractXMLObjectProviderInitializer;

/**
 * XMLObject provider initializer for module "saml-impl".
 */
public class XMLObjectProviderInitializer extends AbstractXMLObjectProviderInitializer {
    
    /** Config resources. */
    private static String[] configs = {
        "/saml1-assertion-config.xml", 
        "/saml1-core-validation-config.xml", 
        "/saml1-metadata-config.xml", 
        "/saml1-protocol-config.xml",
        "/saml2-assertion-config.xml", 
        "/saml2-assertion-delegation-restriction-config.xml",    
        "/saml2-core-validation-config.xml", 
        "/saml2-ecp-config.xml",
        "/saml2-metadata-config.xml",
        "/saml2-metadata-idp-discovery-config.xml",
        "/saml2-metadata-query-config.xml", 
        "/saml2-metadata-validation-config.xml", 
        "/saml2-protocol-config.xml",
        "/saml2-protocol-thirdparty-config.xml",
        };

    /** {@inheritDoc} */
    protected String[] getConfigResources() {
        return configs;
    }

}
