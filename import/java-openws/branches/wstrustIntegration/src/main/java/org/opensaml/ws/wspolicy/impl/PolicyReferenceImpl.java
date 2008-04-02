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

package org.opensaml.ws.wspolicy.impl;

import org.opensaml.ws.wspolicy.PolicyReference;
import org.opensaml.xml.util.AttributeMap;

/**
 * PolicyReferenceImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class PolicyReferenceImpl extends AbstractWSPolicyObject implements PolicyReference {

    /** URI attribute value */
    private String uri_ = null;

    /** Digest attribute value */
    private String digest_ = null;

    /** DigestAlgorithm attribute value */
    private String digestAlgorithm_ = null;

    /** xs:anyAttribute attributes */
    private AttributeMap anyAttributes_;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public PolicyReferenceImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        anyAttributes_ = new AttributeMap(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wspolicy.PolicyReference#getDigest()
     */
    public String getDigest() {
        return digest_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wspolicy.PolicyReference#getDigestAlgorithm()
     */
    public String getDigestAlgorithm() {
        return digestAlgorithm_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wspolicy.PolicyReference#getURI()
     */
    public String getURI() {
        return uri_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wspolicy.PolicyReference#setDigest(java.lang.String)
     */
    public void setDigest(String digest) {
        digest_ = prepareForAssignment(digest_, digest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wspolicy.PolicyReference#setDigestAlgorithm(java.lang.String)
     */
    public void setDigestAlgorithm(String digestAlgorithm) {
        digestAlgorithm_ = prepareForAssignment(digestAlgorithm_, digestAlgorithm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wspolicy.PolicyReference#setURI(java.lang.String)
     */
    public void setURI(String uri) {
        uri_ = prepareForAssignment(uri_, uri);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.AttributeExtensibleXMLObject#getUnknownAttributes()
     */
    public AttributeMap getUnknownAttributes() {
        return anyAttributes_;
    }

}
