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
package com.cis.base.config.core.tenant;

import com.cis.base.config.core.boot.JdbcDriverConfig;
import com.cis.base.config.core.utils.ThreadLocalContextUtil;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation that returns a new or existing tomcat 8 jdbc connection pool
 * datasource based on the tenant details stored in a {@link ThreadLocal}
 * variable for this request.
 * <p>
 * {@link ThreadLocalContextUtil} is used to retrieve the
 * {@link PlatformTenant} for the request.
 * @author YSivlay
 */
@Service
public class TomcatJdbcDataSourcePerTenantService implements RoutingDataSourceService {

    private final Map<Long, DataSource> tenantToDataSourceMap = new HashMap<>(1);
    private final DataSource tenantDataSource;

    @Autowired
    private JdbcDriverConfig driverConfig;

    @Autowired
    public TomcatJdbcDataSourcePerTenantService(final @Qualifier("tenantDataSourceJndi") DataSource tenantDataSource) {
        this.tenantDataSource = tenantDataSource;
    }

    @Override
    public DataSource retrieveDataSource() {

        // default to tenant database datasource
        DataSource tenantDataSource = this.tenantDataSource;
        final PlatformTenant tenant = ThreadLocalContextUtil.getTenant();
        if (tenant != null) {
            final PlatformTenantConnection tenantConnection = tenant.connection();
            synchronized (this.tenantToDataSourceMap) {
                if (this.tenantToDataSourceMap.containsKey(tenantConnection.getConnectionId())) {
                    tenantDataSource = this.tenantToDataSourceMap.get(tenantConnection.getConnectionId());
                } else {
                    tenantDataSource = createNewDataSourceFor(tenantConnection);
                    this.tenantToDataSourceMap.put(tenantConnection.getConnectionId(), tenantDataSource);
                }
            }
        }

        return tenantDataSource;
    }

    // creates the data source oltp and report databases
    private DataSource createNewDataSourceFor(final PlatformTenantConnection tenantConnectionObj) {
        // @see http://www.tomcatexpert.com/blog/2010/04/01/configuring-jdbc-pool-high-concurrency
        String jdbcUrl = this.driverConfig.constructProtocol(tenantConnectionObj.getSchemaServer(), tenantConnectionObj.getSchemaServerPort(), tenantConnectionObj.getSchemaName());
        //final String jdbcUrl = tenantConnectionObj.databaseURL();
        final PoolConfiguration poolConfiguration = new PoolProperties();
        poolConfiguration.setDriverClassName(this.driverConfig.getDriverClassName());
        poolConfiguration.setName(tenantConnectionObj.getSchemaName() + "_pool");
        poolConfiguration.setUrl(jdbcUrl);
        poolConfiguration.setUsername(tenantConnectionObj.getSchemaUsername());
        poolConfiguration.setPassword(tenantConnectionObj.getSchemaPassword());
        poolConfiguration.setInitialSize(tenantConnectionObj.getInitialSize());
        poolConfiguration.setTestOnBorrow(tenantConnectionObj.isTestOnBorrow());
        poolConfiguration.setValidationQuery("SELECT 1");
        poolConfiguration.setValidationInterval(tenantConnectionObj.getValidationInterval());
        poolConfiguration.setRemoveAbandoned(tenantConnectionObj.isRemoveAbandoned());
        poolConfiguration.setRemoveAbandonedTimeout(tenantConnectionObj.getRemoveAbandonedTimeout());
        poolConfiguration.setLogAbandoned(tenantConnectionObj.isLogAbandoned());
        poolConfiguration.setAbandonWhenPercentageFull(tenantConnectionObj.getAbandonWhenPercentageFull());

        //Do we need to enable the below properties and add
        //ResetAbandonedTimer for long-running batch Jobs?
        poolConfiguration.setMaxActive(tenantConnectionObj.getMaxActive());
        poolConfiguration.setMinIdle(tenantConnectionObj.getMinIdle());
        poolConfiguration.setMaxIdle(tenantConnectionObj.getMaxIdle());
        poolConfiguration.setSuspectTimeout(tenantConnectionObj.getSuspectTimeout());
        poolConfiguration.setTimeBetweenEvictionRunsMillis(tenantConnectionObj.getTimeBetweenEvictionRunsMillis());
        poolConfiguration.setMinEvictableIdleTimeMillis(tenantConnectionObj.getMinEvictableIdleTimeMillis());

        poolConfiguration.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReport");

        return new org.apache.tomcat.jdbc.pool.DataSource(poolConfiguration);
    }
}
