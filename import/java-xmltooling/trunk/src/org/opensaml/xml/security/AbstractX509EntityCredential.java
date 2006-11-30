/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.security;

import java.security.cert.X509Certificate;
import java.util.List;

import javolution.util.FastList;

/**
 * Base class for {@link org.opensaml.security.X509EntityCredential} implementations.
 */
public abstract class AbstractX509EntityCredential extends AbstractEntityCredential implements X509EntityCredential {

    /** Public certificate of the entity */
    protected X509Certificate entityCertificate;
    
    /** Certificate chain for the entity, includes entity certificate */
    protected FastList<X509Certificate> certificateChain;

    /** {@inheritDoc}  */
    public X509Certificate getEntityCertificate() {
        return entityCertificate;
    }

    /** {@inheritDoc}  */
    public List<X509Certificate> getEntityCertificateChain() {
       return certificateChain.unmodifiable();
    }
}