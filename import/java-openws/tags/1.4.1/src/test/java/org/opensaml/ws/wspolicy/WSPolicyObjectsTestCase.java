/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensaml.ws.wspolicy;

import java.io.InputStream;

import javax.xml.namespace.QName;

import org.opensaml.ws.WSBaseTestCase;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLConfigurator;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.impl.XSAnyBuilder;
import org.opensaml.xml.schema.impl.XSAnyMarshaller;
import org.opensaml.xml.schema.impl.XSAnyUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WSPolicyObjectTestCase is the test case for the WS-policy objects.
 * 
 */
public class WSPolicyObjectsTestCase extends WSBaseTestCase {

    public Logger log= LoggerFactory.getLogger(WSPolicyObjectsTestCase.class);
    
    /**
     * QName for test wildcard element.
     */
    private static final QName TEST_ELEMENT_QNAME = new QName("urn:test:ns", "WildcardTest", "wct");

    /** {@inheritDoc} */
    protected void configureWS() throws Exception {
        // load ws-policy config
        InputStream is= getClass().getResourceAsStream("/wspolicy-config.xml");
        XMLConfigurator configurator= new XMLConfigurator();
        configurator.load(is);
        
        // register provider for Test supporting config
        Configuration.registerObjectProvider(TEST_ELEMENT_QNAME,  
                new XSAnyBuilder(), new XSAnyMarshaller(), new XSAnyUnmarshaller());
    }
    
    public void testAll() throws Exception {
        All all = buildXMLObject(All.ELEMENT_NAME);
        
        all.getPolicies().add((Policy) buildXMLObject(Policy.ELEMENT_NAME));
        all.getAlls().add((All) buildXMLObject(All.ELEMENT_NAME));
        all.getExactlyOnes().add((ExactlyOne) buildXMLObject(ExactlyOne.ELEMENT_NAME));
        all.getPolicyReferences().add((PolicyReference) buildXMLObject(PolicyReference.ELEMENT_NAME));
        
        all.getPolicyReferences().add((PolicyReference) buildXMLObject(PolicyReference.ELEMENT_NAME));
        all.getPolicies().add((Policy) buildXMLObject(Policy.ELEMENT_NAME));
        all.getAlls().add((All) buildXMLObject(All.ELEMENT_NAME));
        all.getAlls().add((All) buildXMLObject(All.ELEMENT_NAME));
        all.getExactlyOnes().add((ExactlyOne) buildXMLObject(ExactlyOne.ELEMENT_NAME));
        
        marshallAndUnmarshall(all);
    }
    
    public void testAppliesTo() throws Exception {
        AppliesTo appliesTo = buildXMLObject(AppliesTo.ELEMENT_NAME);
        
        QName wildAttribName = new QName("urn:test:ns:foo", "SomeAttribute", "test");
        appliesTo.getUnknownAttributes().put(wildAttribName, "foobar");
        QName wildAttribName2 = new QName("urn:test:ns:foo2", "AnotherAttribute", "test2");
        appliesTo.getUnknownAttributes().put(wildAttribName2, "another-foobar");
        
        XSAny testElement  = (XSAny) getBuilder(TEST_ELEMENT_QNAME).buildObject(TEST_ELEMENT_QNAME);
        testElement.setTextContent("urn:test:some-obscure-data");
        appliesTo.getUnknownXMLObjects().add(testElement);
        
        testElement  = (XSAny) getBuilder(TEST_ELEMENT_QNAME).buildObject(TEST_ELEMENT_QNAME);
        testElement.setTextContent("urn:test:some-other-obscure-data");
        appliesTo.getUnknownXMLObjects().add(testElement);
        
        marshallAndUnmarshall(appliesTo);
    }
    
    public void testExactlyOne() throws Exception {
        ExactlyOne exactlyOne = buildXMLObject(ExactlyOne.ELEMENT_NAME);
        
        exactlyOne.getPolicies().add((Policy) buildXMLObject(Policy.ELEMENT_NAME));
        exactlyOne.getAlls().add((All) buildXMLObject(All.ELEMENT_NAME));
        exactlyOne.getExactlyOnes().add((ExactlyOne) buildXMLObject(ExactlyOne.ELEMENT_NAME));
        exactlyOne.getPolicyReferences().add((PolicyReference) buildXMLObject(PolicyReference.ELEMENT_NAME));
        
        exactlyOne.getPolicyReferences().add((PolicyReference) buildXMLObject(PolicyReference.ELEMENT_NAME));
        exactlyOne.getPolicies().add((Policy) buildXMLObject(Policy.ELEMENT_NAME));
        exactlyOne.getAlls().add((All) buildXMLObject(All.ELEMENT_NAME));
        exactlyOne.getAlls().add((All) buildXMLObject(All.ELEMENT_NAME));
        exactlyOne.getExactlyOnes().add((ExactlyOne) buildXMLObject(ExactlyOne.ELEMENT_NAME));
        marshallAndUnmarshall(exactlyOne);
    }
    
