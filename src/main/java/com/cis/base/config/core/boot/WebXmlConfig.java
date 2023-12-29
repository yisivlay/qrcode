/**
 * Copyright 2023 CIS (Cam info Services)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cis.base.config.core.boot;

import com.cis.base.config.core.security.filter.ResponseCorsFilter;
import com.cis.base.config.core.security.filter.TenantAwareBasicAuthenticationFilter;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import javax.servlet.Servlet;

/**
 * @author YSivlay
 */
@Profile("basicauth")
@Configuration
public class WebXmlConfig {

    @Autowired
    private TenantAwareBasicAuthenticationFilter basicAuthenticationFilter;

    @Bean
    public Filter securityFilterChain() {
        return new DelegatingFilterProxy();
    }

    @Bean
    public ServletRegistrationBean<Servlet> jersey() {
        Servlet servlet = new SpringServlet();
        ServletRegistrationBean<Servlet> registrationBean = new ServletRegistrationBean<>();
        registrationBean.setServlet(servlet);
        registrationBean.addUrlMappings("/api/v1/*");
        registrationBean.setName("jersey-servlet");
        registrationBean.setLoadOnStartup(1);
        registrationBean.addInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
        registrationBean.addInitParameter("com.sun.jersey.spi.container.ContainerResponseFilters", ResponseCorsFilter.class.getName());
        registrationBean.addInitParameter("com.sun.jersey.config.feature.DisableWADL", "true");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TenantAwareBasicAuthenticationFilter> filterRegistrationBean() {
        FilterRegistrationBean<TenantAwareBasicAuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(basicAuthenticationFilter);
        filterRegistrationBean.setEnabled(true);
        return filterRegistrationBean;
    }
}
