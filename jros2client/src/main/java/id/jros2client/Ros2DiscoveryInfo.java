/*
 * Copyright 2026 jrosclient project
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

import id.jros2client.qos.QosDurability;
import id.jros2client.qos.QosReliability;
import id.jroscommon.RosName;
import id.jrosmessages.UntypedMessageMetadata;
import id.xfunction.XJsonStringBuilder;

/**
 * @author lambdaprime intid@protonmail.com
 */
public record Ros2DiscoveryInfo(
        RosName name,
        UntypedMessageMetadata type,
        boolean isPublisher,
        QosReliability reliabilityType,
        QosDurability durabilityType) {
    @Override
    public String toString() {
        XJsonStringBuilder builder = new XJsonStringBuilder(this);
        builder.append("name", name);
        builder.append("type", type);
        builder.append("isPublisher", isPublisher);
        builder.append("reliabilityType", reliabilityType);
        builder.append("durabilityType", durabilityType);
        return builder.toString();
    }
}
