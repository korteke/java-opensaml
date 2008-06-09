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


import org.opensaml.ws.wstrust.Participant;
import org.opensaml.ws.wstrust.Participants;
import org.opensaml.ws.wstrust.Primary;
import org.opensaml.xml.AbstractElementExtensibleXMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * ParticipantsUnmarshaller
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class ParticipantsUnmarshaller extends
        AbstractElementExtensibleXMLObjectUnmarshaller {

    /**
     * Default constructor.
     */
    public ParticipantsUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the &lt;wst:Primary&gt; and the &lt;wst:Participant&gt; child
     * elements.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        Participants participants= (Participants) parentXMLObject;
        if (childXMLObject instanceof Primary) {
            Primary primary= (Primary) childXMLObject;
            participants.setPrimary(primary);
        }
        else if (childXMLObject instanceof Participant) {
            Participant participant= (Participant) childXMLObject;
            participants.setParticipant(participant);
        }
        else {
            // xs:any element
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
