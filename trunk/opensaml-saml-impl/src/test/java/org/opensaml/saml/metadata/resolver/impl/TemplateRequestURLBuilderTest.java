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

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.apache.velocity.app.VelocityEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;


public class TemplateRequestURLBuilderTest {
    
    private VelocityEngine engine;
    
    private TemplateRequestURLBuilder function;
    
    @BeforeClass
    public void setUp() {
        engine = net.shibboleth.utilities.java.support.velocity.VelocityEngine.newVelocityEngine();
    }
    
    @Test
    public void testEncodedQueryParam() {
        function = new TemplateRequestURLBuilder(engine, "http://metadata.example.org/?entity=${entityID}", true);
        
        Assert.assertEquals(function.apply("http://example.org/idp"), "http://metadata.example.org/?entity=http%3A%2F%2Fexample.org%2Fidp");
    }
    
    @Test
    public void testMDQStyle() {
        function = new TemplateRequestURLBuilder(engine, "http://metadata.example.org/entities/${entityID}", true);
        
        Assert.assertEquals(function.apply("http://example.org/idp"), "http://metadata.example.org/entities/http%3A%2F%2Fexample.org%2Fidp");
    }

    @Test
    public void testWellKnownLocationStyle() {
        function = new TemplateRequestURLBuilder(engine, "${entityID}", false);
        
        Assert.assertEquals(function.apply("http://example.org/idp"), "http://example.org/idp");
    }
    
    @Test
    public void testTransformer() {
        Function<String,String> transformer = new Function<String, String>() {
            @Nullable public String apply(@Nullable String input) {
                return input.toUpperCase();
            }
        };
        
        function = new TemplateRequestURLBuilder(engine, "${entityID}", false, transformer);
        
        Assert.assertEquals(function.apply("http://example.org/idp"), "HTTP://EXAMPLE.ORG/IDP");
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testNullEntityID() {
        function = new TemplateRequestURLBuilder(engine, "http://metadata.example.org/?entity=${entityID}", true);
        function.apply(null);
    }


}
