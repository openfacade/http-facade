package io.github.openfacade.http;

import java.security.SecureRandom;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class JdkSslContextUtil {
    public static SSLContext buildSSLContextFromJks(String keyStorePath,
                                                    char[] keyStorePassword,
                                                    String trustStorePath,
                                                    char[] trustStorePassword,
                                                    boolean disableSslVerify) {
        try {
            KeyManagerFactory keyManagerFactory = TlsUtil.initKeyManagerFactory(keyStorePath, keyStorePassword);

            // Load the trust store, if specified
            TrustManagerFactory trustManagerFactory = null;
            if (trustStorePath != null) {
                trustManagerFactory = TlsUtil.initTrustManagerFactory(trustStorePath, trustStorePassword);
            }

            // Set up SSL context
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");

            TrustManager[] trustManagers;
            if (disableSslVerify) {
                trustManagers = new TrustManager[]{InsecureTrustManager.INSTANCE};
            } else if (trustManagerFactory != null) {
                trustManagers = trustManagerFactory.getTrustManagers();
            } else {
                trustManagers = null;
            }

            // Set up SSL parameters
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, new SecureRandom());

            if (disableSslVerify) {
                sslContext.getDefaultSSLParameters().setEndpointIdentificationAlgorithm(null);
            }

            return sslContext;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
