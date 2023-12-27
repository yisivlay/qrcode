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
package com.cis.base.config.core.exception;

/**
 * @author YSivlay
 * {@link RuntimeException} thrown when an invalid tenant identifier is used in
 * request to platform.
 */
public class InvalidTenantIdentifierException extends RuntimeException {

    public InvalidTenantIdentifierException(final String message) {
        super(message);
    }
}
