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
package org.opensaml.ws.wstrust.impl;


import org.opensaml.ws.wstrust.BinarySecret;
import org.opensaml.ws.wstrust.Entropy;
import org.opensaml.xml.AbstractExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.encryption.EncryptedKey;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * Unmarshaller for the &lt;wst:Entropy&gt; element.
 * 
 * @see Entropy
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class EntropyUnmarshaller extends
        AbstractExtensibleXMLObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public EntropyUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the &lt;wst:BinarySecret&gt; or the &lt;xenc:EncryptedKey&gt;
     * child elements.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        Entropy container= (Entropy) parentXMLObject;
        if (childXMLObject instanceof BinarySecret) {
            BinarySecret binarySecret= (BinarySecret) childXMLObject;
            container.setBinarySecret(binarySecret);
        }
        else if (childXMLObject instanceof EncryptedKey) {
            EncryptedKey encryptedKey= (EncryptedKey) childXMLObject;
            container.setEncryptedKey(encryptedKey);
        }
        else {
            // xs:any element
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
