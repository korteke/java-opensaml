package org.opensaml.xml.security.trust;

import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.credential.CredentialResolver;

/**
 * Evaluates the trustworthiness and validity of a token against 
 * implementation-specific requirements.
 *
 * @param <TokenType> the token type this trust engine evaluates
 */
public interface TrustEngine<TokenType> {

    /**
     * Gets the credential resolver used to recover trusted credentials that 
     * may be used to validate tokens.
     *
     * @return credential resolver used to recover trusted credentials 
     *         that may be used to validate tokens
     */
    public CredentialResolver getCredentialResolver();

    /**
     * Validates the token against the trusted credential information obtained using the trusted
     * credential resolver.
     *
     * @param token security token to validate
     * @param trustedCredentialCriteria criteria used to describe the trusted credential(s)
     *
     * @return true if the token is trusted and valid, false if not
     *
     * @throws SecurityException thrown if there is a problem validating the security token
     */
    public boolean validate(TokenType token, CredentialCriteriaSet trustedCredentialCriteria) 
           throws SecurityException;
}
