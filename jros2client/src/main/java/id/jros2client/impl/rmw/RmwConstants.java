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

import pinorobotics.rtpstalk.qos.DurabilityType;
import pinorobotics.rtpstalk.qos.PublisherQosPolicy;
import pinorobotics.rtpstalk.qos.ReliabilityType;
import pinorobotics.rtpstalk.qos.SubscriberQosPolicy;

/**
 * @author lambdaprime intid@protonmail.com
 */
public interface RmwConstants {

    /**
     * From http://design.ros2.org/articles/qos.html:
     *
     * <p>In order to make the transition from ROS 1 to ROS 2, exercising a similar network behavior
     * is desirable. By default, publishers and subscriptions are reliable in ROS 2, have volatile
     * durability, and keep last history.
     */
    PublisherQosPolicy DEFAULT_PUBLISHER_QOS =
            new PublisherQosPolicy(
                    ReliabilityType.RELIABLE, DurabilityType.VOLATILE_DURABILITY_QOS);

    /**
     * From http://design.ros2.org/articles/qos.html:
     *
     * <p>In order to make the transition from ROS 1 to ROS 2, exercising a similar network behavior
     * is desirable. By default, publishers and subscriptions are reliable in ROS 2, have volatile
     * durability, and keep last history.
     */
    SubscriberQosPolicy DEFAULT_SUBSCRIBER_QOS =
            new SubscriberQosPolicy(
                    ReliabilityType.RELIABLE, DurabilityType.VOLATILE_DURABILITY_QOS);
}
