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

package org.opensaml.core.xml.config;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.core.config.InitializerBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;

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
    @Test
    public void testProviderInit() throws InitializationException {
        XMLObjectProviderRegistry registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        AssertJUnit.assertNull("Registry was non-null", registry);
        
        Initializer initializer = getTestedInitializer();
        initializer.init();
        
        registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        AssertJUnit.assertNotNull("Registry was null", registry);
        
        for (QName providerName : getTestedProviders()) {
            AssertJUnit.assertNotNull("Builder  for provider '" + providerName + "'was null",
                    registry.getBuilderFactory().getBuilder(providerName));
            AssertJUnit.assertNotNull("Unmarshaller  for provider '" + providerName + "'was null",
                    registry.getUnmarshallerFactory().getUnmarshaller(providerName));
            AssertJUnit.assertNotNull("Marshaller  for provider '" + providerName + "'was null",
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
