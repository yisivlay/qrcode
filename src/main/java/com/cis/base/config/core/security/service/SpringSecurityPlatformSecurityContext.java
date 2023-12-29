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
package com.cis.base.config.core.security.service;

import com.cis.base.administration.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author YSivlay
 */
@Service
public class SpringSecurityPlatformSecurityContext implements PlatformSecurityContext {

    @Override
    public User authenticatedUser() {
        User currentUser = null;
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            final Authentication auth = context.getAuthentication();
            if (auth != null) {
                currentUser = (User) auth.getPrincipal();
            }
        }

        if (currentUser == null) {
            throw new RuntimeException();
        }

        if (this.doesPasswordHasToBeRenewed(currentUser)) {
            throw new RuntimeException("Password of this user has expired, please reset it!");
        }

        return currentUser;
    }

    public boolean doesPasswordHasToBeRenewed(User user) {
        // TODO Check validate date to be renew password here
        return false;
    }
}
