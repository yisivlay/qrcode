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
package com.cis.base.config.core.security.data;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.StopWatch;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YSivlay
 */
@Getter
@Setter
public class RequestLog {

    @SuppressWarnings("unused")
    private final long startTime;
    @SuppressWarnings("unused")
    private final long totalTime;
    @SuppressWarnings("unused")
    private final String method;
    @SuppressWarnings("unused")
    private final String url;
    @SuppressWarnings("unused")
    private final Map<String, String[]> parameters;

    private RequestLog(final long startTime, final long time, final String method, final String requestUrl,
                       final Map<String, String[]> parameters) {
        this.startTime = startTime;
        this.totalTime = time;
        this.method = method;
        this.url = requestUrl;
        this.parameters = parameters;
    }

    public static RequestLog from(final StopWatch task, final HttpServletRequest request) throws IOException {
        final String requestUrl = request.getRequestURL().toString();

        final Map<String, String[]> parameters = new HashMap<>(request.getParameterMap());
        parameters.remove("password");
        parameters.remove("_");

        return new RequestLog(task.getStartTime(), task.getTime(), request.getMethod(), requestUrl, parameters);
    }

}
