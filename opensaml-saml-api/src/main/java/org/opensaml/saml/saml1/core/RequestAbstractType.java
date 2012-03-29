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

/**
 * 
 */
package org.opensaml.saml.saml1.core;

import java.util.List;

import org.joda.time.DateTime;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;

/**
 * This interface describes the base class for types derived from
 * <code> RequestAbstractType </code>
 */
public interface RequestAbstractType extends SignableSAMLObject {

    /** Name for the attribute which defines the Major Version (which must be "1". */
    public final static String MAJORVERSION_ATTRIB_NAME = "MajorVersion";

    /** Name for the attribute which defines the Minor Version. */
    public final static String MINORVERSION_ATTRIB_NAME = "MinorVersion";

    /** Name for the attribute which defines the Issue Instant. */
    public final static String ISSUEINSTANT_ATTRIB_NAME = "IssueInstant";

    /** Name for the attribute which defines the Issue Instant. */
    public final static String ID_ATTRIB_NAME = "RequestID";

    /** 
     * Gets the SAML version of this message. 
     * 
     * @return the SAML version of this message
     */
    public SAMLVersion getVersion();
    
    /**
     * Sets the SAML version of this message.
     * 
     * @param version SAML version of this message
     */
    public void setVersion(SAMLVersion version);
      
    /** Get the issue instant */
    public DateTime getIssueInstant();
 
    /** Get the ID */
    public String getID();
    
    /** Set the ID */
    public void setID(String id);
    
    /** Set the issue instant */
    public void setIssueInstant(DateTime date);
 
    /** Return the list of RespondWith elements */
    public List <RespondWith> getRespondWiths();
}