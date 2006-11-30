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

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.opensaml.xml.signature.DSAKeyValue;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.KeyValue;
import org.opensaml.xml.signature.RSAKeyValue;

/**
 * A simple key resolver that extracts either public DSA or RSA key from the KeyInfo's KeyValue element. Key value
 * elements that can not be resolved are ignored.
 * 
 * If more than one key is present the "primary" key is simply the first one listed in the KeyInfo.
 */
public class InlineKeyResolver implements KeyResolver<PublicKey> {

    /** Class logger */
    private final static Logger log = Logger.getLogger(InlineKeyResolver.class);

    /** {@inheritDoc} */
    public PublicKey resolveKey(KeyInfo keyInfo) throws GeneralSecurityException {
        List<PublicKey> keys = resolveKeys(keyInfo);

        if (keys == null || keys.size() < 1) {
            return null;
        }

        return keys.get(0);
    }

    /** {@inheritDoc} */
    public List<PublicKey> resolveKeys(KeyInfo keyInfo) throws GeneralSecurityException {
        List<KeyValue> keyDescriptors = keyInfo.getKeyValues();

        if (keyDescriptors == null || keyDescriptors.size() < 1) {
            return null;
        }

        FastList<PublicKey> keys = new FastList<PublicKey>();
        for (KeyValue keyDescriptor : keyDescriptors) {
            if (keyDescriptor.getDSAKeyValue() != null) {
                keys.add(buildDSAKey(keyDescriptor.getDSAKeyValue()));
            } else if (keyDescriptor.getRSAKeyValue() != null) {
                keys.add(buildRSAKey(keyDescriptor.getRSAKeyValue()));
            }
        }

        return keys;
    }

    /**
     * Builds an DSA key from an DSAKeyValue element.
     * 
     * @param keyDescriptor the key descriptor
     * 
     * @return the key described
     * 
     * @throws GeneralSecurityException thrown if the key algorithm is not supported by the JCE or the key spec does not
     *             contain valid information
     */
    protected PublicKey buildDSAKey(DSAKeyValue keyDescriptor) throws GeneralSecurityException {
        String encodedG = keyDescriptor.getG().getValue();
        BigInteger gComponent = new BigInteger(Base64.decode(encodedG));

        String encodedP = keyDescriptor.getP().getValue();
        BigInteger pComponent = new BigInteger(Base64.decode(encodedP));

        String encodedQ = keyDescriptor.getQ().getValue();
        BigInteger qComponent = new BigInteger(Base64.decode(encodedQ));

        String encodedY = keyDescriptor.getY().getValue();
        BigInteger yComponent = new BigInteger(Base64.decode(encodedY));

        DSAPublicKeySpec keySpec = new DSAPublicKeySpec(yComponent, pComponent, qComponent, gComponent);
        return buildKey(keySpec, "DSA");
    }

    /**
     * Builds an RSA key from an RSAKeyValue element.
     * 
     * @param keyDescriptor the key descriptor
     * 
     * @return the key described
     * 
     * @throws GeneralSecurityException thrown if the key algorithm is not supported by the JCE or the key spec does not
     *             contain valid information
     */
    protected PublicKey buildRSAKey(RSAKeyValue keyDescriptor) throws GeneralSecurityException {
        String encodedModulus = keyDescriptor.getModulus().getValue();
        BigInteger modulus = new BigInteger(Base64.decode(encodedModulus));

        String encodedExponent = keyDescriptor.getExponent().getValue();
        BigInteger exponent = new BigInteger(Base64.decode(encodedExponent));

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
        return buildKey(keySpec, "RSA");
    }

    /**
     * Generates a public key from the given key spec.
     * 
     * @param keySpec specification for the key
     * @param keyAlgorithm key generation algorithm, only DSA and RSA supported
     * 
     * @return the generated key
     * 
     * @throws GeneralSecurityException thrown if the key algorithm is not supported by the JCE or the key spec does not
     *             contain valid information
     */
    protected PublicKey buildKey(KeySpec keySpec, String keyAlgorithm) throws GeneralSecurityException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error(keyAlgorithm + " algorithm is not supported by this VM", e);
            throw new GeneralSecurityException(keyAlgorithm + "RSA algorithm is not supported by the JCE", e);
        } catch (InvalidKeySpecException e) {
            log.error("Invalid key information", e);
            throw new GeneralSecurityException("Invalid key information", e);
        }
    }
}