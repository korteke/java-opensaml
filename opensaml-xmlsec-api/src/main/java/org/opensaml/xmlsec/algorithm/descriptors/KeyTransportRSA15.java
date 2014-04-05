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

package org.opensaml.xmlsec.algorithm.descriptors;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.algorithm.KeyTransportAlgorithm;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

/**
 * Algorithm descriptor for key transport algorithm: RSA v1.5.
 */
public final class KeyTransportRSA15 implements KeyTransportAlgorithm {

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getKey() {
        return JCAConstants.KEY_ALGO_RSA;
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getURI() {
        return EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15;
    }

    /** {@inheritDoc} */
    @Nonnull public AlgorithmType getType() {
        return AlgorithmType.KeyTransport;
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getJCAAlgorithmID() {
        return String.format("%s/%s/%s", getKey(), getCipherMode(), getPadding());
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getCipherMode() {
        return JCAConstants.CIPHER_MODE_ECB;
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getPadding() {
        return JCAConstants.CIPHER_PADDING_PKCS1;
    }

}
