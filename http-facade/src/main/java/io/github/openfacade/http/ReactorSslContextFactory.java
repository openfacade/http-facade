package io.github.openfacade.http;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.util.Arrays;

public class ReactorSslContextFactory {
    public static SslContext buildFromJks(String keyStorePath,
                                          char[] keyStorePassword,
                                          String trustStorePath,
                                          char[] trustStorePassword,
                                          boolean disableSslVerify,
                                          String[] tlsProtocols,
                                          String[] tlsCiphers) {

        try {
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();

            if (keyStorePath != null && keyStorePassword != null) {
                sslContextBuilder.keyManager(TlsUtil.initKeyManagerFactory(keyStorePath, keyStorePassword));
            }

            if (disableSslVerify) {
                sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
            } else if (trustStorePath != null && trustStorePassword != null) {
                sslContextBuilder.trustManager(TlsUtil.initTrustManagerFactory(trustStorePath, trustStorePassword));
            }

            if (tlsProtocols != null) {
                sslContextBuilder.protocols(tlsProtocols);
            }

            if (tlsCiphers != null) {
                sslContextBuilder.ciphers(Arrays.asList(tlsCiphers));
            }

            return sslContextBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error setting up SSL configuration", e);
        }
    }
}
