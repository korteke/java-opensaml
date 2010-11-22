/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.util.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;

/** Builder used to construct {@link HttpClient} objects configured with particular settings. */
public class HttpClientBuilder {

    /** Local IP address used when establishing connections. Default value: system default local address */
    private InetAddress socketLocalAddress;

    /**
     * Maximum period inactivity between two consecutive data packets in milliseconds. Default value: 5000 (5 seconds)
     */
    private int socketTimeout;

    /** Socket buffer size in bytes. Default size is 8192 bytes. */
    private int socketBufferSize;

    /**
     * Maximum length of time in milliseconds to wait for the connection to be established. Default value: 5000 (5
     * seconds)
     */
    private int connectionTimeout;

    /** Whether the SSL certificates used by the responder should be ignored. Default value: false */
    private boolean connectionDisregardSslCertificate;

    /** Determines whether to pool connections to a host. Default value: true */
    private boolean connectionPooling;

    /**
     * Whether to check a connection for staleness before using. This can be an expensive operation. Default value: true
     */
    private boolean connectionStalecheck;

    /** Total number of connections that may be open. Default value: 20 */
    private int connectionsMaxTotal;

    /** Maximum number of connections that may be opened to a single host. Default value: 2 */
    private int connectionsMaxPerRoute;

    /** Host name of the HTTP proxy server through which connections will be made. Default value: null. */
    private String connectionProxyHost;

    /** Port number of the HTTP proxy server through which connections will be made. Default value: 8080. */
    private int connectionProxyPort;

    /** Username used to connect to the HTTP proxy server. Default value: null. */
    private String connectionProxyUsername;

    /** Password used to connect to the HTTP proxy server. Default value: null. */
    private String connectionProxyPassword;

    /** Whether to follow HTTP redirects. Default value: true */
    private boolean httpFollowRedirects;

    /** Character set used for HTTP entity content. Default value: UTF-8 */
    private String httpContentCharSet;

    /** Constructor. */
    public HttpClientBuilder() {
        resetDefaults();
    }

    /** Resets all builder parameters to their defaults. */
    public void resetDefaults() {
        socketLocalAddress = null;
        socketTimeout = 5000;
        socketBufferSize = 8192;
        connectionTimeout = 5000;
        connectionDisregardSslCertificate = false;
        connectionPooling = true;
        connectionStalecheck = true;
        connectionsMaxTotal = 20;
        connectionsMaxPerRoute = 2;
        connectionProxyHost = null;
        connectionProxyPort = 8080;
        connectionProxyUsername = null;
        connectionProxyPassword = null;
        httpFollowRedirects = true;
        httpContentCharSet = "UTF-8";
    }

    /**
     * Gets the local IP address used when making requests.
     * 
     * @return local IP address used when making requests
     */
    public InetAddress getSocketLocalAddress() {
        return socketLocalAddress;
    }

    /**
     * Sets the local IP address used when making requests.
     * 
     * @param address local IP address used when making requests
     */
    public void setSocketLocalAddress(final InetAddress address) {
        socketLocalAddress = address;
    }

    /**
     * Sets the local IP address used when making requests.
     * 
     * @param ipOrHost IP address or hostname, never null
     * 
     * @throws UnknownHostException thrown if the given IP or hostname can not be resolved
     */
    public void setSocketLocalAddress(final String ipOrHost) throws UnknownHostException {
        Assert.isNotNull(ipOrHost, "IP or hostname may not be null");
        socketLocalAddress = InetAddress.getByName(ipOrHost);
    }

    /**
     * Gets the maximum period inactivity between two consecutive data packets in milliseconds. A value of less than 1
     * indicates no timeout.
     * 
     * @return maximum period inactivity between two consecutive data packets in milliseconds
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Sets the maximum period inactivity between two consecutive data packets in milliseconds. A value of less than 1
     * indicates no timeout.
     * 
     * @param timeout maximum period inactivity between two consecutive data packets in milliseconds
     */
    public void setSocketTimeout(final int timeout) {
        this.socketTimeout = timeout;
    }

    /**
     * Gets the size of the socket buffer, in bytes, used for request/response buffering.
     * 
     * @return size of the socket buffer, in bytes, used for request/response buffering
     */
    public int getSocketBufferSize() {
        return socketBufferSize;
    }

    /**
     * Sets size of the socket buffer, in bytes, used for request/response buffering.
     * 
     * @param size size of the socket buffer, in bytes, used for request/response buffering; must be greater than 0
     */
    public void setSocketBufferSize(final int size) {
        Assert.isGreaterThan(0, size, "Socket buffer size must be greater than 0");
        socketBufferSize = size;
    }

    /**
     * Gets the maximum length of time in milliseconds to wait for the connection to be established. A value of less
     * than 1 indicates no timeout.
     * 
     * @return maximum length of time in milliseconds to wait for the connection to be established
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Sets the maximum length of time in milliseconds to wait for the connection to be established. A value of less
     * than 1 indicates no timeout.
     * 
     * @param timeout maximum length of time in milliseconds to wait for the connection to be established
     */
    public void setConnectionTimeout(final int timeout) {
        connectionTimeout = timeout;
    }

