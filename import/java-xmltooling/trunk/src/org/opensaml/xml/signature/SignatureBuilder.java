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

import org.opensaml.xml.AbstractXMLObjectBuilder;
import org.opensaml.xml.XMLObject;

/**
 * Builder of {@link org.opensaml.xml.signature.Signature}s.
 */
public class SignatureBuilder extends AbstractXMLObjectBuilder<XMLObject>{

    /** Signing context used when creating a Signature */
    private SigningContext context;
    
    /**
     * Constructor
     */
    public SignatureBuilder(){
        
    }
    
    /**
     * Builds the Signature.
     * 
     * @throws IllegalStateException thrown if no signing context has been set
     */
    public Signature doBuildObject() throws IllegalStateException{
        if(context == null){
            throw new IllegalStateException("A non-null signing context must be set before a signature can be built");
        }
        Signature signature = new Signature(context);
        return signature;
    }
    
    public void resetState() {
        context = null;
    }

    /**
     * Gets the signing context to use when creating the Signature.
     * 
     * @return the signing context to use when creating the Signature
     */
    public SigningContext getSigningContext(){
        return context;
    }
    
    /**
     * Sets the signing context to use when creating the Signature.
     * 
     * @param newContext the signing context to use when creating the Signature
     */
    public void setSigningContext(SigningContext newContext){
        context = newContext;
    }
}