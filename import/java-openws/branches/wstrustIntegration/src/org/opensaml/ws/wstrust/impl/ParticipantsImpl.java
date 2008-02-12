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


import org.opensaml.ws.wstrust.Participant;
import org.opensaml.ws.wstrust.Participants;
import org.opensaml.ws.wstrust.Primary;
import org.opensaml.xml.AbstractElementExtensibleXMLObject;
import org.opensaml.xml.XMLObject;

/**
 * ParticipantsImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class ParticipantsImpl extends AbstractElementExtensibleXMLObject
        implements Participants {

    /** The {@link Primary} child element */
    private Primary primary_= null;

    /** The {@link Participant} child element */
    private Participant participant_= null;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public ParticipantsImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Participants#getParticipant()
     */
    public Participant getParticipant() {
        return participant_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Participants#getPrimary()
     */
    public Primary getPrimary() {
        return primary_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Participants#setParticipant(org.opensaml.ws.wstrust.Participant)
     */
    public void setParticipant(Participant participant) {
        participant_= prepareForAssignment(participant_, participant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Participants#setPrimary(org.opensaml.ws.wstrust.Primary)
     */
    public void setPrimary(Primary primary) {
        primary_= prepareForAssignment(primary_, primary);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.impl.AbstractElementExtensible#getOrderedChildren()
     */
    @Override
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children= new ArrayList<XMLObject>();
        if (primary_ != null) {
            children.add(primary_);
        }
        if (participant_ != null) {
            children.add(participant_);
        }
        // xs:any element
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }
        return Collections.unmodifiableList(children);
    }

}
