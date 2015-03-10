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

package org.opensaml.saml.saml1.profile;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/** Test for {@link AbstractSAML1NameIdentifierGenerator}. */
public class AbstractSAML1NameIdentifierGeneratorTest extends OpenSAMLInitBaseTestCase {

    private static final String NAME_QUALIFIER = "https://idp.example.org";
    
    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testNoFormat() throws ComponentInitializationException {
        final MockSAML1NameIdentifierGenerator mock = new MockSAML1NameIdentifierGenerator();
        mock.initialize();
    }
    
    @Test
    public void testFull() throws ComponentInitializationException, SAMLException {
        final MockSAML1NameIdentifierGenerator mock = new MockSAML1NameIdentifierGenerator();
        mock.setFormat(NameIdentifier.X509_SUBJECT);
        mock.setIdPNameQualifier(NAME_QUALIFIER);
        mock.initialize();
        
        final NameIdentifier nameId = mock.generate(new ProfileRequestContext(), mock.getFormat());
        Assert.assertNotNull(nameId);
        Assert.assertEquals(nameId.getValue(), "foo");
        Assert.assertEquals(nameId.getNameQualifier(), NAME_QUALIFIER);
    }

    @Test
    public void testOmitSet() throws ComponentInitializationException, SAMLException {
        final MockSAML1NameIdentifierGenerator mock = new MockSAML1NameIdentifierGenerator();
        mock.setFormat(NameIdentifier.X509_SUBJECT);
        mock.setIdPNameQualifier(NAME_QUALIFIER);
        mock.setOmitQualifiers(true);
        mock.initialize();
        
        final NameIdentifier nameId = mock.generate(new ProfileRequestContext(), mock.getFormat());
        Assert.assertNotNull(nameId);
        Assert.assertEquals(nameId.getValue(), "foo");
        Assert.assertNull(nameId.getNameQualifier());
    }

    @Test
    public void testOmitUnset() throws ComponentInitializationException, SAMLException {
        final MockSAML1NameIdentifierGenerator mock = new MockSAML1NameIdentifierGenerator();
        mock.setFormat(NameIdentifier.X509_SUBJECT);
        mock.setOmitQualifiers(true);
        mock.initialize();
        
        final NameIdentifier nameId = mock.generate(new ProfileRequestContext(), mock.getFormat());
        Assert.assertNotNull(nameId);
        Assert.assertEquals(nameId.getValue(), "foo");
        Assert.assertNull(nameId.getNameQualifier());
    }

    @Test
    public void testDefaultQualifier() throws ComponentInitializationException, SAMLException {
        final MockSAML1NameIdentifierGenerator mock = new MockSAML1NameIdentifierGenerator();
        mock.setFormat(NameIdentifier.X509_SUBJECT);
        mock.initialize();
        
        final NameIdentifier nameId = mock.generate(new ProfileRequestContext(), mock.getFormat());
        Assert.assertNotNull(nameId);
        Assert.assertEquals(nameId.getValue(), "foo");
        Assert.assertEquals(nameId.getNameQualifier(), NAME_QUALIFIER);
    }
    
    private class MockSAML1NameIdentifierGenerator extends AbstractSAML1NameIdentifierGenerator {

        public MockSAML1NameIdentifierGenerator() {
            setId("test");
            setDefaultIdPNameQualifierLookupStrategy(new Function<ProfileRequestContext,String>() {
                public String apply(ProfileRequestContext input) {
                    return NAME_QUALIFIER;
                }
            });
        }
        
        /** {@inheritDoc} */
        @Override
        protected String getIdentifier(ProfileRequestContext profileRequestContext) throws SAMLException {
            return "foo";
        }
    }
}