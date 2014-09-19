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

package org.opensaml.xmlsec.encryption.support;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RSAOAEPParametersTest {
    
    @Test
    public void testConstructor() {
        RSAOAEPParameters params;
        
        params = new RSAOAEPParameters();
        Assert.assertNull(params.getDigestMethod());
        Assert.assertNull(params.getMaskGenerationFunction());
        Assert.assertNull(params.getOAEPParams());
        
        params = new RSAOAEPParameters("  digest  ", "  MGF  ", " OAEPParams    ");
        Assert.assertEquals(params.getDigestMethod(), "digest");
        Assert.assertEquals(params.getMaskGenerationFunction(), "MGF");
        Assert.assertEquals(params.getOAEPParams(), "OAEPParams");
    }
    
    @Test
    public void testGettersAndSetters() {
        RSAOAEPParameters params;
        
        params = new RSAOAEPParameters();
        
        params.setDigestMethod("  digest   ");
        params.setMaskGenerationFunction("    MGF    ");
        params.setOAEPparams("    OAEPParams     ");
        
        Assert.assertEquals(params.getDigestMethod(), "digest");
        Assert.assertEquals(params.getMaskGenerationFunction(), "MGF");
        Assert.assertEquals(params.getOAEPParams(), "OAEPParams");
    }
    
    @Test
    public void testIsEmpty() {
        RSAOAEPParameters params;
        
        params = new RSAOAEPParameters();
        Assert.assertTrue(params.isEmpty());
        
        params = new RSAOAEPParameters(null, null, null);
        Assert.assertTrue(params.isEmpty());
        
        params = new RSAOAEPParameters("digest", null, null);
        Assert.assertFalse(params.isEmpty());
        
        params = new RSAOAEPParameters(null, "MGF", null);
        Assert.assertFalse(params.isEmpty());
        
        params = new RSAOAEPParameters(null, null, "OAEPParams");
        Assert.assertFalse(params.isEmpty());
    }
    
    @Test
    public void testIsComplete() {
        RSAOAEPParameters params;
        
        params = new RSAOAEPParameters();
        Assert.assertFalse(params.isComplete());
        
        params = new RSAOAEPParameters(null, null, null);
        Assert.assertFalse(params.isComplete());
        
        params = new RSAOAEPParameters("digest", null, null);
        Assert.assertFalse(params.isComplete());
        
        params = new RSAOAEPParameters("digest", "MGF", null);
        Assert.assertFalse(params.isComplete());
        
        params = new RSAOAEPParameters("digest", null, "OAEPParams");
        Assert.assertFalse(params.isComplete());
        
        params = new RSAOAEPParameters(null, "MGF", null);
        Assert.assertFalse(params.isComplete());
        
        params = new RSAOAEPParameters(null, "MGF", "OAEPParams");
        Assert.assertFalse(params.isComplete());
        
        params = new RSAOAEPParameters(null, null, "OAEPParams");
        Assert.assertFalse(params.isComplete());
        
        params = new RSAOAEPParameters("digest", "MGF", "OAEPParams");
        Assert.assertTrue(params.isComplete());
    }

}
