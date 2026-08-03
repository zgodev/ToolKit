package com.zhangyt.network.httputil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SecureSocketFactory extends SSLSocketFactory {
    private SSLSocketFactory socketFactory;
    public static final String[] ALLOW_CIPHER_SUITE = {"TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"};

    public SecureSocketFactory(SSLSocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        Socket createSocket = this.socketFactory.createSocket(host, port);
        if (createSocket instanceof SSLSocket) {
            ((SSLSocket) createSocket).setEnabledCipherSuites(ALLOW_CIPHER_SUITE);
        }
        return createSocket;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        Socket createSocket = this.socketFactory.createSocket(host, port, localHost, localPort);
        if (createSocket instanceof SSLSocket) {
            ((SSLSocket) createSocket).setEnabledCipherSuites(ALLOW_CIPHER_SUITE);
        }
        return createSocket;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        Socket createSocket = this.socketFactory.createSocket(host, port);
        if (createSocket instanceof SSLSocket) {
            ((SSLSocket) createSocket).setEnabledCipherSuites(ALLOW_CIPHER_SUITE);
        }
        return createSocket;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        Socket createSocket = this.socketFactory.createSocket(address, port, localAddress, localPort);
        if (createSocket instanceof SSLSocket) {
            ((SSLSocket) createSocket).setEnabledCipherSuites(ALLOW_CIPHER_SUITE);
        }
        return createSocket;
    }

    @Override
    public Socket createSocket() throws IOException {
        Socket createSocket = this.socketFactory.createSocket();
        if (createSocket instanceof SSLSocket) {
            ((SSLSocket) createSocket).setEnabledCipherSuites(ALLOW_CIPHER_SUITE);
        }
        return createSocket;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return ALLOW_CIPHER_SUITE;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return ALLOW_CIPHER_SUITE;
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        Socket createSocket = this.socketFactory.createSocket(s, host, port, autoClose);
        if (createSocket instanceof SSLSocket) {
            ((SSLSocket) createSocket).setEnabledCipherSuites(ALLOW_CIPHER_SUITE);
        }
        return createSocket;
    }
}