    /**
     * Gets whether the responder's SSL certificate should be ignored.
     * 
     * @return whether the responder's SSL certificate should be ignored
     */
    public boolean isConnectionDisregardSslCertificate() {
        return connectionDisregardSslCertificate;
    }

    /**
     * Sets whether the responder's SSL certificate should be ignored.
     * 
     * @param disregard whether the responder's SSL certificate should be ignored
     */
    public void setConnectionDisregardSslCertificate(final boolean disregard) {
        connectionDisregardSslCertificate = disregard;
    }

    /**
     * Gets whether connections with the same route (destination plus proxy(ies)) should be pooled and reused.
     * 
     * @return whether connections with the same route should be pooled and reused
     */
    public boolean isConnectionPooling() {
        return connectionPooling;
    }

    /**
     * Sets whether connections with the same route (destination plus proxy(ies)) should be pooled and reused.
     * 
     * @param pooling whether connections with the same route should be pooled and reused
     */
    public void setConnectionPooling(final boolean pooling) {
        connectionPooling = pooling;
    }

    /**
     * Gets whether reused connections are checked if they are closed before being used by the client.
     * 
     * @return whether reused connections are checked if they are closed before being used by the client
     */
    public boolean isConnectionStalecheck() {
        return connectionStalecheck;
    }

    /**
     * Sets whether reused connections are checked if they are closed before being used by the client. Checking can take
     * up to 30ms (per request). If checking is turned off an I/O error occurs if the connection is used request. This
     * should be enabled uncles the code using the client explicitly handles the error case and retries connection as
     * appropriate.
     * 
     * @param check whether reused connections are checked if they are closed before being used by the client
     */
    public void setConnectionStalecheck(final boolean check) {
        connectionStalecheck = check;
    }

    /**
     * Gets the maximum number of connections that may be open at any given time when pooling is used.
     * 
     * @return maximum number of connections that may be open at any given time when pooling is used
     */
    public int getConnectionsMaxTotal() {
        return connectionsMaxTotal;
    }

    /**
     * Sets the maximum number of connections that may be open at any given time when pooling is used.
     * 
     * @param max maximum number of connections that may be open at any given time when pooling is used; must be greater
     *            than zero
     */
    public void setConnectionsMaxTotal(final int max) {
        Assert.isGreaterThan(0, max, "Max total connections must be greater than 0");
        connectionsMaxTotal = max;
    }

    /**
     * Gets the maximum number of connection per route. A route is the destination host plus all intermediary proxies.
     * 
     * @return maximum number of connection per route
     */
    public int getConnectionsMaxPerRoute() {
        return connectionsMaxPerRoute;
    }

    /**
     * Sets the maximum number of connection per route. A route is the destination host plus all intermediary proxies.
     * 
     * @param max maximum number of connection per route; must be greater than zero
     */
    public void setConnectionsMaxPerRoute(final int max) {
        Assert.isGreaterThan(0, max, "Max connections per route must be greater than zero");
        connectionsMaxPerRoute = max;
    }

    /**
     * Gets the hostname of the default proxy used when making connection. A null indicates no default proxy.
     * 
     * @return hostname of the default proxy used when making connection
     */
    public String getConnectionProxyHost() {
        return connectionProxyHost;
    }

    /**
     * Sets the hostname of the default proxy used when making connection. A null indicates no default proxy.
     * 
     * @param host hostname of the default proxy used when making connection
     */
    public void setConnectionProxyHost(final String host) {
        connectionProxyHost = StringSupport.trimOrNull(host);
    }

    /**
     * Gets the port of the default proxy used when making connection.
     * 
     * @return port of the default proxy used when making connection
     */
    public int getConnectionProxyPort() {
        return connectionProxyPort;
    }

    /**
     * Sets the port of the default proxy used when making connection.
     * 
     * @param port port of the default proxy used when making connection; must be greater than 0 and less than 65536
     */
    public void setConnectionProxyPort(final int port) {
        Assert.numberInRangeExclusive(0, 65536, port, "Proxy port must be between 0 and 65536, exclusive");
        connectionProxyPort = port;
    }

    /**
     * Gets the username to use when authenticating to the proxy.
     * 
     * @return username to use when authenticating to the proxy
     */
    public String getConnectionProxyUsername() {
        return connectionProxyUsername;
    }

    /**
     * Sets the username to use when authenticating to the proxy.
     * 
     * @param usename username to use when authenticating to the proxy; may be null
     */
    public void setConnectionProxyUsername(final String usename) {
        connectionProxyUsername = usename;
    }

    /**
     * Gets the password used when authenticating to the proxy.
     * 
     * @return password used when authenticating to the proxy
     */
    public String getConnectionProxyPassword() {
        return connectionProxyPassword;
    }

