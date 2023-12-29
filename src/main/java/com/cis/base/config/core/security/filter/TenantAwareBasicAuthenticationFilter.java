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
package com.cis.base.config.core.security.filter;

import com.cis.base.administration.user.domain.User;
import com.cis.base.config.core.exception.InvalidTenantIdentifierException;
import com.cis.base.config.core.security.data.RequestLog;
import com.cis.base.config.core.security.service.BasicAuthTenantDetailsService;
import com.cis.base.config.core.serialization.ToApiJsonSerializer;
import com.cis.base.config.core.tenant.PlatformTenant;
import com.cis.base.config.core.utils.ThreadLocalContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.cis.base.config.core.security.filter.TenantConstant.tenantRequestHeader;

/**
 * @author YSivlay
 */
@Profile("basicauth")
@Service(value = "basicAuthenticationProcessingFilter")
public class TenantAwareBasicAuthenticationFilter extends BasicAuthenticationFilter {
    private final static Logger logger = LoggerFactory.getLogger(TenantAwareBasicAuthenticationFilter.class);

    private static boolean firstRequestProcessed = false;
    private final BasicAuthTenantDetailsService basicAuthTenantDetailsService;
    private final ToApiJsonSerializer<RequestLog> toApiJsonSerializer;

    @Autowired
    public TenantAwareBasicAuthenticationFilter(final AuthenticationManager authenticationManager,
                                                final AuthenticationEntryPoint authenticationEntryPoint,
                                                final BasicAuthTenantDetailsService basicAuthTenantDetailsService,
                                                final ToApiJsonSerializer<RequestLog> toApiJsonSerializer) {
        super(authenticationManager, authenticationEntryPoint);
        this.basicAuthTenantDetailsService = basicAuthTenantDetailsService;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        final StopWatch task = new StopWatch();
        task.start();
        try {
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            } else {
                String tenantIdentifier = request.getHeader(tenantRequestHeader);
                if (StringUtils.isBlank(tenantIdentifier)) {
                    tenantIdentifier = request.getParameter("tenantIdentifier");
                }
                if (tenantIdentifier == null) {
                    throw new InvalidTenantIdentifierException("No tenant identifier found: Add request header of '"
                            + tenantRequestHeader
                            + "' or add the parameter 'tenantIdentifier' to query string of request URL.");
                }
                String pathInfo = request.getRequestURI();
                final PlatformTenant tenant = this.basicAuthTenantDetailsService.loadTenantById(tenantIdentifier);
                ThreadLocalContextUtil.setTenant(tenant);
                String authToken = request.getHeader("Authorization");
                if (authToken != null && authToken.startsWith("Basic ")) {
                    ThreadLocalContextUtil.setAuthToken(authToken.replaceFirst("Basic ", ""));
                }
                if (!firstRequestProcessed) {
                    final String baseUrl = request.getRequestURL().toString().replace(request.getPathInfo(), "/");
                    System.setProperty("baseUrl", baseUrl);
                    firstRequestProcessed = true;
                }
            }
            super.doFilterInternal(request, response, chain);
        } catch (final InvalidTenantIdentifierException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            response.addHeader("WWW-Authenticate", "Basic realm=\"" + "Platform API" + "\"");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            task.stop();
            logger.info(this.toApiJsonSerializer.serialize(task));
        }
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        super.onSuccessfulAuthentication(request, response, authResult);
        User user = (User) authResult.getPrincipal();

        String pathURL = request.getRequestURI();
        boolean isSelfServiceRequest = (pathURL != null && pathURL.contains("/self/"));
        boolean notAllowed = ((isSelfServiceRequest && !user.isSelfServiceUser()) || (!isSelfServiceRequest && user.isSelfServiceUser()));
        if (notAllowed) {
            throw new BadCredentialsException("User not authorised to use the requested resource.");
        }
    }
}
