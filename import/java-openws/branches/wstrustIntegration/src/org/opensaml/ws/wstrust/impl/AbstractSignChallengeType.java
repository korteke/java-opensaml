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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.opensaml.ws.wstrust.Challenge;
import org.opensaml.ws.wstrust.SignChallengeType;
import org.opensaml.xml.AbstractExtensibleXMLObject;
import org.opensaml.xml.XMLObject;

/**
 * AbstractSignChallengeType
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
abstract class AbstractSignChallengeType extends AbstractExtensibleXMLObject
        implements SignChallengeType {

    /** {@link Challenge} child element */
    protected Challenge challenge_= null;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public AbstractSignChallengeType(String namespaceURI,
            String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.SignChallengeType#getChallenge()
     */
    public Challenge getChallenge() {
        return challenge_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.SignChallengeType#setChallenge(org.opensaml.ws.wstrust.Challenge)
     */
    public void setChallenge(Challenge challenge) {
        challenge_= prepareForAssignment(challenge_, challenge);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.impl.AbstractWSTrustObject#getOrderedChildren()
     */
    @Override
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children= new ArrayList<XMLObject>();
        if (challenge_ != null) {
            children.add(challenge_);
        }
        // xs:any
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }
        return Collections.unmodifiableList(children);
    }
}
