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
package org.opensaml.ws.wstrust;

import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.ElementExtensibleXMLObject;

/**
 * Abstract SignChallengeType contains a &lt;wst:Challenge&gt; element.
 * 
 * @see SignChallenge
 * @see SignChallengeResponse
 * @see Challenge
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public abstract interface SignChallengeType extends
        AttributeExtensibleXMLObject, ElementExtensibleXMLObject {

    /**
     * Returns the &lt;wst:Challenge&gt; child element.
     * 
     * @return the {@link Challenge} child element or <code>null</code>.
     */
    public Challenge getChallenge();

    /**
     * Sets the &lt;wst:Challenge&gt; child element.
     * 
     * @param challenge
     *            the {@link Challenge} child element to set.
     */
    public void setChallenge(Challenge challenge);

}
