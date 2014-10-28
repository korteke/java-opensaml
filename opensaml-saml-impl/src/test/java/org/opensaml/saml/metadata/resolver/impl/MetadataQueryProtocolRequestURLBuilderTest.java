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

package org.opensaml.saml.metadata.resolver.impl;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MetadataQueryProtocolRequestURLBuilderTest {
    
    private MetadataQueryProtocolRequestURLBuilder function;
    
    @Test
    public void testWithoutTrailingSlash() {
        function = new MetadataQueryProtocolRequestURLBuilder("http://metadata.example.org/service");
        
        Assert.assertEquals(function.apply("http://example.org/idp"), "http://metadata.example.org/service/entities/http%3A%2F%2Fexample.org%2Fidp");
    }

    @Test
    public void testWithTrailingSlash() {
        function = new MetadataQueryProtocolRequestURLBuilder("http://metadata.example.org/service/");
        
        Assert.assertEquals(function.apply("http://example.org/idp"), "http://metadata.example.org/service/entities/http%3A%2F%2Fexample.org%2Fidp");
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testNullEntityID() {
        function = new MetadataQueryProtocolRequestURLBuilder("http://metadata.example.org/service/");
        function.apply(null);
    }
    

}
