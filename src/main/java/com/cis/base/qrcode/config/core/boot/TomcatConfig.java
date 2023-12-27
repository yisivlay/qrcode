package com.cis.base.qrcode.config.core.boot;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/**
 * @author YSivlay
 */
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.setContextPath("/qrcode-provider");
        factory.addConnectorCustomizers(
                connector -> {
                    connector.setSecure(true);
                    connector.setScheme("https");
                    connector.setPort(8443);
                    connector.setProperty("keyAlias", "tomcat");
                    connector.setProperty("keystorePass", "caminfoservices");
                    connector.setProperty("keystoreFile", "/keystore.jks");
                    connector.setProperty("keyPass", "caminfoservices");
                    connector.setProperty("clientAuth", "false");
                    connector.setProperty("sslProtocol", "TLS");
                    connector.setProperty("sslEnabledProtocols", "+TLSv1.2,+TLSv1.3");
                });
    }
}
