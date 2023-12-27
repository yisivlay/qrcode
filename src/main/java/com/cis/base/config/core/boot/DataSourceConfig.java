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

import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author YSivlay
 * Configuration for a DataSource.
 * @see DataSourceProperties about how to configure this DataSource
 */
@Configuration
public class DataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Autowired
    JdbcDriverConfig config;

    @Bean
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties(config.getDriverClassName(), config.getProtocol(), config.getSubProtocol(), config.getPort());
    }

    @Bean
    public DataSource tenantDataSourceJndi() {
        PoolConfiguration p = getProperties();
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(p);
        logger.info("Created new datasource: " + p.getUrl());
        return ds;
    }

    protected DataSourceProperties getProperties() {
        return dataSourceProperties();
    }
}
