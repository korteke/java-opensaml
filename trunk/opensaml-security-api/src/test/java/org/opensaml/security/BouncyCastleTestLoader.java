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

package org.opensaml.security;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Testing utility class whose purpose is to check and load the Bouncy Castle security provider for tests which require
 * advanced crypto capabilities, and unload it afterwards, if it wasn't loaded originally.  The goal of this is to preserve
 * the pre-test condition of whether BC was originally loaded or not, so we don't muck with the environment of JVM in which
 * the tests are running.  For example, the JVM may actually have been configured with BC deliberately, 
 * so we don't want to unload it by mistake.
 */
public class BouncyCastleTestLoader {
    
    private static final String PROVIDER_NAME = "BC";
    
    private boolean hadBCOriginally;
    
    public BouncyCastleTestLoader() {
        hadBCOriginally = (Security.getProvider(PROVIDER_NAME) != null);
    }
    
    public void load() {
        // Only load if isn't already loaded
        boolean haveBCNow = (Security.getProvider(PROVIDER_NAME) != null);
        if (!haveBCNow) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    
    public void unload() {
        // Only unload if wasn't loaded originally
        if (!hadBCOriginally) {
            Security.removeProvider("BC");
        }
    }

}
