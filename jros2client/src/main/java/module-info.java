/*
 * Copyright 2020 jrosclient project
 *
 * Website: https://github.com/lambdaprime/jrosclient
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * Java module which allows to interact with ROS 2 (Robot Operating System).
 *
 * <p>For usage examples see <a href="http://portal2.atwebpages.com/jrosclient">Documentation</a>
 *
 * @see <a href= "https://github.com/lambdaprime/jros2client/releases">Download</a>
 * @see <a href="https://github.com/lambdaprime/jros2client">GitHub repository</a>
 * @author lambdaprime intid@protonmail.com
 */
module jros2client {
    requires id.xfunction;
    requires java.logging;
    requires transitive jrosclient;
    requires transitive jros2messages;
    requires transitive rtpstalk;
    requires io.opentelemetry.api;

    exports id.jros2client;
    exports id.jros2client.impl to
            jros2client.tests,

            // Services in ROS2 are based on custom form of DDS-RPC and therefore they
            // need access to RTPS client
            jros2services;
    exports id.jros2client.impl.rmw to
            jros2client.tests,

            // Services in ROS2 are based on custom form of DDS-RPC and therefore they
            // need access to DDS logic
            jros2services;
}
