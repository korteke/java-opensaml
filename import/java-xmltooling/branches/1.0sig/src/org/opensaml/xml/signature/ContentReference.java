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

import java.util.List;

import org.apache.xml.security.signature.XMLSignature;

/**
 * Interface for representing the references to the content that is digitally signed.
 */
public interface ContentReference {

    /**
     * Gets the list of transforms applied to the referenced content.
     * 
     * @return list of transforms applied to the referenced conten
     */
    public List<String> getTransforms(); 
    
    /**
     * Gets the digest algorithm applied to the transformed content.
     * 
     * @return the digest algorithm applied to the transformed content
     */
    public String getDigestAlgorithm();
    
    /**
     * Called by the signature marshaller to allow references to be added to the signature. 
     *
     * @param signature the signature object
     */
    public void createReference(XMLSignature signature);
}