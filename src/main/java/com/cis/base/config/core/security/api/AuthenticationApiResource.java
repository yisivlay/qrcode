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
package com.cis.base.config.core.security.api;

import com.cis.base.administration.role.data.RoleData;
import com.cis.base.administration.role.domain.Role;
import com.cis.base.administration.user.domain.User;
import com.cis.base.config.core.security.data.AuthenticatedUserData;
import com.cis.base.config.core.security.service.SpringSecurityPlatformSecurityContext;
import com.cis.base.config.core.serialization.ToApiJsonSerializer;
import com.sun.jersey.core.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author YSivlay
 */
@Component
@Profile("basicauth")
@Scope("singleton")
@Path("/authentication")
public class AuthenticationApiResource {

    private final DaoAuthenticationProvider authenticationProvider;
    private final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializer;
    private final SpringSecurityPlatformSecurityContext springSecurityPlatformSecurityContext;

    @Autowired
    public AuthenticationApiResource(@Qualifier("authenticationProvider") final DaoAuthenticationProvider authenticationProvider,
                                     final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializer,
                                     final SpringSecurityPlatformSecurityContext springSecurityPlatformSecurityContext) {
        this.authenticationProvider = authenticationProvider;
        this.apiJsonSerializer = apiJsonSerializer;
        this.springSecurityPlatformSecurityContext = springSecurityPlatformSecurityContext;
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public String authenticate(@QueryParam("username") final String username,
                               @QueryParam("password") final String password) {

        final Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        final Authentication checkAuthentication = this.authenticationProvider.authenticate(authentication);

        final Collection<String> permissions = new ArrayList<>();
        AuthenticatedUserData authenticatedUserData = AuthenticatedUserData.builder().username(username).permissions(permissions).build();
        if (checkAuthentication.isAuthenticated()) {
            final Collection<GrantedAuthority> authorities = new ArrayList<>(checkAuthentication.getAuthorities());
            authorities.stream().map(GrantedAuthority::getAuthority).forEach(permissions::add);
        }

        final byte[] base64EncodedAuthenticationKey = Base64.encode(username + ":" + password);
        final User principal = (User) checkAuthentication.getPrincipal();
        final Collection<RoleData> roles = new ArrayList<>();
        final Set<Role> userRoles = principal.getRoles();
        for (final Role role : userRoles) {
            roles.add(
                    RoleData.builder()
                            .id(role.getId())
                            .name(role.getName())
                            .description(role.getDescription())
                            .disabled(role.getDisabled())
                            .build()
            );
        }
        final Long officeId = principal.getOffice().getId();
        final String officeName = principal.getOffice().getName();
        if (this.springSecurityPlatformSecurityContext.doesPasswordHasToBeRenewed(principal)) {
            authenticatedUserData = AuthenticatedUserData.builder()
                    .username(username)
                    .userId(principal.getId())
                    .base64EncodedAuthenticationKey(new String(base64EncodedAuthenticationKey))
                    .build();
        } else {
            authenticatedUserData = AuthenticatedUserData.builder()
                    .username(username)
                    .officeId(officeId)
                    .officeName(officeName)
                    .roles(roles)
                    .permissions(permissions)
                    .userId(principal.getId())
                    .base64EncodedAuthenticationKey(new String(base64EncodedAuthenticationKey))
                    .build();
        }
        return this.apiJsonSerializer.serialize(authenticatedUserData);
    }
}
