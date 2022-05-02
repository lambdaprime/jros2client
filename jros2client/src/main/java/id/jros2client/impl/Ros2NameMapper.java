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
import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadataAccessor;
import id.xfunction.Preconditions;
import java.nio.file.Paths;

/**
 * Mapper for ROS names.
 *
 * <p>ROS naming differs between ROS versions
 * https://design.ros2.org/articles/topic_and_service_names.html on top of that it differs between
 * the ROS topics, services, actions, etc.
 *
 * <p>Each <b>jrosclient</b> implementation already provides their own Name Mapper for this
 * interface so that users don't have to implement it.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class Ros2NameMapper {

    private MessageMetadataAccessor metadataAccessor = new MessageMetadataAccessor();
    private RosNameUtils rosNameUtils;

    public Ros2NameMapper(RosNameUtils rosNameUtils) {
        this.rosNameUtils = rosNameUtils;
    }

    /**
     * Returns DDS topic name
     *
     * @see <a
     *     href="https://design.ros2.org/articles/topic_and_service_names.html#ros-specific-namespace-prefix">Topic
     *     and Service name mapping to DDS</a>
     */
    public <M extends Message> String asFullyQualifiedTopicName(
            Class<M> messageClass, String topicName) {
        topicName = rosNameUtils.toAbsoluteName(topicName);
        var interfaceType = metadataAccessor.getInterfaceType(messageClass);
        return switch (interfaceType) {
                    case MESSAGE -> "rt";
                    case ACTION -> "rq";
                    default -> throw new UnsupportedOperationException(
                            "ROS interface type " + interfaceType);
                }
                + topicName;
    }

    /**
     * Unfortunately message type conversion between ROS and DDS is not documented yet. May be it is
     * intentionally to keep it as implementation detail. Here we construct message type name solely
     * based on empirical data based on how ROS does it.
     */
    public <M extends Message> String asFullyQualifiedMessageName(Class<M> messageClass) {
        var messageName = metadataAccessor.getName(messageClass);
        var path = Paths.get(messageName);
        Preconditions.equals(
                2, path.getNameCount(), "Message name format is invalid: " + messageName);
        // action_tutorials_interfaces::action::dds_::Fibonacci_SendGoal_Request_
        var interfaceType = metadataAccessor.getInterfaceType(messageClass);
        return switch (interfaceType) {
            case MESSAGE -> String.format("%s::msg::dds_::%s_", path.getName(0), path.getName(1));
            case ACTION -> String.format(
                    "%s::action::dds_::%s_",
                    path.getName(0), asDdsName(path.getName(1).toString()));
            default -> throw new UnsupportedOperationException(
                    "ROS interface type " + interfaceType);
        };
    }

    private Object asDdsName(String name) {
        var ddsName = name.replace("ActionGoal", "");
        Preconditions.isTrue(
                ddsName.length() < name.length(),
                "Message definition name " + name + " should end with ActionGoal postfix");
        return ddsName;
    }
}
