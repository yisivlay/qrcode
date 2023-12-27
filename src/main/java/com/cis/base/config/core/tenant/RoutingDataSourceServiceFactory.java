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

import com.cis.base.config.core.utils.ThreadLocalContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Factory class to get data source service based on the details stored in
 * {@link ThreadLocal} variable for this request
 * <p>
 * {@link ThreadLocalContextUtil} is used to retrieve the Context
 * @author YSivlay
 */
@Component
public class RoutingDataSourceServiceFactory {

    private final ApplicationContext applicationContext;

    @Autowired
    public RoutingDataSourceServiceFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public RoutingDataSourceService determineDataSourceService() {
        String serviceName = "tomcatJdbcDataSourcePerTenantService";
        if (ThreadLocalContextUtil.CONTEXT_TENANTS.equalsIgnoreCase(ThreadLocalContextUtil.getDataSourceContext())) {
            serviceName = "dataSourceForTenants";
        }
        return this.applicationContext.getBean(serviceName, RoutingDataSourceService.class);

    }
}
