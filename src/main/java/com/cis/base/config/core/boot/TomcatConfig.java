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
