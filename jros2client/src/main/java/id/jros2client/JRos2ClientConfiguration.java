/*
 * Copyright 2020 jros2client project
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
package id.jros2client;

/**
 * Configuration parameters of {@link JRos2Client}
 *
 * @author lambdaprime intid@protonmail.com
 */
public record JRos2ClientConfiguration() {

    //    public static class Builder {
    //
    //        public static final int DEFAULT_START_PORT = 7412;
    //
    //        public List<String> networkIfaces = List.of();
    //        private int startPort;
    //
    //        /**
    //         * List of network interfaces which {@link JRos2Client} will be working on.
    //         *
    //         * <p>By default it is active on all network interfaces but for performance reasons
    //         * it is recommended to keep it active only for those interfaces where
    //         * ROS is available.
    //         */
    //        public Builder networkInterfaces(String... networkIfaces) {
    //            this.networkIfaces = Arrays.asList(networkIfaces);
    //            return this;
    //        }
    //
    //        /**
    //         * Default starting port from which port assignment for {@link JRos2Client}
    //         * will happen. {@link JRos2Client} will be listening them and using for
    //         * discovery of other ROS nodes and  them for communication with them.
    //         */
    //        public Builder startPort(int startPort) {
    //            this.startPort = startPort;
    //            return this;
    //        }
    //    }

}
