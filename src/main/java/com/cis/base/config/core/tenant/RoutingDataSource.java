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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Based on springs {@link AbstractRoutingDataSource} idea, this is a
 * {@link DataSource} that routes or delegates to another data source depending
 * on the tenant details passed in the request.
 * <p>
 * The tenant details are process earlier and stored in a {@link ThreadLocal}.
 * <p>
 * The {@link RoutingDataSourceService} is responsible for returning the
 * appropriate {@link DataSource} for the tenant of this request.
 * @author YSivlay
 */
@Service(value = "routingDataSource")
public class RoutingDataSource extends AbstractDataSource {

    private final RoutingDataSourceServiceFactory dataSourceServiceFactory;

    @Autowired
    public RoutingDataSource(RoutingDataSourceServiceFactory dataSourceServiceFactory) {
        this.dataSourceServiceFactory = dataSourceServiceFactory;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return determineTargetDataSource().getConnection();
    }

    private DataSource determineTargetDataSource() {
        return this.dataSourceServiceFactory.determineDataSourceService().retrieveDataSource();
    }

    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return determineTargetDataSource().getConnection(username, password);
    }
}
