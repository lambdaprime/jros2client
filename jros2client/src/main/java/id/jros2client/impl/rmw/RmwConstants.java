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

import pinorobotics.rtpstalk.WriterSettings;
import pinorobotics.rtpstalk.qos.DurabilityType;
import pinorobotics.rtpstalk.qos.PublisherQosPolicy;
import pinorobotics.rtpstalk.qos.ReliabilityType;

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
     * In FastDDS pushMode <a
     * href="https://github.com/eProsima/Fast-DDS/blob/1a070975177802b62efed52cd62557be7a6c3eb0/include/fastdds/rtps/writer/RTPSWriter.h#L512">enabled
     * by default</a> Looking at <a href="https://github.com/ros2/rmw_fastrtps.git">ROS2 RMW</a> the
     * pushMode setting is not being changed. <a href="http://design.ros2.org/articles/qos.html">ROS
     * official document</a> does not mention pushMode. All this points that ROS2 by default has
     * pushMode enabled.
     *
     * <p>To more align with ROS2 default behavior in <b>jros2client</b> the pushMode is enabled by
     * default
     */
    boolean DEFAULT_PUSHMODE_ENABLED = true;

    /** Publisher settings */
    WriterSettings DEFAULT_WRITER_SETTINGS = new WriterSettings(DEFAULT_PUSHMODE_ENABLED);
}
