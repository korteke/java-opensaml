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

package org.opensaml.core;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.testng.annotations.BeforeSuite;

/**
 * Base test class for code that needs OpenSAML initialized before any tests are run.
 */
public abstract class OpenSAMLInitBaseTestCase {
    
    /**
     *  Initialize OpenSAML.
     *  
     * @throws InitializationException 
     */
    @BeforeSuite(groups={"opensaml.init"})
    public void initOpenSAML() throws InitializationException {
        InitializationService.initialize();
    }

}
