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

package org.opensaml.saml.common;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.AbstractXMLObjectBuilder;

/**
 * Base builder for {@link org.opensaml.saml.common.SAMLObject}s.
 * 
 * @param <SAMLObjectType> the SAML object type built
 */
public abstract class AbstractSAMLObjectBuilder<SAMLObjectType extends SAMLObject> extends
        AbstractXMLObjectBuilder<SAMLObjectType> implements SAMLObjectBuilder<SAMLObjectType> {

    /**
     * Builds a SAMLObject using the default name and namespace information provided SAML specifications.
     * 
     * @return built SAMLObject
     */
    @Nonnull public abstract SAMLObjectType buildObject();
}