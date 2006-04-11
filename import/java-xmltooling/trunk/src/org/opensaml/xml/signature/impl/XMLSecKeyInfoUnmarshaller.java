/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.signature.impl;

import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.X509Data;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.KeyInfoBuilder;
import org.w3c.dom.Element;

/**
 * A marshaller for {@link org.opensaml.xml.signature.impl.XMLSecKeyInfoImpl} objects. This class, along with it's
 * respective builder and unmarshaller use the Apache XMLSec 1.3 APIs to perform signing and verification.
 */
public class XMLSecKeyInfoUnmarshaller implements Unmarshaller {

    /** Logger */
    private static final Logger LOG = Logger.getLogger(XMLSecKeyInfoUnmarshaller.class);

    /**
     * Constructor
     */
    public XMLSecKeyInfoUnmarshaller() {
        if (!Init.isInitialized()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Initializing XML security library");
            }
            Init.init();
        }
    }

    /**
     * {@inheritDoc}
     */
    public XMLObject unmarshall(Element element) throws UnmarshallingException {
        XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
        KeyInfoBuilder keyInfoBuilder = (KeyInfoBuilder) builderFactory.getBuilder(element);
        KeyInfo keyInfoObj = keyInfoBuilder.buildObject();

        try {
            org.apache.xml.security.keys.KeyInfo keyInfoElem = new org.apache.xml.security.keys.KeyInfo(element, "#");

            int numOfKeyNames = keyInfoElem.lengthKeyName();
            for (int i = 0; i < numOfKeyNames; i++) {
                keyInfoObj.getKeyNames().add(keyInfoElem.itemKeyName(i).getKeyName());
            }

            int numOfKeys = keyInfoElem.lengthKeyValue();
            for (int i = 0; i < numOfKeys; i++) {
                keyInfoObj.getKeys().add(keyInfoElem.itemKeyValue(i).getPublicKey());
            }

            int numOfX509Data = keyInfoElem.lengthX509Data();
            int numOfCerts;
            X509Data x509Data;
            for (int i = 0; i < numOfX509Data; i++) {
                x509Data = keyInfoElem.itemX509Data(i);
                numOfCerts = x509Data.lengthCertificate();
                for (int j = 0; j < numOfCerts; j++) {
                    keyInfoObj.getCertificates().add(x509Data.itemCertificate(j).getX509Certificate());
                }
            }

            return keyInfoObj;
        } catch (XMLSecurityException e) {
            throw new UnmarshallingException("Unable to build XMLSec KeyInfo object from KeyInfo XML element");
        }
    }
}
