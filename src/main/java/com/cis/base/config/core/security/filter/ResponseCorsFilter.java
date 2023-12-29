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
package com.cis.base.config.core.security.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.core.Response;

/**
 * @author YSivlay
 */
public class ResponseCorsFilter implements ContainerResponseFilter {
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        final Response.ResponseBuilder resp = Response.fromResponse(response.getResponse());

        resp
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Expose-Headers", "Platform-TenantId")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        final String reqHead = request.getHeaderValue("Access-Control-Request-Headers");
        if (null != reqHead) {
            resp.header("Access-Control-Allow-Headers", reqHead);
        }
        response.setResponse(resp.build());
        return response;
    }
}
