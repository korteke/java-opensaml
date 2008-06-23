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


import org.opensaml.ws.wstrust.Challenge;
import org.opensaml.ws.wstrust.SignChallenge;
import org.opensaml.ws.wstrust.SignChallengeResponse;
import org.opensaml.ws.wstrust.SignChallengeType;
import org.opensaml.xml.AbstractExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * Unmarshaller for the SignChallengeType element which contains a
 * &lt;wst:Challenge&gt; child element.
 * 
 * @see SignChallengeType
 * @see SignChallenge
 * @see SignChallengeResponse
 * 
 */
public abstract class AbstractSignChallengeTypeUnmarshaller extends
        AbstractExtensibleXMLObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public AbstractSignChallengeTypeUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the &lt;wst:Challenge&gt; child element.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        SignChallengeType signChallengeType= (SignChallengeType) parentXMLObject;
        if (childXMLObject instanceof Challenge) {
            Challenge challenge= (Challenge) childXMLObject;
            signChallengeType.setChallenge(challenge);
        }
        else {
            // xs:any
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
