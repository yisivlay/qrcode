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

import com.cis.base.config.core.security.domain.PlatformUser;
import com.cis.base.config.core.security.domain.PlatformUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author YSivlay
 */
@Service(value = "userDetailsService")
public class TenantAwareJpaPlatformUserDetailsService implements PlatformUserDetailsService {

    private final PlatformUserRepository platformUserRepository;

    @Autowired
    public TenantAwareJpaPlatformUserDetailsService(PlatformUserRepository platformUserRepository) {
        this.platformUserRepository = platformUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final boolean deleted = false;
        final boolean enabled = true;
        final PlatformUser user = this.platformUserRepository.findByUsernameAndDeletedAndEnabled(username, deleted, enabled);
        if (user == null) {
            throw new UsernameNotFoundException(username + ": not found");
        }
        return user;
    }
}
