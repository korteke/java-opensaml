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

import javax.xml.namespace.QName;

import org.opensaml.xml.AttributeExtensibleXMLObject;

/**
 * The &lt;wsp:PolicyReference&gt; element.
 * 
 * @see "WS-Policy (http://schemas.xmlsoap.org/ws/2004/09/policy)"
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public interface PolicyReference extends AttributeExtensibleXMLObject, WSPolicyObject {

    /** Element local name. */
    public static final String ELEMENT_LOCAL_NAME = "PolicyReference";

    /** Default element name */
    public final static QName ELEMENT_NAME = new QName(WSPolicyConstants.WSP_NS, ELEMENT_LOCAL_NAME,
            WSPolicyConstants.WSP_PREFIX);

    /** The &lt;wsp:PolicyReference/@URI&gt; attribute local name */
    public static final String URI_ATTR_LOCAL_NAME = "URI";

    /** The &lt;wsp:PolicyReference/@URI&gt; attribute name */
    public static final QName URI_ATTR_NAME = new QName(null, URI_ATTR_LOCAL_NAME);

    /** The &lt;wsp:PolicyReference/@Digest&gt; attribute local name */
    public static final String DIGEST_ATTR_LOCAL_NAME = "Digest";

    /** The &lt;wsp:PolicyReference/@Digest&gt; attribute name */
    public static final QName DIGEST_ATTR_NAME = new QName(null, DIGEST_ATTR_LOCAL_NAME);

    /** The &lt;wsp:PolicyReference/@Digest&gt; attribute local name */
    public static final String DIGEST_ALGORITHM_ATTR_LOCAL_NAME = "DigestAlgorithm";

    /** The &lt;wsp:PolicyReference/@Digest&gt; attribute name */
    public static final QName DIGEST_ALGORITHM_ATTR_NAME = new QName(null,
            DIGEST_ALGORITHM_ATTR_LOCAL_NAME);

    /** The default &lt;wsp:PolicyReference/@DigestAlgorithm&gt; attribute value */
    public static final String DIGESTALGORITHM_SHA1EXC = WSPolicyConstants.WSP_NS + "/Sha1Exc";

    /**
     * Returns the &lt;wsp:PolicyReference/@URI&gt; attribute value.
     * 
     * @return the URI attribute value.
     */
    public String getURI();

    /**
     * Sets the &lt;wsp:PolicyReference/@URI&gt; attribute value.
     * 
     * @param uri the URI attribute value to set.
     */
    public void setURI(String uri);

    /**
     * Returns the &lt;wsp:PolicyReference/@Digest&gt; attribute URI value.
     * 
     * @return the Digest attribute URI value.
     */
    public String getDigest();

    /**
     * Sets the &lt;wsp:PolicyReference/@Digest&gt; attribute URI value.
     * 
     * @param digest the Digest attribute URI value to set.
     */
    public void setDigest(String digest);

    /**
     * Returns the &lt;wsp:PolicyReference/@DigestAlgoritm&gt; attribute Base64 binary value.
     * 
     * @return the DigestAlgoritm attribute Base64 binary value.
     */
    public String getDigestAlgorithm();

    /**
     * Sets the &lt;wsp:PolicyReference/@DigestAlgoritm&gt; attribute Base64 binary value.
     * 
     * @param digestAlgorithm the DigestAlgoritm attribute Base64 binary value to set.
     */
    public void setDigestAlgorithm(String digestAlgorithm);

}
