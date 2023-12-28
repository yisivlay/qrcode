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

import com.cis.base.config.core.tenant.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author YSivlay
 */
@Component("legacySupportPasswordEncoder")
public class LegacySupportPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcryptEncoder;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LegacySupportPasswordEncoder(@Qualifier("passwordEncoder") final BCryptPasswordEncoder bcryptEncoder,
                                        final RoutingDataSource dataSource) {

        this.bcryptEncoder = bcryptEncoder;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return this.bcryptEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (bcryptEncoder.matches(rawPassword, encodedPassword)) {
            jdbcTemplate.update("UPDATE users SET password = ? WHERE password = ?", bcryptEncoder.encode(rawPassword), encodedPassword);
            return true;
        }
        return this.bcryptEncoder.matches(rawPassword, encodedPassword);
    }
}