    /**
     * Sets the password used when authenticating to the proxy.
     * 
     * @param password password used when authenticating to the proxy; may be null
     */
    public void setConnectionProxyPassword(final String password) {
        connectionProxyPassword = password;
    }

    /**
     * Gets whether HTTP redirects will be followed.
     * 
     * @return whether HTTP redirects will be followed
     */
    public boolean isHttpFollowRedirects() {
        return httpFollowRedirects;
    }

    /**
     * Gets whether HTTP redirects will be followed.
     * 
     * @param followRedirects true if redirects are followed, false otherwise
     */
    public void setHttpFollowRedirects(final boolean followRedirects) {
        httpFollowRedirects = followRedirects;
    }

    /**
     * Gets the character set used with the HTTP entity (body).
     * 
     * @return character set used with the HTTP entity (body)
     */
    public String getHttpContentCharSet() {
        return httpContentCharSet;
    }

    /**
     * Sets the character set used with the HTTP entity (body).
     * 
     * @param charSet character set used with the HTTP entity (body)
     */
    public void setHttpContentCharSet(final String charSet) {
        httpContentCharSet = charSet;
    }

    /**
     * Constructs an {@link HttpClient} using the settings of this builder.
     * 
     * @return the constructed client
     */
    public HttpClient buildClient() {
        final DefaultHttpClient client = new DefaultHttpClient(buildConnectionManager());
        client.addRequestInterceptor(new RequestAcceptEncoding());
        client.addResponseInterceptor(new ResponseContentEncoding());

        final HttpParams httpParams = client.getParams();

        if (socketLocalAddress != null) {
            httpParams.setParameter(AllClientPNames.LOCAL_ADDRESS, socketLocalAddress);
        }

        if (socketTimeout > 0) {
            httpParams.setIntParameter(AllClientPNames.SO_TIMEOUT, socketTimeout);
        }

        httpParams.setIntParameter(AllClientPNames.SOCKET_BUFFER_SIZE, socketBufferSize);

        if (connectionTimeout > 0) {
            httpParams.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, connectionTimeout);
        }

        httpParams.setBooleanParameter(AllClientPNames.STALE_CONNECTION_CHECK, connectionStalecheck);

        if (connectionProxyHost != null) {
            final HttpHost proxyHost = new HttpHost(connectionProxyHost, connectionProxyPort);
            httpParams.setParameter(AllClientPNames.DEFAULT_PROXY, proxyHost);

            if (connectionProxyUsername != null && connectionProxyPassword != null) {
                final CredentialsProvider credProvider = client.getCredentialsProvider();
                credProvider.setCredentials(new AuthScope(connectionProxyHost, connectionProxyPort),
                        new UsernamePasswordCredentials(connectionProxyUsername, connectionProxyPassword));
            }
        }

        httpParams.setBooleanParameter(AllClientPNames.HANDLE_REDIRECTS, httpFollowRedirects);

        httpParams.setParameter(AllClientPNames.HTTP_CONTENT_CHARSET, httpContentCharSet);

        return client;
    }

    /**
     * Builds the connection manager used by the HTTP client. If {@link #connectionPooling} is false then the
     * {@link SingleClientConnManager} is used. Otherwise the {@link ThreadSafeClientConnManager} is used with
     * {@link ThreadSafeClientConnManager#setDefaultMaxPerRoute(int)} set to {@link #connectionsMaxPerRoute} and
     * {@link ThreadSafeClientConnManager#setMaxTotalConnections(int)} set to {@link #connectionsMaxTotal}.
     * 
     * @return the connection manager used by the HTTP client
     */
    private ClientConnectionManager buildConnectionManager() {
        final SchemeRegistry registry = buildSchemeRegistry();

        if (!connectionPooling) {
            return new SingleClientConnManager(registry);
        } else {
            final ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(registry);
            manager.setDefaultMaxPerRoute(connectionsMaxPerRoute);
            manager.setMaxTotal(connectionsMaxTotal);
            return manager;
        }
    }

    /**
     * Creates the default scheme registry for connection. The constructed registry supports http with a default port of
     * 80 and https with a default port of 443. If {@link #connectionDisregardSslCertificate} is true, than the https
     * port will accept any certificate presented by the responder.
     * 
     * @return the default scheme registry.
     */
    private SchemeRegistry buildSchemeRegistry() {
        final SchemeRegistry registry = new SchemeRegistry();

        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

        final SSLSocketFactory sslSF;
        if (!connectionDisregardSslCertificate) {
            sslSF = SSLSocketFactory.getSocketFactory();
        } else {
            X509TrustManager noTrustManager = new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    // accept everything
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    // accept everything
                }
            };

            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, new TrustManager[] { noTrustManager }, null);
                sslSF = new SSLSocketFactory(sslcontext);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("TLS SSLContext type is required to be supported by the JVM but is not", e);
            } catch (KeyManagementException e) {
                throw new RuntimeException("Some how the trust everything trust manager didn't trust everything", e);
            }
        }
        registry.register(new Scheme("https", 443, sslSF));

        return registry;
    }
}