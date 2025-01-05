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
package id.jros2client.qos;

import id.xfunction.XJsonStringBuilder;

/**
 * Quality of Service policy definition for a ROS Publisher
 *
 * @author lambdaprime intid@protonmail.com
 */
public record PublisherQos(QosReliability qosReliability, QosDurability qosDurability) {

    public static final QosReliability DEFAULT_PUBLISHER_QOS_RELIABILITY = QosReliability.RELIABLE;
    public static final QosDurability DEFAULT_PUBLISHER_QOS_DURABILITY = QosDurability.VOLATILE;

    /**
     * In order to make the transition from ROS 1 to ROS 2, exercising a similar network behavior is
     * desirable. By default, publishers and subscriptions are reliable in ROS 2, have volatile
     * durability, and keep last history.
     *
     * @see <a href="http://design.ros2.org/articles/qos.html">ROS 2 Quality of Service policies</a>
     */
    public static final PublisherQos DEFAULT_PUBLISHER_QOS =
            new PublisherQos(DEFAULT_PUBLISHER_QOS_RELIABILITY, DEFAULT_PUBLISHER_QOS_DURABILITY);

    public PublisherQos(QosReliability qosReliability) {
        this(qosReliability, DEFAULT_PUBLISHER_QOS_DURABILITY);
    }

    public PublisherQos(QosDurability qosDurability) {
        this(DEFAULT_PUBLISHER_QOS_RELIABILITY, qosDurability);
    }

    @Override
    public String toString() {
        XJsonStringBuilder builder = new XJsonStringBuilder(this);
        builder.append("qosReliability", qosReliability);
        builder.append("qosDurability", qosDurability);
        return builder.toString();
    }
}
