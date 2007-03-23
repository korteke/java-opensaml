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

package org.opensaml.ws.soap.client.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.opensaml.ws.MessageSource;
import org.opensaml.ws.soap.client.SOAPTransportException;
import org.opensaml.ws.util.URLBuilder;
import org.opensaml.xml.security.trust.TrustEngine;
import org.opensaml.xml.security.x509.X509Credential;

/**
 * An {@link HTTPSOAPTransport} based on the Jakarta Commons HTTPClient library.
 * 
 * TODO consider making this support connection pooling to help with the cost of SSL/TLS operations.
 */
public class HTTPClientSOAPTransport extends AbstractHTTPSOAPTransport {

    /** Types of HTTP methods that may be used to send the request. */
    public static enum HTTP_REQUEST_METHOD {
        GET, POST
    };

    /** HTTP method to use to send the request. */
    private HTTP_REQUEST_METHOD requestMethod;

    /**
     * Constructor.
     * 
     * @param method HTTP method used to send the request
     */
    public HTTPClientSOAPTransport(HTTP_REQUEST_METHOD method) {
        requestMethod = method;
    }

    /** {@inheritDoc} */
    public boolean supportsConfidential() {
        return true;
    }

    /** {@inheritDoc} */
    public MessageSource send(String peerURL, InputStream message) throws SOAPTransportException {
        URLBuilder endpoint = new URLBuilder(peerURL);

//        BasicHTTPMessageSource response;
//        if (requestMethod == HTTP_REQUEST_METHOD.GET) {
//            response = sendByGet(endpoint);
//        } else {
//            response = sendByPost(endpoint, message);
//        }
//
//        if (getPeerConnectionAuthenticatingTrustEngine() != null) {
//            response.setPeerAuthenticated(true);
//        }
//        return response;
        
        return null;
    }

//    /**
//     * Gets the HTTP client used to make the request.
//     * 
//     * @param endpoint endpoint to send the message to
//     * 
//     * @return HTTP client used to make the request
//     */
//    protected HttpClient getHttpClient(URLBuilder endpoint) {
//        HttpClient httpClient = new HttpClient();
//
//        HostConfiguration hostConfig = new HostConfiguration();
//        if (endpoint.getScheme().equalsIgnoreCase("https")) {
//            ProtocolSocketFactory socketFactory = new TLSSocketFactory(getConnectionAuthenticationCredential(),
//                    getPeerConnectionAuthenticatingTrustEngine());
//            Protocol https = new Protocol("https", socketFactory, endpoint.getPort());
//            hostConfig = new HostConfiguration();
//            hostConfig.setHost(endpoint.getHost(), endpoint.getPort(), https);
//        } else {
//            hostConfig.setHost(endpoint.getHost(), endpoint.getPort());
//        }
//
//        if (getEntityAuthenticationCredential() != null) {
//            httpClient.getParams().setAuthenticationPreemptive(true);
//            AuthScope authnScope = new AuthScope(endpoint.getHost(), endpoint.getPort());
//            httpClient.getState().setCredentials(authnScope, getCredentials());
//        }
//
//        httpClient.setHostConfiguration(hostConfig);
//        return httpClient;
//    }
//
//    /**
//     * Translates the transport implementation agnostic entity credentials into HTTPClient credentials.
//     * 
//     * @return entity authentication credentials
//     */
//    protected Credentials getCredentials() {
//        if (getEntityAuthenticationScheme() == AuthenticationScheme.Basic
//                || getEntityAuthenticationScheme() == AuthenticationScheme.Digest) {
//            UsernamePasswordCredential authnCredential = (UsernamePasswordCredential) getEntityAuthenticationCredential();
//            return new UsernamePasswordCredentials(authnCredential.getUsername(), authnCredential.getPassword());
//        } else if (getEntityAuthenticationScheme() == AuthenticationScheme.NTLM) {
//            NTLMCredential authnCredential = (NTLMCredential) getEntityAuthenticationCredential();
//            return new NTCredentials(authnCredential.getUsername(), authnCredential.getPassword(), authnCredential
//                    .getHost(), authnCredential.getDomain());
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * Sends a request to the given endpoint by HTTP GET.
//     * 
//     * @param endpoint endpoint to send the request to
//     * 
//     * @return the response to the request
//     * 
//     * @throws SOAPTransportException Thrown if there is a problem sending the request or receiving the response, for
//     *             example, if TLS or entity credentials were incorrect of the server is offline
//     */
//    protected BasicHTTPMessageSource sendByGet(URLBuilder endpoint) throws SOAPTransportException {
//        HttpClient httpClient = getHttpClient(endpoint);
//        GetMethod httpMethod = new GetMethod(endpoint.buildURL());
//
//        try {
//            httpClient.executeMethod(httpMethod);
//            return buildMessageSourceFromResponse(httpMethod);
//        } catch (IOException e) {
//            throw new SOAPTransportException("Unable to send GET request to " + endpoint.getHost() + ":"
//                    + endpoint.getPort(), e);
//        }
//    }
//
//    /**
//     * Sends a request to the given endpoint by HTTP POST.
//     * 
//     * @param endpoint endpoint to send the request to
//     * @param message message to send
//     * 
//     * @return the response to the request
//     * 
//     * @throws SOAPTransportException Thrown if there is a problem sending the request or receiving the response, for
//     *             example, if TLS or entity credentials were incorrect of the server is offline
//     */
//    protected BasicHTTPMessageSource sendByPost(URLBuilder endpoint, InputStream message) throws SOAPTransportException {
//        HttpClient httpClient = getHttpClient(endpoint);
//        PostMethod httpMethod = new PostMethod(endpoint.buildURL());
//
//        httpMethod.setRequestEntity(new InputStreamRequestEntity(message));
//        try {
//            httpClient.executeMethod(httpMethod);
//            return buildMessageSourceFromResponse(httpMethod);
//        } catch (IOException e) {
//            throw new SOAPTransportException("Unable to send GET request to " + endpoint.getHost() + ":"
//                    + endpoint.getPort(), e);
//        }
//    }
//
//    protected BasicHTTPMessageSource buildMessageSourceFromResponse(HttpMethod httpMethod) {
//        BasicHTTPMessageSource response = new BasicHTTPMessageSource();
//    }
//
//    /**
//     * A factory used to create sockets that validate a server's X509 information and optionally 
//     * authenticate this transport using X509 credentials.
//     */
//    protected class TLSSocketFactory implements SecureProtocolSocketFactory {
//        
//        /**
//         * Constructor.
//         *
//         * @param authenticationCredential credentials to use to authenticate to the peer, may be null
//         * @param peerAuthenticator trust engine used to evaluate peer's credentials, may not be null
//         */
//        public TLSSocketFactory(X509Credential authenticationCredential,
//                TrustEngine<X509Credential, X509Credential> peerAuthenticator) {
//            if(peerAuthenticator == null){
//                throw new IllegalArgumentException("Trust engine used to authenticate peer may not be null");
//            }
//        }
//
//        /** {@inheritDoc} */
//        public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException,
//                UnknownHostException {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        /** {@inheritDoc} */
//        public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        /** {@inheritDoc} */
//        public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException,
//                UnknownHostException {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        /** {@inheritDoc} */
//        public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3, HttpConnectionParams arg4)
//                throws IOException, UnknownHostException, ConnectTimeoutException {
//            // TODO Auto-generated method stub
//            return null;
//        }
//    }

    
}