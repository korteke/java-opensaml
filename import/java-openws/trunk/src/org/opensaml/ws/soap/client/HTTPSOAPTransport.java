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

package org.opensaml.ws.soap.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.opensaml.ws.message.decoder.MessageDecoder;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.message.encoder.MessageEncoder;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.soap.common.SOAPMessageContext;
import org.opensaml.ws.transport.TransportException;
import org.opensaml.ws.transport.http.HTTPInTransport;
import org.opensaml.ws.transport.http.HTTPOutTransport;
import org.opensaml.xml.security.credential.Credential;

/**
 * SOAP transport using HTTP.
 */
public class HTTPSOAPTransport implements ClientTransport {

    /** Class logger. */
    private Logger log = Logger.getLogger(HTTPSOAPTransport.class);

    /** HTTP client used to connect to peers. */
    private HttpClient httpClient;

    /** Encoder used to encode message to the outbound transport. */
    private MessageEncoder messageEncoder;

    /** Decoder used to decode message from inbound transport. */
    private MessageDecoder messageDecoder;

    /**
     * Constructor.
     * 
     * @param client client used to communicate with peer.
     * @param encoder encoder used to encode messages onto the outgoing transport
     * @param decoder decoder used to decode messages from inbound transport
     */
    public HTTPSOAPTransport(HttpClient client, MessageEncoder encoder, MessageDecoder decoder) {
        httpClient = client;
        messageEncoder = encoder;
        messageDecoder = decoder;
    }

    /** {@inheritDoc} */
    public void send(URI endpointURI, SOAPMessageContext messageContext) throws TransportException {
        try {
            PostMethod postMethod = new PostMethod(endpointURI.toASCIIString());
            PostMethodHttpOutTransport outTransport = new PostMethodHttpOutTransport(postMethod);
            messageContext.setOutboundMessageTransport(outTransport);
            messageEncoder.encode(messageContext);

            httpClient.executeMethod(postMethod);

            PostMethodHttpInTransport inTransport = new PostMethodHttpInTransport(postMethod);
            messageContext.setInboundMessageTransport(inTransport);
            messageDecoder.decode(messageContext);
        } catch (IOException e) {
            throw new TransportException("Unable to establish connection to peer", e);
        } catch (MessageEncodingException e) {
            throw new TransportException("Unable to encode message onto outbound transport", e);
        } catch (MessageDecodingException e) {
            throw new TransportException("Unable to decode message from inbound transport", e);
        } catch (SecurityPolicyException e){
            throw new TransportException("Inbound transport and response did not meet security policy requirements", e);
        }
    }

    /**
     * Adapts an Apache Commons HTTPClient {@link PostMethod} into an {@link HTTPOutTransport}.
     */
    protected class PostMethodHttpOutTransport implements HTTPOutTransport {

        /** Post method used to send the request. */
        private PostMethod postMethod;

        /** Whether the transport is authenticated. */
        private boolean transportAuthenticated;

        /** Whether the transport is confidential. */
        private boolean transportConfidential;

