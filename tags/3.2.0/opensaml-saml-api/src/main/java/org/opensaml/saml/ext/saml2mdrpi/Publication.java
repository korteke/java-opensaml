/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
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

package org.opensaml.saml.ext.saml2mdrpi;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * * Representation of the <code>&lt;mdrpi:Publication&gt</code> element. <br/>
 * See <a
 * href="http://docs.oasis-open.org/security/saml/Post2.0/saml-metadata-rpi/v1.0/">http://docs.oasis-open.org/security
 * /saml/Post2.0/saml-metadata-rpi/v1.0/</a>
 */
public interface Publication extends SAMLObject {

    /** Name of the element inside the Extensions. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Publication";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20MDRPI_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20MDRPI_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "PublicationType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML20MDRPI_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20MDRPI_PREFIX);

    /** publisher attribute name. */
    public String PUBLISHER_ATTRIB_NAME = "publisher";

    /** creation attribute name. */
    public String CREATION_INSTANT_ATTRIB_NAME = "creationInstant";

    /** publication id attribute name. */
    public String PUBLICATION_ID_ATTRIB_NAME = "publicationId";

    /**
     * Get the publisher.
     * 
     * @return the publisher
     */
    public String getPublisher();

    /**
     * Set the publisher.
     * 
     * @param publisher the publisher
     */
    public void setPublisher(String publisher);

    /**
     * Get the creation instant.
     * 
     * @return the creation instant
     */
    public DateTime getCreationInstant();

    /**
     * Set the creation instant.
     * 
     * @param dateTime the instant
     */
    public void setCreationInstant(DateTime dateTime);

    /**
     * Get the publicationId.
     * 
     * @return the publicationId
     */
    public String getPublicationId();

    /**
     * Set the publicationId.
     * 
     * @param publicationId the publicationIdr
     */
    public void setPublicationId(String publicationId);

}
