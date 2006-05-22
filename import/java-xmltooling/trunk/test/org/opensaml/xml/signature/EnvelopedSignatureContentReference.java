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

import org.apache.log4j.Logger;
import org.apache.xml.security.algorithms.MessageDigestAlgorithm;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;

/**
 * A simple content reference implementation based on reference IDs to elements that exist in the document to be signed.
 */
public class EnvelopedSignatureContentReference implements ContentReference {
    
    /** Logger */
    private static Logger log = Logger.getLogger(EnvelopedSignatureContentReference.class);

    /** Element reference ID */
    private String referenceID;

    /**
     * Constructor
     *
     * @param referenceID the reference ID of the element to be signed
     */
    public EnvelopedSignatureContentReference(String referenceID) {
        this.referenceID = referenceID;
    }

    /** {@inheritDoc} */
    public void createReference(XMLSignature signature) {
        try{
        Transforms dsigTransforms = new Transforms(signature.getDocument());
        dsigTransforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        dsigTransforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);

        signature.addDocument(referenceID, dsigTransforms, MessageDigestAlgorithm.ALGO_ID_DIGEST_SHA256);
        }catch(Exception e){
            log.error("Error while adding content reference", e);
        }
    }
}
