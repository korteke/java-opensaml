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
package org.opensaml.core.config;

import javax.xml.namespace.QName;

import org.opensaml.xml.XMLObjectProviderRegistry;

/**
 * An abstract base class for XMLObject provider initializers which takes care of the boilerplate, requiring
 * concrete subclasses to only supply the initializer impl to test along with the collection of QNames
 * to check.
 */
public abstract class XMLObjectProviderInitializerBaseTestCase extends InitializerBaseTestCase {
    
    /**
     * Test basic provider registration.
     * 
     * @throws InitializationException if there is an error during provider init
     */
    public void testProviderInit() throws InitializationException {
        XMLObjectProviderRegistry registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        assertNull("Registry was non-null", registry);
        
        Initializer initializer = getTestedInitializer();
        initializer.init();
        
        registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        assertNotNull("Registry was null", registry);
        
        for (QName providerName : getTestedProviders()) {
            assertNotNull("Builder  for provider '" + providerName + "'was null",
                    registry.getBuilderFactory().getBuilder(providerName));
            assertNotNull("Unmarshaller  for provider '" + providerName + "'was null",
                    registry.getUnmarshallerFactory().getUnmarshaller(providerName));
            assertNotNull("Marshaller  for provider '" + providerName + "'was null",
                    registry.getMarshallerFactory().getMarshaller(providerName));
        }
    }
    
    /**
     * Get the initializer impl to test.
     * 
     * @return the initializer impl instance
     */
    protected abstract Initializer getTestedInitializer();

    /**
     * Get the array of QNames to test from the XMLObjectProviderRegistry.
     * 
     * @return an array of XMLObject provider QNames
     */
    protected abstract QName[] getTestedProviders();
    
}
