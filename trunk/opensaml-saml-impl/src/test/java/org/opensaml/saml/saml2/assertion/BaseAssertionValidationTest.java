
package org.opensaml.saml.saml2.assertion;

import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.assertion.impl.AbstractSubjectConfirmationValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

public class BaseAssertionValidationTest extends XMLObjectBaseTestCase {
    
    public static final Long CLOCK_SKEW = 5*60*1000L;
    
    public static final String PRINCIPAL_NAME = "gollum";
    
    public static final String ISSUER = "https://idp.example.org";
    
    public static final String SUBJECT_CONFIRMATION_RECIPIENT = "https://sp.example.com";
    
    public static final String SUBJECT_CONFIRMATION_ADDRESS = "10.1.2.3";
    
    private Assertion assertion;
    
    protected Assertion getAssertion() {
        return assertion;
    }
    
    @BeforeMethod
    protected void setUpBasicAssertion() {
        assertion = SAML2ActionTestingSupport.buildAssertion();
        assertion.setIssueInstant(new DateTime());
        assertion.setIssuer(SAML2ActionTestingSupport.buildIssuer(ISSUER));
        assertion.setSubject(SAML2ActionTestingSupport.buildSubject(PRINCIPAL_NAME));
        assertion.setConditions(buildBasicConditions());
        
        SubjectConfirmation subjectConfirmation = buildXMLObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        // Default to bearer with basic valid confirmation data, but the test can change as appropriate
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        subjectConfirmation.setSubjectConfirmationData(buildBasicSubjectConfirmationData());
        assertion.getSubject().getSubjectConfirmations().add(subjectConfirmation);
    }
    
    protected Conditions buildBasicConditions() {
        Conditions conditions = buildXMLObject(Conditions.DEFAULT_ELEMENT_NAME);
        DateTime now = new DateTime();
        conditions.setNotBefore(now.minusMinutes(5));
        conditions.setNotOnOrAfter(now.plusMinutes(5));
        return conditions;
    }
    
    protected SubjectConfirmationData buildBasicSubjectConfirmationData() {
        return buildBasicSubjectConfirmationData(null);
    }
    
    protected SubjectConfirmationData buildBasicSubjectConfirmationData(QName type) {
       SubjectConfirmationData scd = null;
       if (type != null) {
           XMLObjectBuilder<SubjectConfirmationData> builder = getBuilder(type);
           scd = builder.buildObject(SubjectConfirmationData.DEFAULT_ELEMENT_NAME, type);
       }
       else {
           scd = buildXMLObject(SubjectConfirmationData.DEFAULT_ELEMENT_NAME); 
       }
       scd.setRecipient(SUBJECT_CONFIRMATION_RECIPIENT);
       scd.setAddress(SUBJECT_CONFIRMATION_ADDRESS);
       DateTime now = new DateTime();
       scd.setNotBefore(now.minusMinutes(5));
       scd.setNotOnOrAfter(now.plusMinutes(5));
       return scd;
    }
    
    protected Map<String,Object> buildBasicStaticParameters() {
        HashMap<String,Object> params = new HashMap<>();
        
        params.put(SAML20AssertionValidator.CLOCK_SKEW_PARAM, CLOCK_SKEW);
        
        params.put(AbstractSubjectConfirmationValidator.VALID_RECIPIENTS_PARAM, 
                Collections.singleton(SUBJECT_CONFIRMATION_RECIPIENT));
        try {
            params.put(AbstractSubjectConfirmationValidator.VALID_ADDRESSES_PARAM, 
                    Collections.singleton(InetAddress.getByName(SUBJECT_CONFIRMATION_ADDRESS)));
        } catch(UnknownHostException e) {
            Assert.fail("Invalid address: " + SUBJECT_CONFIRMATION_ADDRESS);
        }
        return params;
    }
    
    protected X509Certificate getCertificate(String name) throws CertificateException, URISyntaxException {
        File certFile = new File(this.getClass().getResource("/data/org/opensaml/saml/saml2/assertion/" + name).toURI());
        return X509Support.decodeCertificate(certFile);
    }
    
    protected PrivateKey getPrivateKey(String name) throws KeyException, URISyntaxException {
        File keyFile = new File(this.getClass().getResource("/data/org/opensaml/saml/saml2/assertion/" + name).toURI());
        return KeySupport.decodePrivateKey(keyFile, null);
    }
    
    protected Credential getSigningCredential(PublicKey publicKey, PrivateKey privateKey) {
        BasicCredential cred = CredentialSupport.getSimpleCredential(publicKey, privateKey);
        cred.setUsageType(UsageType.SIGNING);
        cred.setEntityId(ISSUER);
        return cred;
    }
    
    protected void signAssertion(Assertion assertion, Credential credential) throws SecurityException, MarshallingException, SignatureException {
        SignatureSigningParameters parameters = new SignatureSigningParameters();
        parameters.setSigningCredential(credential);
        parameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        parameters.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        parameters.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        SignatureSupport.signObject(assertion, parameters);
    }
    
}
