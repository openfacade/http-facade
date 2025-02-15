/*
 * Copyright 2024 OpenFacade Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public TomcatHttpServer(HttpServerConfig config, Tomcat tomcat) {
        super(config);
        this.tomcat = tomcat;
    }

    @Override
    public CompletableFuture<Void> start() {
        this.tomcat.setPort(config.port() == 0 ? SocketUtil.findAvailablePort() : config.port());

        Context ctx = this.tomcat.addContext("", null);

        if (config.tlsConfig() != null) {
            TlsConfig tlsConfig = config.tlsConfig();
            String keyStorePath = tlsConfig.keyStorePath();
            char[] keyStorePassword = tlsConfig.keyStorePassword();

            this.tomcat.getConnector().setSecure(true);
            this.tomcat.getConnector().setScheme("https");

            SSLHostConfig sslHostConfig = new SSLHostConfig();
            SSLHostConfigCertificate cert = new SSLHostConfigCertificate(sslHostConfig, Type.RSA);
            cert.setCertificateKeystoreFile(keyStorePath);
            cert.setCertificateKeystorePassword(new String(keyStorePassword));

            sslHostConfig.addCertificate(cert);
            this.tomcat.getConnector().addSslHostConfig(sslHostConfig);

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
