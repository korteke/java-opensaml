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

import org.opensaml.xml.util.XMLConstants;


/**
 * Constants defined in or related to the XML Signature specification, version 20020112.
 */
public class SignatureConstants {
    
    // *********************************************************
    // Algorithm URI's 
    // *********************************************************
    
    /** Signature - Required DSAwithSHA1 (DSS). */
    public static final String ALGO_ID_SIGNATURE_DSA = XMLConstants.XMLSIG_NS + "dsa-sha1";

    /** Signature - Recommended RSAwithSHA1 (PKCS1). */
    public static final String ALGO_ID_SIGNATURE_RSA = XMLConstants.XMLSIG_NS + "rsa-sha1";
    
    /** Signature - Recommended RSAwithSHA1 (PKCS1). */
    public static final String ALGO_ID_SIGNATURE_RSA_SHA1 = ALGO_ID_SIGNATURE_RSA;
    
    /** MAC - Required HMAC-SHA1. */
    public static final String ALGO_ID_MAC_HMAC_SHA1 = XMLConstants.XMLSIG_NS + "hmac-sha1";

   /** Digest - Required SHA1. */
    public static final String ALGO_ID_DIGEST_SHA1 = XMLConstants.XMLSIG_NS + "sha1";
    
   /** Encoding - Required Base64. */
    public static final String ALGO_ID_ENCODING_BASE64 = XMLConstants.XMLSIG_NS + "base64";
    
    // *********************************************************
    // URI's representing types that may be dereferenced, such
    // as in RetrievalMethod/@Type
    // *********************************************************
    
    /** Type - KeyInfo DSAKeyValue. */
    public static final String TYPE_KEYINFO_DSA_KEYVALUE = XMLConstants.XMLSIG_NS + "DSAKeyValue";
    
    /** Type - KeyInfo RSAKeyValue. */
    public static final String TYPE_KEYINFO_RSA_KEYVALUE = XMLConstants.XMLSIG_NS + "RSAKeyValue";
    
    /** Type - KeyInfo X509Data. */
    public static final String TYPE_KEYINFO_X509DATA = XMLConstants.XMLSIG_NS + "X509Data";
    
    /** Type - KeyInfo PGPData. */
    public static final String TYPE_KEYINFO_PGPDATA = XMLConstants.XMLSIG_NS + "PGPData";
    
    /** Type - KeyInfo SPKIData. */
    public static final String TYPE_KEYINFO_SPKIDATA = XMLConstants.XMLSIG_NS + "SPKIData";
    
    /** Type - KeyInfo MgmtData. */
    public static final String TYPE_KEYINFO_MGMTDATA = XMLConstants.XMLSIG_NS + "MgmtData";
    
    /** Type - A binary (ASN.1 DER) X.509 Certificate. */
    public static final String TYPE_KEYINFO_RAW_X509CERT = XMLConstants.XMLSIG_NS + "rawX509Certificate";

    /** Type - Signature Object. */
    //public static final String TYPE_SIGNATURE_OBJECT = XMLConstants.XMLSIG_NS + "Object";

    /** Type - Signature Manifest. */
    //public static final String TYPE_SIGNATURE_MANIFEST = XMLConstants.XMLSIG_NS + "Manifest";

    /** Type - Signature SignatureProperties. */
    //public static final String TYPE_SIGNATURE_SIGNATURE_PROPERTIES = XMLConstants.XMLSIG_NS + "SignatureProperties";

    
    // *********************************************************
    // Canonicalization
    // *********************************************************
    
    /** Canonicalization - Inclusive WITHOUT comments. */
    public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
   
   /** Canonicalization - Inclusive WITH comments. */
    public static final String ALGO_ID_C14N_WITH_COMMENTS = ALGO_ID_C14N_OMIT_COMMENTS + "#WithComments";
   
   /** Canonicalization - Exclusive WITHOUT comments. */
    public static final String ALGO_ID_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
   
   /** Canonicalization - Exclusive WITH comments. */
    public static final String ALGO_ID_C14N_EXCL_WITH_COMMENTS = ALGO_ID_C14N_EXCL_OMIT_COMMENTS + "WithComments";

    
    // *********************************************************
    // Transforms
    // *********************************************************
    
