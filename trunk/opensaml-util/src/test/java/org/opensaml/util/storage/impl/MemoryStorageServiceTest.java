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

package org.opensaml.util.storage.impl;

import java.util.Timer;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.util.storage.StorageService;
import org.opensaml.util.storage.StorageServiceTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test of {@link MemoryStorageService} implementation.
 */
public class MemoryStorageServiceTest extends StorageServiceTest {

    /** {@inheritDoc} */
    @Nonnull protected StorageService getStorageService() {
        MemoryStorageService ss = new MemoryStorageService();
        ss.setCleanupInterval(1);
        ss.setCleanupTaskTimer(new Timer());
        return ss;
    }
        
    @Test
    public void invalidConfig() {
        MemoryStorageService ss = new MemoryStorageService();
        ss.setCleanupInterval(1);
        
        try {
            ss.initialize();
            Assert.fail("Storage service should have failed to initialize");
        } catch (ComponentInitializationException e) {
            // expected
        }
        
        ss.destroy();
    }
    
    @Test
    public void validConfig() throws ComponentInitializationException {
        MemoryStorageService ss = new MemoryStorageService();
        
        ss.initialize();
        ss.destroy();
    }
    
}