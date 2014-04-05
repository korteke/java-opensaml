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

import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.algorithm.BlockEncryptionAlgorithm;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

/**
 * Algorithm descriptor for block encryption algorithm: AES-192-GCM.
 */
public final class BlockEncryptionAES192GCM implements BlockEncryptionAlgorithm {

    /** {@inheritDoc} */
    @Nonnull public String getKey() {
        return JCAConstants.KEY_ALGO_AES;
    }

    /** {@inheritDoc} */
    @Nonnull public String getURI() {
        return EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM;
    }

    /** {@inheritDoc} */
    @Nonnull public AlgorithmType getType() {
        return AlgorithmType.BlockEncryption;
    }

    /** {@inheritDoc} */
    @Nonnull public String getJCAAlgorithmID() {
        return String.format("%s/%s/%s", getKey(), getCipherMode(), getPadding());
    }

    /** {@inheritDoc} */
    @Nonnull public Integer getKeyLength() {
        return 192;
    }

    /** {@inheritDoc} */
    @Nonnull public String getCipherMode() {
        return JCAConstants.CIPHER_MODE_GCM;
    }

    /** {@inheritDoc} */
    @Nonnull public String getPadding() {
        return JCAConstants.CIPHER_PADDING_NONE;
    }

}
