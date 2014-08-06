/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xmlsec.config;

import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An initializer which validates the Java Cryptographic Architecture environment is usable.
 * 
 * <p>
 * Validates that the set of security providers configured in the JVM supports required cryptographic capabilities,
 * for example for the XML Encryption and XML Signature specifications.
 * </p>
 * 
 * <p>
 * Depending on the requirements of the calling code, failure to fully support encryption and signature requirements
 * may or may not be significant.  A configuration property <code>opensaml.config.xmlsec.cryptoValidationIsFatal</code>
 * is supplied to allow the environment to determine whether an invalid result is fatal or not.  The default
 * value of this flag is <code>false</code>. If any case, a warning is logged.
 * </p>
 * 
 */
public class JavaCryptoValidationInitializer implements Initializer {
    
    /** Configuration property determining whether invalid result is fatal or not. */
    public static final String CONFIG_PROPERTY_FAIL_IS_FATAL = "opensaml.config.xmlsec.cryptoValidationIsFatal";
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(JavaCryptoValidationInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        boolean valid = true;
        
        // XML Encryption spec requires AES support (128 and 256).
        // Some JREs are known to ship with no JCEs that support
        // the ISO10126Padding padding scheme.
        
        String errorMsgAESPadding = "The JCE providers currently configured in the JVM do not support\n"
            + "required capabilities for XML Encryption, either the 'AES' cipher algorithm\n"
            + "or the 'ISO10126Padding' padding scheme\n";
        
        try {
            Cipher.getInstance("AES/CBC/ISO10126Padding");
        } catch (NoSuchAlgorithmException e) {
            // IBM JCE returns this as the top-level exception even for the unsupported padding case. :-(
            // Otherwise would be nice to make the error msg more specific.
            log.warn(errorMsgAESPadding);
            valid = false;
        } catch (NoSuchPaddingException e) {
            log.warn(errorMsgAESPadding);
            valid = false;
        }
        
        if (!valid) {
            Properties props = ConfigurationService.getConfigurationProperties(); 
            String isFatal = (props != null) ? props.getProperty(CONFIG_PROPERTY_FAIL_IS_FATAL, "false") : "false";
            if ("true".equalsIgnoreCase(isFatal) || "1".equals(isFatal)) {
                log.warn("Configuration indicates an invalid crypto configuration should be fatal");
                throw new InitializationException(
                        "A fatal error was encountered validating required crypto capabilities");
            }
        }

    }

}