    public void testPolicy() throws Exception {
        Policy policy = buildXMLObject(Policy.ELEMENT_NAME);
        
        policy.setName("urn:test:policy-name-foo");
        policy.setWSUId("abc123");
        QName wildAttribName = new QName("urn:test:ns:foo", "SomeAttribute", "test");
        policy.getUnknownAttributes().put(wildAttribName, "foobar");
        
        policy.getPolicies().add((Policy) buildXMLObject(Policy.ELEMENT_NAME));
        policy.getAlls().add((All) buildXMLObject(All.ELEMENT_NAME));
        policy.getExactlyOnes().add((ExactlyOne) buildXMLObject(ExactlyOne.ELEMENT_NAME));
        policy.getPolicyReferences().add((PolicyReference) buildXMLObject(PolicyReference.ELEMENT_NAME));
        
        policy.getPolicyReferences().add((PolicyReference) buildXMLObject(PolicyReference.ELEMENT_NAME));
        policy.getPolicies().add((Policy) buildXMLObject(Policy.ELEMENT_NAME));
        policy.getAlls().add((All) buildXMLObject(All.ELEMENT_NAME));
        policy.getAlls().add((All) buildXMLObject(All.ELEMENT_NAME));
        policy.getExactlyOnes().add((ExactlyOne) buildXMLObject(ExactlyOne.ELEMENT_NAME));
        
        marshallAndUnmarshall(policy);
    }
    
    public void testPolicyAttachment() throws Exception {
        PolicyAttachment policyAttachment = buildXMLObject(PolicyAttachment.ELEMENT_NAME);
        
        QName wildAttribName = new QName("urn:test:ns:foo", "SomeAttribute", "test");
        policyAttachment.getUnknownAttributes().put(wildAttribName, "foobar");
        QName wildAttribName2 = new QName("urn:test:ns:foo2", "AnotherAttribute", "test2");
        policyAttachment.getUnknownAttributes().put(wildAttribName2, "another-foobar");
        
        policyAttachment.setAppliesTo((AppliesTo) buildXMLObject(AppliesTo.ELEMENT_NAME));
        
        policyAttachment.getPolicies().add((Policy) buildXMLObject(Policy.ELEMENT_NAME));
        policyAttachment.getPolicies().add((Policy) buildXMLObject(Policy.ELEMENT_NAME));
        policyAttachment.getPolicyReferences().add((PolicyReference) buildXMLObject(PolicyReference.ELEMENT_NAME));
        policyAttachment.getPolicies().add((Policy) buildXMLObject(Policy.ELEMENT_NAME));
        policyAttachment.getPolicyReferences().add((PolicyReference) buildXMLObject(PolicyReference.ELEMENT_NAME));
        
        XSAny testElement  = (XSAny) getBuilder(TEST_ELEMENT_QNAME).buildObject(TEST_ELEMENT_QNAME);
        testElement.setTextContent("urn:test:some-obscure-data");
        policyAttachment.getUnknownXMLObjects().add(testElement);
        
        testElement  = (XSAny) getBuilder(TEST_ELEMENT_QNAME).buildObject(TEST_ELEMENT_QNAME);
        testElement.setTextContent("urn:test:some-other-obscure-data");
        policyAttachment.getUnknownXMLObjects().add(testElement);
        
        marshallAndUnmarshall(policyAttachment);
    }
    
    public void testPolicyReference() throws Exception {
        PolicyReference policyReference = buildXMLObject(PolicyReference.ELEMENT_NAME);
        
        policyReference.setURI("urn:test:policyRefFoo");
        policyReference.setDigest("digestdata");
        policyReference.setDigestAlgorithm(PolicyReference.DIGEST_ALGORITHM_SHA1EXC);
        
        QName wildAttribName = new QName("urn:test:ns:foo", "SomeAttribute", "test");
        policyReference.getUnknownAttributes().put(wildAttribName, "foobar");
        QName wildAttribName2 = new QName("urn:test:ns:foo2", "AnotherAttribute", "test2");
        policyReference.getUnknownAttributes().put(wildAttribName2, "another-foobar");
        
        marshallAndUnmarshall(policyReference);
    }

}
