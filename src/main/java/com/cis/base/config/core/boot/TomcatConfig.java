/**
 * Copyright 2023 CIS (Cam info Services)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cis.base.config.core.boot;

import lombok.SneakyThrows;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

/**
 * @author YSivlay
 */
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @SneakyThrows
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.setContextPath("/qrcode-provider");
        factory.addAdditionalTomcatConnectors(createSslConnector());
    }

    private Connector createSslConnector() throws IOException {
        File keystore = new ClassPathResource("/keystore.jks").getFile();

        Connector connector = new Connector("org.apache.coyote.http11.Http11Nio2Protocol");
        Http11Nio2Protocol protocol = (Http11Nio2Protocol) connector.getProtocolHandler();
        connector.setScheme("https");
        connector.setSecure(true);
        connector.setPort(8443);
        connector.setScheme("https");
        connector.setProperty("keyAlias", "tomcat");
        connector.setProperty("keystorePass", "caminfoservices");
        connector.setProperty("keystoreFile", keystore.getAbsolutePath());
        connector.setProperty("keyPass", "caminfoservices");
        connector.setProperty("clientAuth", "true");
        connector.setProperty("sslEnabledProtocols", "+TLSv1.2,+TLSv1.3");
        protocol.addSslHostConfig(ssLHostConfig());
        protocol.setAcceptCount(100);
        protocol.setMaxKeepAliveRequests(100);
        protocol.setMaxThreads(200);
        protocol.setMinSpareThreads(10);
        protocol.setMaxConnections(8192);
        return connector;
    }

    private SSLHostConfig ssLHostConfig() throws IOException {
        File truststore = new ClassPathResource("/keystore.jks").getFile();

        SSLHostConfig sslHostConfig = new SSLHostConfig();
        sslHostConfig.setHostName("cis");
        sslHostConfig.setTruststoreFile(truststore.getAbsolutePath());
        sslHostConfig.setTruststorePassword("caminfoservices");
        sslHostConfig.setProtocols("TLS");
        return sslHostConfig;
    }
}
