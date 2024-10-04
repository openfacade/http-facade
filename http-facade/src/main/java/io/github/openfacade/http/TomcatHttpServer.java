package io.github.openfacade.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLHostConfigCertificate.Type;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class TomcatHttpServer extends ServletHttpServer {
    private final Tomcat tomcat;

    public TomcatHttpServer(HttpServerConfig config) {
        super(config);
        this.tomcat = new Tomcat();
        tomcat.setPort(config.port() == 0 ? SocketUtil.findAvailablePort() : config.port());

        Context ctx = tomcat.addContext("", null);

        if (config.tlsConfig() != null) {
            TlsConfig tlsConfig = config.tlsConfig();
            String keyStorePath = tlsConfig.keyStorePath();
            char[] keyStorePassword = tlsConfig.keyStorePassword();

            tomcat.getConnector().setSecure(true);
            tomcat.getConnector().setScheme("https");

            SSLHostConfig sslHostConfig = new SSLHostConfig();
            SSLHostConfigCertificate cert = new SSLHostConfigCertificate(sslHostConfig, Type.RSA);
            cert.setCertificateKeystoreFile(keyStorePath);
            cert.setCertificateKeystorePassword(new String(keyStorePassword));

            sslHostConfig.addCertificate(cert);
            tomcat.getConnector().addSslHostConfig(sslHostConfig);

            // Enforce HTTPS (optional)
            SecurityConstraint securityConstraint = new SecurityConstraint();
            securityConstraint.setUserConstraint("CONFIDENTIAL");
            SecurityCollection collection = new SecurityCollection();
            collection.addPattern("/*");
            securityConstraint.addCollection(collection);
            ctx.addConstraint(securityConstraint);
        }

        Tomcat.addServlet(ctx, "requestHandlerServlet", new RequestHandlerServlet());
        ctx.addServletMappingDecoded("/*", "requestHandlerServlet");
    }

    @Override
    public CompletableFuture<Void> start() {
        return CompletableFuture.runAsync(() -> {
            try {
                tomcat.start();
                log.info("Tomcat HTTP server started on port {}", config.port());
            } catch (LifecycleException e) {
                throw new RuntimeException("Failed to start Tomcat server: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> stop() {
        return CompletableFuture.runAsync(() -> {
            try {
                if (tomcat != null) {
                    tomcat.stop();
                }
            } catch (LifecycleException e) {
                throw new RuntimeException("Failed to stop Tomcat server: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public int listenPort() {
        return tomcat.getConnector().getLocalPort();
    }
}
