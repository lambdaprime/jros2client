/*
 * Copyright 2023 jrosclient project
 * 
 * Website: https://github.com/lambdaprime/jros2client
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.jros2client.impl;

import id.jrosclient.RosVersion;
import io.opentelemetry.api.common.Attributes;

/**
 * @author lambdaprime intid@protonmail.com
 */
public interface JRos2ClientConstants {
    /**
     * Unique attributes for jros2client specific metrics.
     *
     * <p>Many metrics are shared between all jrosclient implementations: jros2client, jros1client.
     * These attributes allow to identify metrics for jros2client.
     */
    @SuppressWarnings("exports")
    Attributes JROS2CLIENT_ATTRS =
            Attributes.builder().put("RosVersion", RosVersion.ROS2.toString()).build();
}