   /** Transform - Required Enveloped Signature. */
    public static final String TRANSFORM_ENVELOPED_SIGNATURE = XMLConstants.XMLSIG_NS + "enveloped-signature";
    
    /** Transform - Required Inclusive c14n WITHOUT comments. */
    public static final String TRANSFORM_C14N_OMIT_COMMENTS = ALGO_ID_C14N_OMIT_COMMENTS;
    
    /** Transform - Recommended Inclusive c14n WITH comments. */
    public static final String TRANSFORM_C14N_WITH_COMMENTS = ALGO_ID_C14N_WITH_COMMENTS;
    
   /** Transform - Exclusive c14n WITHOUT comments. */
    public static final String TRANSFORM_C14N_EXCL_OMIT_COMMENTS = ALGO_ID_C14N_EXCL_OMIT_COMMENTS;
    
   /** Transform - Exclusive c14n WITH comments. */
    public static final String TRANSFORM_C14N_EXCL_WITH_COMMENTS = ALGO_ID_C14N_EXCL_WITH_COMMENTS;
    
   /** Transform - Optional XSLT. */
    public static final String TRANSFORM_XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";
    
   /** Transform - Recommended XPath. */
    public static final String TRANSFORM_XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
    
   /** Transform - Base64 Decode. */
    public static final String TRANSFORM_BASE64_DECODE = XMLConstants.XMLSIG_NS + "base64";
    
    /*
    public static final String TRANSFORM_XPOINTER = "http://www.w3.org/TR/2001/WD-xptr-20010108";
    public static final String TRANSFORM_XPATH2FILTER04 = "http://www.w3.org/2002/04/xmldsig-filter2";
    public static final String TRANSFORM_XPATH2FILTER = "http://www.w3.org/2002/06/xmldsig-filter2";
    */
    
    
    // *********************************************************
    // Some additional algorithm URI's from RFC 4051
    // *********************************************************
    /** Namespace URI defined by RFC 4051. */
    public static final String MORE_ALGO_NS = "http://www.w3.org/2001/04/xmldsig-more#";
    
    /** Signature - NOT Recommended RSAwithMD5. */
    public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = MORE_ALGO_NS + "rsa-md5";
    
    /** Signature - Optional RSAwithRIPEMD160. */
    public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 = MORE_ALGO_NS + "rsa-ripemd160";
    
    /** Signature - Optional RSAwithSHA256. */
    public static final String ALGO_ID_SIGNATURE_RSA_SHA256 = MORE_ALGO_NS + "rsa-sha256";
    
    /** Signature - Optional RSAwithSHA384. */
    public static final String ALGO_ID_SIGNATURE_RSA_SHA384 = MORE_ALGO_NS + "rsa-sha384";
    
    /** Signature - Optional RSAwithSHA512. */
    public static final String ALGO_ID_SIGNATURE_RSA_SHA512 = MORE_ALGO_NS + "rsa-sha512";

    /** HMAC - NOT Recommended HMAC-MD5. */
    public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = MORE_ALGO_NS + "hmac-md5";
    
    /** HMAC - Optional HMAC-RIPEMD160. */
    public static final String ALGO_ID_MAC_HMAC_RIPEMD160 = MORE_ALGO_NS + "hmac-ripemd160";
    
    /** HMAC - Optional HMAC-SHA256. */
    public static final String ALGO_ID_MAC_HMAC_SHA256 = MORE_ALGO_NS + "hmac-sha256";
    
    /** HMAC - Optional HMAC-SHA284. */
    public static final String ALGO_ID_MAC_HMAC_SHA384 = MORE_ALGO_NS + "hmac-sha384";
    
    /** HMAC - Optional HMAC-SHA512. */
    public static final String ALGO_ID_MAC_HMAC_SHA512 = MORE_ALGO_NS + "hmac-sha512";
    
    /**Signature - Optional ECDSAwithSHA1. */
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA1 = MORE_ALGO_NS + "ecdsa-sha1";
    


}
