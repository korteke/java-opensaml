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

package org.opensaml.ws.security;

import javax.servlet.ServletRequest;

import org.opensaml.xml.XMLObject;

/**
 * A factory for rules which set the policy context issuer to fixed, specified value.
 */
public class StaticIssuerRuleFactory implements SecurityPolicyRuleFactory<ServletRequest> {
    
    /** The issuer to set in the security policy context. */
    private String issuer;
    
    /** State of issuer authentication. */
    private Boolean issuerAuthenticated;
    
    /** Constructor. */
    public StaticIssuerRuleFactory() {
        issuerAuthenticated = null;
    }
    
    /**
     * Get the issuer.
     * 
     * @return Returns the issuer.
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Set the issuer. 
     * 
     * @param newIssuer The issuer to set.
     */
    public void setIssuer(String newIssuer) {
        this.issuer = newIssuer;
    }
    
    /**
     * Get the issuer authenticated state.
     * 
     * @return Returns the issuerAuthenticated.
     */
    public Boolean getIssuerAuthenticated() {
        return issuerAuthenticated;
    }

    /**
     * Set the issuer authenticated state.
     * 
     * @param newIssuerAuthenticated The issuerAuthenticated to set.
     */
    public void setIssuerAuthenticated(Boolean newIssuerAuthenticated) {
        this.issuerAuthenticated = newIssuerAuthenticated;
    }
    
    /** {@inheritDoc} */
    public SecurityPolicyRule<ServletRequest> createRuleInstance() {
        return new StaticIssuerRule(getIssuer(), getIssuerAuthenticated());
    }
    
    /**
     * A factory for rules which set the policy context issuer to fixed, specified value.
     */
    protected class StaticIssuerRule implements SecurityPolicyRule<ServletRequest> {
        
        /** The issuer to set in the security policy context. */
        private String issuer;
        
        /** State of issuer authentication. */
        private Boolean issuerAuthenticated;
        
        /**
         * Constructor.
         *
         * @param newIssuer the issuer to set in the context
         * @param newIssuerAuthenticated the issuer authenticated state to set
         */
        protected StaticIssuerRule(String newIssuer, Boolean newIssuerAuthenticated) {
            issuer = newIssuer;
            issuerAuthenticated = newIssuerAuthenticated;
        }
        
        /** {@inheritDoc} */
        public void evaluate(ServletRequest request, XMLObject message, SecurityPolicyContext context)
            throws SecurityPolicyException {
            
            context.setIssuer(issuer);
            
            if (issuerAuthenticated == Boolean.TRUE) {
                context.setIssuerAuthenticated(true);
            } else if (issuerAuthenticated == Boolean.FALSE) {
                context.setIssuerAuthenticated(false);
            }
            
        }
        
    }

}
