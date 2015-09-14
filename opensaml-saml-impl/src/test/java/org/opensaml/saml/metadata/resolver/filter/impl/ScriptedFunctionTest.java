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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.script.ScriptException;

import net.shibboleth.ext.spring.resource.ResourceHelper;
import net.shibboleth.utilities.java.support.resource.Resource;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class ScriptedFunctionTest {
    
    static final String SCRIPT_7 = "s = new java.lang.String(\"String\");set = new java.util.HashSet();set.add(s);set";
    static final String SCRIPT_8 = "JavaString=Java.type(\"java.lang.String\"); JavaSet = Java.type(\"java.util.HashSet\");set = new JavaSet();set.add(new JavaString(\"String\"));set";
    static final String FILE_7 = "/data/org/opensaml/saml/metadata/resolver/filter/impl/script.js";
    static final String FILE_8 = "data/org/opensaml/saml/metadata/resolver/filter/impl/script8.js";
    
    private final SAMLObjectBuilder builder = new EntityDescriptorBuilder();
    
    private boolean isV8() {
        final String ver = System.getProperty("java.version");
        return ver.startsWith("1.8");
    }

    private String script() {
        if (isV8()) {
            return SCRIPT_8;
        }
        return SCRIPT_7;
    }
    
    private String file() {
        if (isV8()) {
            return FILE_8;
        }
        return FILE_7;
    }
    
    private XMLObject makeObject() {
        return builder.buildObject();
    }
    
    
    @Test public void inlineScript() throws ScriptException {
        
        final Set<String> s = ScriptedTrustedNamesFunction.inlineScript(script()).apply(makeObject());
        Assert.assertEquals(s.size(), 1);
        Assert.assertTrue(s.contains("String"));
   }
    
    
    @Test public void fileScript() throws ScriptException, IOException {
        final Resource r = ResourceHelper.of(new ClassPathResource(file()));
        final Set<String> result = ScriptedTrustedNamesFunction.resourceScript(r).apply(makeObject());

        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains("String"));
    }
    
    @Test public void customScript() throws ScriptException {
        
        final ScriptedTrustedNamesFunction what = ScriptedTrustedNamesFunction.inlineScript("custom;");
        what.setCustomObject(Collections.singleton("String"));
        
        final Set<String> s = what.apply(makeObject());
        Assert.assertEquals(s.size(), 1);
        Assert.assertTrue(s.contains("String"));
   }
}
