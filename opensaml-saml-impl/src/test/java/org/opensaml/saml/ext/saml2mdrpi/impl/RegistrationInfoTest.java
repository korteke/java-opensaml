/*
 * Licensed to the University Corporation for Advanced Internet Development, Inc.
 * under one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache
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
package org.opensaml.saml.ext.saml2mdrpi.impl;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationInfo;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationPolicy;
import org.testng.Assert;



public class RegistrationInfoTest extends XMLObjectProviderBaseTestCase {

    private static String expectedAuthority = "https://www.aai.dfn.de";

    private static DateTime expectedRegistrationInstant = new DateTime(2010, 8, 11, 14, 59, 1, 2, ISOChronology.getInstanceUTC());

    private static String[] langs = {"en", "de",};
    private static String[] uris = {"grhttps://www.aai.dfn.de/en/join/","https://www.aai.dfn.de/teilnahme/",};

    /**
     * Constructor.
     */
    public RegistrationInfoTest() {
        super();
        singleElementFile = "/org/opensaml/saml/ext/saml2mdrpi/RegistrationInfo.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/ext/saml2mdrpi/RegistrationInfoOptionalAttr.xml";
        childElementsFile = "/org/opensaml/saml/ext/saml2mdrpi/RegistrationInfoChildren.xml";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        RegistrationInfo info = (RegistrationInfo) unmarshallElement(singleElementFile);
        Assert.assertEquals(info.getRegistrationAuthority(), expectedAuthority);
    }

    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesUnmarshall() {
        RegistrationInfo info = (RegistrationInfo) unmarshallElement(singleElementOptionalAttributesFile);
        Assert.assertEquals(info.getRegistrationAuthority(), expectedAuthority);
        Assert.assertEquals(info.getRegistrationInstant(), expectedRegistrationInstant);
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        RegistrationInfo info = (RegistrationInfo) buildXMLObject(RegistrationInfo.DEFAULT_ELEMENT_NAME);

        info.setRegistrationAuthority(expectedAuthority);

        assertXMLEquals(expectedDOM, info);
    }

    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesMarshall() {
        RegistrationInfo info = (RegistrationInfo) buildXMLObject(RegistrationInfo.DEFAULT_ELEMENT_NAME);

        info.setRegistrationAuthority(expectedAuthority);
        info.setRegistrationInstant(expectedRegistrationInstant);

        assertXMLEquals(expectedOptionalAttributesDOM, info);
    }
    public void testChildElementsUnmarshall() {
        RegistrationInfo info = (RegistrationInfo) unmarshallElement(childElementsFile);
        Assert.assertEquals(info.getRegistrationAuthority(), expectedAuthority);
        Assert.assertEquals(info.getRegistrationInstant(), expectedRegistrationInstant);
        RegistrationPolicy policy = info.getRegistrationPolicies().get(0);
        Assert.assertEquals(policy.getXMLLang(), langs[0]);
        Assert.assertEquals(policy.getValue(), uris[0]);
        policy = info.getRegistrationPolicies().get(1);
        Assert.assertEquals(policy.getXMLLang(), langs[1]);
        Assert.assertEquals(policy.getValue(), uris[1]);
    }

    public void testChildElementsMarshall() {
        RegistrationInfo info = (RegistrationInfo) buildXMLObject(RegistrationInfo.DEFAULT_ELEMENT_NAME);
        info.setRegistrationAuthority(expectedAuthority);
        info.setRegistrationInstant(expectedRegistrationInstant);

        for (int i = 0; i < 2; i++) {

            RegistrationPolicy policy = (RegistrationPolicy) buildXMLObject(RegistrationPolicy.DEFAULT_ELEMENT_NAME);
            policy.setValue(uris[i]);
            policy.setXMLLang(langs[i]);
            info.getRegistrationPolicies().add(policy);
        }
        assertXMLEquals(expectedChildElementsDOM, info);
    }
}
