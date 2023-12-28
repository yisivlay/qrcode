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

import com.cis.base.config.core.boot.JdbcTenantDetailsService;
import com.cis.base.config.core.exception.InvalidTenantIdentifierException;
import com.cis.base.config.core.tenant.PlatformTenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author YSivlay
 */
@Service
public class BasicAuthTenantDetailsServiceJdbc implements BasicAuthTenantDetailsService{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BasicAuthTenantDetailsServiceJdbc(@Qualifier("tenantDataSourceJndi") final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public PlatformTenant loadTenantById(String tenantId) {
        try {
            final JdbcTenantDetailsService.TenantMapper rm = new JdbcTenantDetailsService.TenantMapper();
            final String sql = "SELECT  " + rm.schema() + " WHERE t.identifier like ?";

            return this.jdbcTemplate.queryForObject(sql, rm, tenantId);
        } catch (final EmptyResultDataAccessException e) {
            throw new InvalidTenantIdentifierException("The tenant identifier: " + tenantId + " is not valid.");
        }
    }
}
