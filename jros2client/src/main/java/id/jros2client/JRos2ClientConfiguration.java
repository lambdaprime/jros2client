/*
 * Copyright 2020 jrosclient project
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

import pinorobotics.rtpstalk.RtpsTalkConfiguration;

/**
 * Configuration parameters of {@link JRos2Client}
 *
 * @author lambdaprime intid@protonmail.com
 */
public record JRos2ClientConfiguration(RtpsTalkConfiguration rtpsTalkConfiguration) {
    public static class Builder {

        private RtpsTalkConfiguration rtpsTalkConfiguration =
                new RtpsTalkConfiguration.Builder().build();

        /**
         * RTPS settings for ROS2 transport protocol.
         *
         * <p>Default RTPS settings taken from {@link
         * pinorobotics.rtpstalk.RtpsTalkConfiguration.Builder}
         */
        public Builder rtpsTalkConfiguration(RtpsTalkConfiguration rtpsTalkConfiguration) {
            this.rtpsTalkConfiguration = rtpsTalkConfiguration;
            return this;
        }

        public JRos2ClientConfiguration build() {
            return new JRos2ClientConfiguration(rtpsTalkConfiguration);
        }
    }
}