        /**
         * Constructor.
         * 
         * @param method post method used to send the request
         */
        public PostMethodHttpOutTransport(PostMethod method) {
            postMethod = method;
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public void sendRedirect(String location) {

        }

        /** {@inheritDoc} */
        public void setHeader(String name, String value) {
            postMethod.addRequestHeader(name, value);
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public void setParameter(String name, String value) {

        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public void setStatusCode(int code) {

        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public void setVersion(HTTP_VERSION version) {

        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public OutputStream getOutgoingStream() {
            try {
                PipedInputStream requestInputStream = new PipedInputStream();
                PipedOutputStream pipedOutput = new PipedOutputStream(requestInputStream);
                requestInputStream.connect(pipedOutput);

                InputStreamRequestEntity requestEntity = new InputStreamRequestEntity(requestInputStream);
                postMethod.setRequestEntity(requestEntity);

                return pipedOutput;
            } catch (IOException e) {
                log.error("Error constructing output stream to POST method body", e);
                return null;
            }
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public void setAttribute(String name, Object value) {

        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         * 
         */
        public void setCharacterEncoding(String encoding) {

        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public Object getAttribute(String name) {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public String getCharacterEncoding() {
            return postMethod.getRequestCharSet();
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public Credential getLocalCredential() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public Credential getPeerCredential() {
            return null;
        }

        /** {@inheritDoc} */
        public boolean isAuthenticated() {
            return transportAuthenticated;
        }

        /** {@inheritDoc} */
        public boolean isConfidential() {
            return transportConfidential;
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public void setAuthenticated(boolean isAuthenticated) {
            transportAuthenticated = isAuthenticated;
        }

        /** {@inheritDoc} */
        public void setConfidential(boolean isConfidential) {
            transportConfidential = isConfidential;
        }

        /** {@inheritDoc} */
        public String getHTTPMethod() {
            return postMethod.getName();
        }

        /** {@inheritDoc} */
        public String getHeaderValue(String name) {
            return postMethod.getRequestHeader(name).getValue();
        }

        /** {@inheritDoc} */
        public String getParameter(String name) {
            return postMethod.getParameter(name).getValue();
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public int getStatusCode() {
            return -1;
        }

        /**
         * {@inheritDoc}
         */
        public HTTP_VERSION getVersion() {
            HttpVersion httpVersion = postMethod.getEffectiveVersion();
            if (httpVersion == HttpVersion.HTTP_1_0) {
                return HTTP_VERSION.HTTP1_0;
            } else if (httpVersion == HttpVersion.HTTP_1_1) {
                return HTTP_VERSION.HTTP1_1;
            }
            return null;
        }
    }

    /**
     * Adapts an Apache Commons HTTPClient {@link PostMethod} into an {@link HTTPInTransport}.
     */
    protected class PostMethodHttpInTransport implements HTTPInTransport {

        /** Adapted POST method. */
        private PostMethod postMethod;

        /** Whether the transport is authenticated. */
        private boolean transportAuthenticated;

        /** Whether the transport is confidential. */
        private boolean transportConfidential;

        /**
         * Constructor.
         * 
         * @param method post method to be adapted
         */
        public PostMethodHttpInTransport(PostMethod method) {
            postMethod = method;
        }

        /** {@inheritDoc} */
        public String getPeerAddress() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public String getPeerDomainName() {
            try {
                return postMethod.getURI().getHost();
            } catch (URIException e) {
                log.error("Unable to recover host from request URI", e);
                return null;
            }
        }

        /** {@inheritDoc} */
        public InputStream getIncomingStream() {
            try {
                return postMethod.getResponseBodyAsStream();
            } catch (IOException e) {
                log.error("Unable to retrieve response stream", e);
                return null;
            }
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public Object getAttribute(String name) {
            return null;
        }

        /** {@inheritDoc} */
        public String getCharacterEncoding() {
            return postMethod.getResponseCharSet();
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public Credential getLocalCredential() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public Credential getPeerCredential() {
            // TODO Auto-generated method stub
            return null;
        }

        /** {@inheritDoc} */
        public boolean isAuthenticated() {
            return transportAuthenticated;
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public boolean isConfidential() {
            return transportConfidential;
        }

        /** {@inheritDoc} */
        public void setAuthenticated(boolean isAuthenticated) {
            transportAuthenticated = isAuthenticated;
        }

        /** {@inheritDoc} */
        public void setConfidential(boolean isConfidential) {
            transportConfidential = isConfidential;
        }

        /** {@inheritDoc} */
        public String getHTTPMethod() {
            return postMethod.getName();
        }

        /** {@inheritDoc} */
        public String getHeaderValue(String name) {
            return postMethod.getResponseHeader(name).getValue();
        }

        /**
         * {@inheritDoc}
         * 
         * This method is not supported for this transport implementation.
         */
        public String getParameter(String name) {
            return null;
        }

        /** {@inheritDoc} */
        public int getStatusCode() {
            return postMethod.getStatusCode();
        }

        /**
         * {@inheritDoc}
         */
        public HTTP_VERSION getVersion() {
            HttpVersion httpVersion = postMethod.getEffectiveVersion();
            if (httpVersion == HttpVersion.HTTP_1_0) {
                return HTTP_VERSION.HTTP1_0;
            } else if (httpVersion == HttpVersion.HTTP_1_1) {
                return HTTP_VERSION.HTTP1_1;
            }
            return null;
        }
    }
}