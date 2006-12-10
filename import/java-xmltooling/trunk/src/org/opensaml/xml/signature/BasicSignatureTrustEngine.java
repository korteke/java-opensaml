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

package org.opensaml.xml.signature;

import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.apache.log4j.Logger;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.InlineX509KeyInfoResolver;
import org.opensaml.xml.security.KeyInfoSource;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.X509KeyInfoResolver;
import org.opensaml.xml.security.X509Util;
import org.w3c.dom.Element;

/**
 * A signature validation and trust engine that checks the signature against keys embedded within KeyInfo elements but
 * does not perform full PKIX validation.
 */
public class BasicSignatureTrustEngine implements SignatureTrustEngine<X509KeyInfoResolver> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(BasicSignatureTrustEngine.class);
    
    /** Subject Alt Names types (DNS, URI) used during peer name matching. */
    private static Integer[] altNameTypes = {X509Util.DNS_ALT_NAME, X509Util.URI_ALT_NAME};

    /** KeyInfoSource to use if no other one is given. */
    private KeyInfoSource defaultKeyInfoSource;

    /** KeyResolver to use if none is given. */
    private X509KeyInfoResolver defaultKeyResolver;

    /** Constructor. */
    public BasicSignatureTrustEngine() {
        defaultKeyResolver = new InlineX509KeyInfoResolver();
    }

    /** {@inheritDoc} */
    public KeyInfoSource getDefaultKeyInfoSource() {
        return defaultKeyInfoSource;
    }

    /** {@inheritDoc} */
    public X509KeyInfoResolver getDefaultKeyResolver() {
        return defaultKeyResolver;
    }

    /** {@inheritDoc} */
    public void setDefaultKeyResolver(X509KeyInfoResolver keyResolver) {
        defaultKeyResolver = keyResolver;
    }

    /** {@inheritDoc} */
    public void setDefaultkeyInfoSource(KeyInfoSource keyInfo) {
        defaultKeyInfoSource = keyInfo;
    }

    /** {@inheritDoc} */
    public boolean validate(Signature token) throws SecurityException {
        return validate(token, defaultKeyInfoSource, defaultKeyResolver);
    }

    /** {@inheritDoc} */
    public boolean validate(Signature token, KeyInfoSource keyInfoSource, X509KeyInfoResolver keyResolver)
            throws SecurityException {
        if (log.isDebugEnabled()) {
            log.debug("Verify digital signature with against keying information from: " + keyInfoSource.getName());
        }

        XMLSignature signature = buildSignature(token);

        Iterator<KeyInfo> keyInfoItr = keyInfoSource.getKeyInfo();
        List<PublicKey> keys;
        while (keyInfoItr.hasNext()) {
            keys = getKeys(keyInfoItr.next(), keyInfoSource.getName(), keyResolver);
            if (validate(signature, keys)) {
                return true;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    public boolean validate(byte[] signature, byte[] content, String sigAlg, KeyInfoSource keyInfo,
            X509KeyInfoResolver keyResolver) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Constructs an {@link XMLSignature} from the given signature object.
     * 
     * @param signature the signature
     * 
     * @return the constructed XMLSignature
     * 
     * @throws SecurityException thrown if the given signature can not be marshalled or converted into an XMLSignature
     */
    protected XMLSignature buildSignature(Signature signature) throws SecurityException {
        if (log.isDebugEnabled()) {
            log.debug("Creating XMLSignature object");
        }
        try {
            Marshaller sigMarshaller = Configuration.getMarshallerFactory().getMarshaller(
                    Signature.DEFAULT_ELEMENT_NAME);
            Element signatureElem = sigMarshaller.marshall(signature);
            return new XMLSignature(signatureElem, "");
        } catch (MarshallingException e) {
            throw new SecurityException("Unable to marshall signature", e);
        } catch (XMLSecurityException e) {
            throw new SecurityException("Unable to create XMLSignature", e);
        }
    }

    /**
     * Gets the list of public keys from the given key info. The complete list of keys include any raw keys expresed at
     * KeyValue elements and the public key from any certificate who DN or subjectAltName matches the given peer name.
     * 
     * @param keyInfo the key info to extract the keys from
     * @param peerName the name of the peer that produced the signature
     * @param keyResolver the resolver used to extract keys and certificates from the key info
     * 
     * @return list of public keys given in the key info
     * 
     * @throws SecurityException thrown if the keys or certificate within the key info can not be resolved
     */
    protected List<PublicKey> getKeys(KeyInfo keyInfo, String peerName, X509KeyInfoResolver keyResolver)
            throws SecurityException {
        FastList<PublicKey> keys = new FastList<PublicKey>();

        try {
            keys.addAll(keyResolver.resolveKeys(keyInfo));
            
            Set<String> commonNames;
            for (X509Certificate cert : keyResolver.resolveCertificates(keyInfo)) {
                commonNames = new FastSet<String>();
                commonNames.addAll(X509Util.getCommonNames(cert.getSubjectX500Principal()));
                commonNames.addAll(X509Util.getAltNames(cert, altNameTypes));
                if(commonNames.contains(peerName)){
                    keys.add(cert.getPublicKey());
                }
            }
        } catch (KeyException e) {
            throw new SecurityException("Unable to parse raw key information", e);
        } catch (CertificateException e) {
            throw new SecurityException("Unable to parse ceritificate information", e);
        }

        return keys;
    }

    /**
     * Validates the given signature against a set of keys.
     * 
     * @param signature the signature to validate
     * @param keys the keys to use during validation
     * 
     * @return true if the signature validates with any of the given keys
     * 
     * @throws SecurityException thrown if there is a problem checking the signature against a key
     */
    protected boolean validate(XMLSignature signature, List<PublicKey> keys) throws SecurityException {
        try {
            for (PublicKey key : keys) {
                if (signature.checkSignatureValue(key)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Signature validated with public key");
                    }
                    return true;
                }
            }
        } catch (XMLSignatureException e) {
            throw new SecurityException("Unable to evaluate key against signature", e);
        }

        return false;
    }
}