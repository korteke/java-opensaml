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

package org.opensaml.ws.security;

import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.security.x509.X509Credential;

/**
 * An adapter that exposes the X.509 certificates contained in the HTTP request attribute as an
 * {@link X509Credentiall}.
 */
public class HttpRequestX509CredentialAdapter extends BasicX509Credential implements X509Credential {

    /** HTTP header to pull certificate info from. */
    public static final String X509_CERT_HTTP_HEADER = "javax.servlet.request.X509Certificate";

    /**
     * Constructor.
     *
     * @param httpRequest the HTTP request
     */
    public HttpRequestX509CredentialAdapter(HttpServletRequest httpRequest) {
        X509Certificate[] chain = (X509Certificate[]) httpRequest.getAttribute(X509_CERT_HTTP_HEADER);
        if (chain == null | chain.length == 0) {
            throw new IllegalArgumentException("HTTP Request does not contain X.509 certificates in header "
                    + X509_CERT_HTTP_HEADER);
        }

        setEntityCertificate(chain[0]);
        setEntityCertificateChain(Arrays.asList(chain));
        setUsageType(UsageType.SIGNING);
    }
}