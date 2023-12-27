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
package com.cis.base.qrcode.config.core.boot;

import com.cis.base.qrcode.config.core.service.TenantDetailsService;
import com.cis.base.qrcode.config.core.tenant.PlatformTenant;
import com.cis.base.qrcode.config.core.tenant.PlatformTenantConnection;
import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.FlywayException;
import com.googlecode.flyway.core.util.jdbc.DriverDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

/**
 * @author YSivlay
 * A service that picks up on tenants that are configured to auto-update their
 * specific schema on application startup.
 */
@Service
public class TenantDatabaseService {

    protected final DataSource tenantDataSource;
    protected final TenantDataSourceService tenantDataSourceService;
    private final TenantDetailsService tenantDetailsService;
    private final JdbcDriverConfig driverConfig;

    @Autowired
    public TenantDatabaseService(@Qualifier("tenantDataSourceJndi") final DataSource dataSource,
                                 final TenantDetailsService detailsService,
                                 final TenantDataSourceService tenantDataSourceService,
                                 final JdbcDriverConfig driverConfig) {
        this.tenantDetailsService = detailsService;
        this.tenantDataSource = dataSource;
        this.tenantDataSourceService = tenantDataSourceService;
        this.driverConfig = driverConfig;
    }

    @PostConstruct
    public void upgradeAllTenants() {
        upgradeTenantDatabase();
        final List<PlatformTenant> tenants = this.tenantDetailsService.findAllTenants();
        for (final PlatformTenant tenant : tenants) {
            final PlatformTenantConnection connection = tenant.connection();
            if (connection.isAutoUpdateEnabled()) {
                final Flyway flyway = new Flyway();
                String connectionProtocol = driverConfig.constructProtocol(connection.getSchemaServer(), connection.getSchemaServerPort(), connection.getSchemaName());
                DriverDataSource source = new DriverDataSource(driverConfig.getDriverClassName(), connectionProtocol, connection.getSchemaUsername(), connection.getSchemaPassword());
                flyway.setDataSource(source);
                flyway.setLocations("sql/migrations/core_db");
                flyway.setOutOfOrder(false);
                try {
                    flyway.migrate();
                } catch (FlywayException e) {
                    String betterMessage = e.getMessage() + "; for Tenant DB URL: " + connectionProtocol + ", username: "
                            + connection.getSchemaUsername();
                    throw new FlywayException(betterMessage, e.getCause());
                }
            }
        }
    }

    /**
     * Initializes, and if required upgrades (using Flyway) the Tenant DB
     * itself.
     */
    private void upgradeTenantDatabase() {
        final Flyway flyway = new Flyway();
        flyway.setDataSource(tenantDataSource);
        flyway.setLocations("sql/migrations/tenant_db");
        flyway.setOutOfOrder(false);
        flyway.migrate();

        tenantDataSourceService.fixUpTenantsSchemaServerPort();
    }
}
