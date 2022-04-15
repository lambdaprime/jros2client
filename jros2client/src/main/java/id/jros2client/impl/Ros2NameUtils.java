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
package id.jros2client.impl;

import id.jrosclient.utils.RosNameUtils;
import id.xfunction.Preconditions;
import java.nio.file.Paths;

/** @author lambdaprime intid@protonmail.com */
public class Ros2NameUtils {

    private RosNameUtils rosNameUtils;

    public Ros2NameUtils(RosNameUtils rosNameUtils) {
        this.rosNameUtils = rosNameUtils;
    }

    /**
     * Returns DDS topic name
     *
     * @see <a
     *     href="https://design.ros2.org/articles/topic_and_service_names.html#ros-specific-namespace-prefix">Topic
     *     and Service name mapping to DDS</a>
     */
    public String toDdsTopic(String topicName) {
        topicName = rosNameUtils.toAbsoluteName(topicName);
        return "rt" + topicName;
    }

    /**
     * Unfortunately message type conversion between ROS and DDS is not documented yet. May be it is
     * intentionally to keep it as implementation detail. Here we construct message type name solely
     * based on empirical data based on how ROS does it.
     */
    public String toDdsMessageType(String topicName) {
        var path = Paths.get(topicName);
        Preconditions.equals(2, path.getNameCount(), "");
        return String.format("%s::msg::dds_::%s_", path.getName(0), path.getName(1));
    }
}
