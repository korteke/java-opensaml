/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.security.trust;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;

/**
 * Evaluate a token in sequence using a chain of subordinate trust engines.  If the token may be established as
 * trusted by any of the subordinate engines, the token is considered trusted. Otherwise it is considered
 * untrusted.
 * 
 * @param <TokenType> the token type this trust engine evaluates
 */
public class ChainingTrustEngine<TokenType> extends AbstractTrustEngine<TokenType> implements TrustEngine<TokenType> {
    
    /** Class logger. */
    private static Logger log = Logger.getLogger(ChainingTrustEngine.class);
    
    /** The chain of subordinate trust engines.  */
    private List<TrustEngine<TokenType>> engines;
    
    /** Constructor. */
    public ChainingTrustEngine() {
        engines = new ArrayList<TrustEngine<TokenType>>();
    }
    
    /**
     * Get the list of configured trust engines which constitute the trust evaluation chain.
     * 
     * @return the modifiable list of trust engines in the chain
     */
    public  List<TrustEngine<TokenType>> getChain() {
        return engines;
    }
    
    /** {@inheritDoc} */
    public boolean validate(TokenType token, CredentialCriteriaSet trustedCredentialCriteria) throws SecurityException {
        for (TrustEngine<TokenType> engine : engines) {
            if (engine.validate(token, trustedCredentialCriteria)) {
                if (log.isDebugEnabled()) {
                    log.debug("Token was trusted by chain member: " + engine.getClass().getName());
                }
                return true;
            }
        }
        return false;
    }

}
