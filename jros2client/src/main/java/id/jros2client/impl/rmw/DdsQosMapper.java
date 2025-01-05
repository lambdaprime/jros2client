/*
 * Copyright 2022 jrosclient project
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
package id.jros2client.impl.rmw;

import id.jros2client.qos.PublisherQos;
import id.jros2client.qos.QosDurability;
import id.jros2client.qos.QosReliability;
import id.jros2client.qos.SubscriberQos;
import pinorobotics.rtpstalk.qos.DurabilityType;
import pinorobotics.rtpstalk.qos.PublisherQosPolicy;
import pinorobotics.rtpstalk.qos.ReliabilityType;
import pinorobotics.rtpstalk.qos.SubscriberQosPolicy;

/**
 * Mapper of ROS2 QOS policies to DDS QOS policies.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class DdsQosMapper {

    public DurabilityType asDds(QosDurability qos) {
        return switch (qos) {
            case TRANSIENT_LOCAL -> DurabilityType.TRANSIENT_LOCAL_DURABILITY_QOS;
            case VOLATILE -> DurabilityType.VOLATILE_DURABILITY_QOS;
        };
    }

    public ReliabilityType asDds(QosReliability qos) {
        return switch (qos) {
            case BEST_EFFORT -> ReliabilityType.BEST_EFFORT;
            case RELIABLE -> ReliabilityType.RELIABLE;
        };
    }

    public SubscriberQosPolicy asDds(SubscriberQos qos) {
        return new SubscriberQosPolicy(asDds(qos.qosReliability()), asDds(qos.qosDurability()));
    }

    public PublisherQosPolicy asDds(PublisherQos qos) {
        return new PublisherQosPolicy(asDds(qos.qosReliability()), asDds(qos.qosDurability()));
    }
}
